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

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.learningjournal.kafka.examples.common.AppConfigs;
import guru.learningjournal.kafka.examples.common.AppSerdes;
import guru.learningjournal.kafka.examples.common.Top3NewsTypes;
import guru.learningjournal.kafka.examples.types.AdClick;
import guru.learningjournal.kafka.examples.types.AdInventories;
import guru.learningjournal.kafka.examples.types.ClicksByNewsType;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Demo KStream-GlobalKTable join and TopN aggregation
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class Top3NewsTypesDemo {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,
            AppConfigs.applicationID);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
            AppConfigs.bootstrapServers);
        properties.put(StreamsConfig.STATE_DIR_CONFIG,
            AppConfigs.stateStoreName);

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //Global table for all inventories
        GlobalKTable<String, AdInventories> adInventoriesGlobalKTable =
            streamsBuilder.globalTable(
                AppConfigs.inventoryTopic,
                Consumed.with(AppSerdes.String(),
                    AppSerdes.AdInventories())
            );

        //Stream of Clicks
        KStream<String, AdClick> clicksKStream =
            streamsBuilder.stream(AppConfigs.clicksTopic,
                Consumed.with(AppSerdes.String(),
                    AppSerdes.AdClick())
            );

        //Stream of Clicked Inventories
        KStream<String, AdInventories> clickedInventoryKStream =
            clicksKStream.join(
                adInventoriesGlobalKTable,
                (clickKey, clickValue) -> clickKey,
                (adClick, adInventory) -> adInventory
            );

        //Group clicked inventories on NewsType and count
        //You will get clicks by news Types
        KTable<String, Long> clicksByNewsTypeKTable =
            clickedInventoryKStream.groupBy(
                (inventoryID, inventoryValue) -> inventoryValue.getNewsType(),
                Grouped.with(AppSerdes.String(), AppSerdes.AdInventories())
            ).count();

        //The clicksByNewsType is exhaustive and distributed
        //We need to compute the top 3 only
        //In order to do that, we must following
        //1. Bring all clicksByNewsType to a single partition
        //2. Sort them by clicks and take only top 3
        //There are two steps to achieve this.
        //1. Group them on a common key to bring it to a single partition
        //2. Simulate Sort()+Top(3) using a custom aggregation
        KTable<String, Top3NewsTypes> top3NewsTypesKTable =
            clicksByNewsTypeKTable.groupBy(
                (k_newsType, v_clickCount) -> {
                    ClicksByNewsType value = new ClicksByNewsType();
                    value.setNewsType(k_newsType);
                    value.setClicks(v_clickCount);
                    return KeyValue.pair(AppConfigs.top3AggregateKey, value);
                },
                Grouped.with(AppSerdes.String(), AppSerdes.ClicksByNewsType())
            ).aggregate(Top3NewsTypes::new,
                (k, newClickByNewsType, aggTop3NewType) -> {
                    aggTop3NewType.add(newClickByNewsType);
                    return aggTop3NewType;
                },
                (k, oldClickByNewsType, aggTop3NewType) -> {
                    aggTop3NewType.remove(oldClickByNewsType);
                    return aggTop3NewType;
                },
                Materialized.<String, Top3NewsTypes, KeyValueStore<Bytes, byte[]>>
                    as("top3-clicks")
                    .withKeySerde(AppSerdes.String())
                    .withValueSerde(AppSerdes.Top3NewsTypes()));

        top3NewsTypesKTable.toStream().foreach((k, v) -> {
            try {
                logger.info("k=" + k + " v= " + v.getTop3Sorted());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), properties);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
