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

import guru.learningjournal.kafka.examples.types.SimpleInvoice;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Properties;

//import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;
//import static org.apache.kafka.streams.kstream.Suppressed.untilWindowCloses;

/**
 * Demo application for Windowed Aggregates
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class CountingWindowApp {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, SimpleInvoice> KS0 = streamsBuilder.stream(AppConfigs.posTopicName,
            Consumed.with(AppSerdes.String(), AppSerdes.SimpleInvoice())
                .withTimestampExtractor(new InvoiceTimeExtractor())
        );

        KGroupedStream<String, SimpleInvoice> KS1 = KS0.groupByKey(
            Grouped.with(AppSerdes.String(),
                AppSerdes.SimpleInvoice()));

        TimeWindowedKStream<String, SimpleInvoice> KS2 = KS1.windowedBy(
            TimeWindows.of(Duration.ofMinutes(5))
            //.grace(Duration.ofMillis(100))
        );

        KTable<Windowed<String>, Long> KT3 = KS2.count(
            //Materialized is not needed if you don't want to override defaults
            Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("invoice-count")
            //.withRetention(Duration.ofHours(6))
        );

        //Suppress is only available in 2.1, Checkout 2.1 branch
        //.suppress(untilWindowCloses(unbounded()));


        KT3.toStream().foreach(
            (kWindowed, v) -> logger.info(
                "StoreID: " + kWindowed.key() +
                    " Window start: " +
                    Instant.ofEpochMilli(kWindowed.window().start())
                        .atOffset(ZoneOffset.UTC) +
                    " Window end: " +
                    Instant.ofEpochMilli(kWindowed.window().end())
                        .atOffset(ZoneOffset.UTC) +
                    " Count: " + v +
                    " Window#: " + kWindowed.window().hashCode()

            ));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
