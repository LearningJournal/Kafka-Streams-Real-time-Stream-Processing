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
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Transform and FanOut Invoices to Notification and also aggregate rewards
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
        props.put(StreamsConfig.STATE_DIR_CONFIG, "state-store");

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, PosInvoice> KS0 = streamsBuilder.stream(AppConfigs.posTopicName,
            Consumed.with(PosSerdes.String(), PosSerdes.PosInvoice()));

        KStream<String, PosInvoice> KS1 = KS0.filter(
            (key, value) -> value.getCustomerType().equalsIgnoreCase(AppConfigs.CUSTOMER_TYPE_PRIME)
        );

        KStream<String, Notification> KS2 = KS1.map(
            (key, invoice) -> new KeyValue<>(
                invoice.getCustomerCardNo(),
                Notifications.getNotificationFrom(invoice)
            )
        );

        KGroupedStream<String, Notification> KGS3 = KS2.groupByKey(
            Grouped.with(
                PosSerdes.String(),
                PosSerdes.Notification()
            )
        );

        KTable<String, Notification> KTS4 = KGS3.reduce(
            (aggValue, newValue) -> {
                newValue.setTotalLoyaltyPoints(newValue.getEarnedLoyaltyPoints() +
                    aggValue.getTotalLoyaltyPoints());
                return newValue;
            }
        );

        KTS4.toStream().to(AppConfigs.notificationTopic,
            Produced.with(
                PosSerdes.String(),
                PosSerdes.Notification())
        );

        logger.info("Starting Kafka Streams");
        KafkaStreams myStream = new KafkaStreams(streamsBuilder.build(), props);
        myStream.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Stream");
            myStream.close();
        }));
    }
}

