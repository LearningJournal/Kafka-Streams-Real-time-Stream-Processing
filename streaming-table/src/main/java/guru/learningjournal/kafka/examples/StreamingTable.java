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

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import java.time.Duration;
import java.util.Properties;

/**
 * Simple example to demonstrates Creating KTable
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class StreamingTable {
    public static void main(final String[] args) {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "StreamingTable");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "state-store");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
                WallclockTimestampExtractor.class.getName());

        //Uncomment to Enable record cache of size 10 MB.
        //props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);
        //Uncomment to Set commit interval to 1 second.
        //props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);

        StreamsBuilder streamBuilder = new StreamsBuilder();
        KTable<String, String> KT0 = streamBuilder.table("stock-tick");

        /*
        //Uncomment this block and comment next line to suppress
        KTable<String, String> KT1 = KT0.filter((key, value) -> key.contains("HDFCBANK"))
                .suppress(Suppressed.untilTimeLimit(
                        Duration.ofMinutes(5),
                        Suppressed.BufferConfig.maxBytes(1000000L).emitEarlyWhenFull())
                );
        */

        KTable<String, String> KT1 = KT0.filter((key, value) -> key.contains("HDFCBANK"));

        KStream<String, String> KS2 = KT1.toStream();
        KS2.peek((k, v) -> System.out.println("Key = " + k + " Value = " + v));

        KafkaStreams streams = new KafkaStreams(streamBuilder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}

