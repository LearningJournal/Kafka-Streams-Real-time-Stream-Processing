/*
 * Copyright (c) 2019. Prashant Kumar Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package guru.learningjournal.kafka.examples;

import guru.learningjournal.kafka.examples.types.HadoopRecord;
import guru.learningjournal.kafka.examples.types.Notification;
import guru.learningjournal.kafka.examples.types.PosInvoice;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Transform and FanOut Invoices to different topics for other services
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

@SuppressWarnings("unchecked")
public class PosFanOutApp {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, FanOutConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, FanOutConfigs.bootstrapServers);

        StreamsBuilder builder = new StreamsBuilder();
        KStream KS0 = builder.stream(FanOutConfigs.posTopicName,
                Consumed.with(PosSerdes.String(), PosSerdes.PosInvoice()));

        //Requirement 1 - Produce to shipment
        KStream KS1 = KS0.filter((Predicate<String, PosInvoice>) (key, value) ->
                value.getDeliveryType().toString()
                        .equalsIgnoreCase(FanOutConfigs.DELIVERY_TYPE_HOME_DELIVERY));

        KS1.to(FanOutConfigs.shipmentTopicName,
                Produced.with(PosSerdes.String(), PosSerdes.PosInvoice()));

        //Requirement 2 - Produce to loyaltyHadoopRecord
        KStream KS3 = KS0.filter((Predicate<String, PosInvoice>) (key, value) ->
                value.getCustomerType().toString()
                        .equalsIgnoreCase(FanOutConfigs.CUSTOMER_TYPE_PRIME));

        KStream KS4 = KS3.mapValues((ValueMapper<PosInvoice, Notification>)
                RecordBuilder::getNotification);

        KS4.to(FanOutConfigs.notificationTopic,
                Produced.with(PosSerdes.String(), PosSerdes.Notification()));

        //Requirement 3 - Produce to Hadoop
        KStream KS6 = KS0.mapValues((ValueMapper<PosInvoice, PosInvoice>)
                RecordBuilder::getMaskedInvoice);

        KStream KS7 = KS6.flatMapValues((ValueMapper<PosInvoice, Iterable<HadoopRecord>>)
                RecordBuilder::getHadoopRecords);

        KS7.to(FanOutConfigs.hadoopTopic,
                Produced.with(PosSerdes.String(), PosSerdes.HadoopRecord()));

        Topology posFanOutTopology = builder.build();

        logger.info("Starting the following topology");
        logger.info(posFanOutTopology.describe().toString());

        KafkaStreams myStream = new KafkaStreams(posFanOutTopology, props);
        myStream.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Stream");
            myStream.close();
        }));
    }
}