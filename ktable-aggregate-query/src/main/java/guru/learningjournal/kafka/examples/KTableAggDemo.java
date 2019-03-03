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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Demo application to compute aggregate for KTable
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class KTableAggDemo {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Please provide command line arguments: hostname port");
            System.exit(-1);
        }
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreLocation);

        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, hostName
            + ":"
            + portNumber);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KTable<String, Employee> KS0 = streamsBuilder.table(AppConfigs.topicName,
            Consumed.with(AppSerdes.String(), AppSerdes.Employee()));

        KGroupedTable<String, Employee> KGS1 = KS0.groupBy(
            (k, v) -> KeyValue.pair(v.getDepartment(), v),
            Grouped.with(AppSerdes.String(), AppSerdes.Employee()));

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
            //subtract
            (k, v, aggV) -> new DepartmentAggregate()
                .withEmployeeCount(aggV.getEmployeeCount() - 1)
                .withTotalSalary(aggV.getTotalSalary() - v.getSalary())
                .withAvgSalary((aggV.getTotalSalary() - v.getSalary())
                    / (aggV.getEmployeeCount() - 1D)),
            Materialized.<String, DepartmentAggregate, KeyValueStore<Bytes,
                byte[]>>as(AppConfigs.aggStateStoreName)
                .withKeySerde(AppSerdes.String())
                .withValueSerde(AppSerdes.DepartmentAggregate())
        );

        KT2.toStream().foreach(
            (k, v) -> System.out.println("Key = " + k + " Value = " + v.toString()));

        KafkaStreams streams = new KafkaStreams(
            streamsBuilder.build(), props);

        AppRestService queryServer = new AppRestService(
            streams,
            hostName,
            portNumber);

        streams.setStateListener((newState, oldState) -> {
            logger.info("State Changing to " + newState + " from " + oldState);
            queryServer.setActive(
                newState == KafkaStreams.State.RUNNING &&
                    oldState == KafkaStreams.State.REBALANCING
            );
        });

        streams.start();
        queryServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            queryServer.stop();
            streams.close();
        }));
    }
}
