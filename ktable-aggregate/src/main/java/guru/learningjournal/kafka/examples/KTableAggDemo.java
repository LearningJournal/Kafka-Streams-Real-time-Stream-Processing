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

import guru.learningjournal.kafka.examples.types.DepartmentAggregate;
import guru.learningjournal.kafka.examples.types.Employee;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Properties;

/**
 * Demo application to compute aggregate for KTable
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class KTableAggDemo {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KTable<String, Employee> KS0 = streamsBuilder.table(AppConfigs.topicName,
            Consumed.with(AppSerdes.String(), AppSerdes.Employee()));

        KGroupedTable<String, Employee> KGS1 = KS0.groupBy(
            (k, v) -> KeyValue.pair(v.getDepartment(), v),
            Serialized.with(AppSerdes.String(), AppSerdes.Employee()));

        KTable<String, DepartmentAggregate> KT2 = KGS1.aggregate(
            //Initializer
            () -> new DepartmentAggregate()
                .withEmployeeCount(0)
                .withTotalSalary(0)
                .withAvgSalary(0D),
            //Adder
            (k, v, aggV) -> new DepartmentAggregate()
                .withEmployeeCount(aggV.getEmployeeCount() + 1)
                .withTotalSalary(aggV.getTotalSalary() + v.getSalary())
                .withAvgSalary((aggV.getTotalSalary() + v.getSalary())
                    / (aggV.getEmployeeCount() + 1D)),
            //subtractor
            (k, v, aggV) -> new DepartmentAggregate()
                .withEmployeeCount(aggV.getEmployeeCount() - 1)
                .withTotalSalary(aggV.getTotalSalary() - v.getSalary())
                .withAvgSalary((aggV.getTotalSalary() - v.getSalary())
                    / (aggV.getEmployeeCount() - 1D)),
            Materialized.<String, DepartmentAggregate, KeyValueStore<Bytes,
                byte[]>>as("dept-agg-store")
                .withValueSerde(AppSerdes.DepartmentAggregate())
        );

        KT2.toStream().foreach(
            (k, v) -> System.out.println("Key = " + k + " Value = " + v.toString()));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
