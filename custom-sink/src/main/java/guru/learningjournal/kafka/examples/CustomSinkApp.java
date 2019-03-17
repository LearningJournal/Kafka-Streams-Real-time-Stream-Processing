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

import guru.learningjournal.kafka.examples.types.GenericRecord;
import guru.learningjournal.kafka.examples.types.TableMap;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Demo application for integrating Processor API with DSL
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class CustomSinkApp {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreLocation);

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //Global table for mappings
        GlobalKTable<String, TableMap> topicTableMapGlobalKTable = streamsBuilder.globalTable(
            AppConfigs.topicTableMap,
            Consumed.with(AppSerdes.String(),
                AppSerdes.TableMap()));

        //Stream of Records
        KStream<String, GenericRecord> recordKStream = streamsBuilder.stream(
            AppConfigs.topicPattern,
            Consumed.with(AppSerdes.String(),
                AppSerdes.GenericRecord())
        ).transform(() -> new RecordTransformer());

        //Join to get Target Table Name
        KStream<String, GenericRecord> joinedKStream = recordKStream.join(
            topicTableMapGlobalKTable,
            (keyGenericRecord, valueGenericRecord) -> keyGenericRecord,
            (valueGenericRecord, valueTableMap) -> {
                valueGenericRecord.setAdditionalProperty(
                    AppConfigs.targetTableField,
                    valueTableMap.getTargetTable());
                return valueGenericRecord;
            }
        );

        //Change key to target table name and cleanup record
        KStream<String, GenericRecord> sinkRecord = joinedKStream.selectKey(
            (k, v) -> {
                String newKey = v.getAdditionalProperties()
                    .get(AppConfigs.targetTableField);
                v.getAdditionalProperties().remove(AppConfigs.targetTableField);
                return newKey;
            }).peek((k, v) -> logger.info("Ready to Sink key= " + k + " value= " + v));

        //Sink to Target Database
        sinkRecord.process(() -> new SinkProcessor());

        //Start the stream and add a shutdown hook
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
