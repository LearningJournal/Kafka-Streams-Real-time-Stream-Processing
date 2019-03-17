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

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.*;

import java.util.Properties;

public class StockFeedApp {
    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreLocation);

        Topology topology = new Topology();

        topology.addSource(
            AppConfigs.sourceName,
            AppSerdes.String().deserializer(),
            AppSerdes.StockTicker().deserializer(),
            AppConfigs.sourceTopicName);

        topology.addProcessor(
            StockProcessor.PROCESSOR_NAME,
            () -> new StockProcessor(),
            AppConfigs.sourceName);

        StoreBuilder storeBuilder = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(StockProcessor.STATE_STORE_NAME),
            AppSerdes.String(),
            AppSerdes.TickerStack()
        );

        topology.addStateStore(
            storeBuilder,
            StockProcessor.PROCESSOR_NAME);

        topology.addSink(AppConfigs.sinkName,
            AppConfigs.targetTopicName,
            AppSerdes.String().serializer(),
            AppSerdes.StockTicker().serializer(),
            StockProcessor.PROCESSOR_NAME);

        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
