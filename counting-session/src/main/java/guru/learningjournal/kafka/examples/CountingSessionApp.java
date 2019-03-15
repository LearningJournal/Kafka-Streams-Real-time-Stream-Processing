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

import guru.learningjournal.kafka.examples.common.AppConfigs;
import guru.learningjournal.kafka.examples.common.AppSerdes;
import guru.learningjournal.kafka.examples.common.AppTimestampExtractor;
import guru.learningjournal.kafka.examples.types.UserClicks;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.SessionStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class CountingSessionApp {
    private static final Logger logger = LogManager.getLogger();

    private static String utcTimeString(Long timestamp) {
        return DateTimeFormatter.ISO_DATE_TIME
            .format(Instant.ofEpochMilli(timestamp)
                .atZone(ZoneOffset.UTC)
            );
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, UserClicks> KS0 = streamsBuilder.stream(
            AppConfigs.posTopicName,
            Consumed.with(AppSerdes.String(), AppSerdes.UserClicks())
                .withTimestampExtractor(new AppTimestampExtractor())
        );

        KGroupedStream<String, UserClicks> KS1 = KS0.groupByKey(
            Grouped.with(AppSerdes.String(),
                AppSerdes.UserClicks()));

        SessionWindowedKStream<String, UserClicks> KS2 = KS1.windowedBy(
            SessionWindows.with(Duration.ofMinutes(5))
                .grace(Duration.ofMinutes(1))
        );

        KTable<Windowed<String>, Long> KT3 = KS2.count(
            //Materialized is not needed if you don't want to override defaults
            Materialized.<String, Long, SessionStore<Bytes, byte[]>>as("clicks-by-user-session")
        );

        KT3.toStream().foreach(
            (kWindowed, v) -> logger.info(
                "UserID: " + kWindowed.key() +
                    " Window Start: " + utcTimeString(kWindowed.window().start()) +
                    " Window End: " + utcTimeString(kWindowed.window().end()) +
                    " Count: " + v
            ));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
