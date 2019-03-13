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

import guru.learningjournal.kafka.examples.types.Notification;
import guru.learningjournal.kafka.examples.types.PosInvoice;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Transform and FanOut Invoices to different topics for other services
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class RewardsApp {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, PosInvoice> KS0 = streamsBuilder.stream(
            AppConfigs.posTopicName,
            Consumed.with(PosSerdes.String(),
                PosSerdes.PosInvoice()));

        KStream<String, PosInvoice> KS1 = KS0.filter((key, value) ->
            value.getCustomerType().equalsIgnoreCase(AppConfigs.CUSTOMER_TYPE_PRIME));

        KStream<String, PosInvoice> KS2 = KS1.through("rewards-intermediate",
            Produced.with(PosSerdes.String(),
                PosSerdes.PosInvoice(),
                new RewardsPartitioner()));

        StoreBuilder kvStoreBuilder = Stores.keyValueStoreBuilder(
            Stores.inMemoryKeyValueStore(AppConfigs.REWARDS_STORE_NAME),
            Serdes.String(),
            Serdes.Double()
        );

        streamsBuilder.addStateStore(kvStoreBuilder);

        KStream<String, Notification> KS3 = KS2.transformValues(
            RewardsTransformer::new,
            AppConfigs.REWARDS_STORE_NAME);

        KS3.to(AppConfigs.notificationTopic,
            Produced.with(PosSerdes.String(), PosSerdes.Notification()));

        logger.info("Starting Kafka Streams");
        KafkaStreams myStream = new KafkaStreams(streamsBuilder.build(), props);
        myStream.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Stream");
            myStream.close();
        }));
    }
}

