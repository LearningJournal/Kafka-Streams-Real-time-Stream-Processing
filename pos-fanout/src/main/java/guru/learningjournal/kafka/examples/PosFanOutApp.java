/*
 * Copyright (c) 2018. Prashant Kumar Pandey
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

public class PosFanOutApp {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, FanOutConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, FanOutConfigs.bootstrapServers);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, PosInvoice> KS0 = builder.stream(FanOutConfigs.posTopicName,
            Consumed.with(PosSerdes.String(), PosSerdes.PosInvoice()));

        //Requirement 1 - Produce to shipment
        KStream<String, PosInvoice> KS1 = KS0.filter((key, value) ->
            value.getDeliveryType()
                .equalsIgnoreCase(FanOutConfigs.DELIVERY_TYPE_HOME_DELIVERY));

        KS1.to(FanOutConfigs.shipmentTopicName,
            Produced.with(PosSerdes.String(), PosSerdes.PosInvoice()));

        //Requirement 2 - Produce to loyaltyHadoopRecord
        KStream<String, PosInvoice> KS3 = KS0.filter((key, value) ->
            value.getCustomerType()
                .equalsIgnoreCase(FanOutConfigs.CUSTOMER_TYPE_PRIME));

        KStream<String, Notification> KS4 = KS3.mapValues(
            invoice -> RecordBuilder.getNotification(invoice)
        );

        KS4.to(FanOutConfigs.notificationTopic,
            Produced.with(PosSerdes.String(), PosSerdes.Notification()));

        //Requirement 3 - Produce to Hadoop
        KStream<String, PosInvoice> KS6 = KS0.mapValues(
            invoice -> RecordBuilder.getMaskedInvoice(invoice)
        );

        KStream<String, HadoopRecord> KS7 = KS6.flatMapValues(
            invoice -> RecordBuilder.getHadoopRecords(invoice)
        );

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
