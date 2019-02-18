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
import guru.learningjournal.kafka.examples.types.UserDetails;
import guru.learningjournal.kafka.examples.types.UserLogin;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class LastLoginDemo {
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

        KTable<String, UserDetails> userDetailsKTable = streamsBuilder.table(
            AppConfigs.userMasterTopic,
            Consumed.with(AppSerdes.String(), AppSerdes.UserDetails())
        );

        KTable<String, UserLogin> userLoginKTable = streamsBuilder.table(
            AppConfigs.lastLoginTopic,
            Consumed.with(AppSerdes.String(), AppSerdes.UserLogin())
        );

        userLoginKTable.join(userDetailsKTable,
            (userLogin, userDetails) -> {
                userDetails.setLastLogin(userLogin.getCreatedTime());
                return userDetails;
            }
        ).toStream().to(AppConfigs.userMasterTopic,
            Produced.with(AppSerdes.String(), AppSerdes.UserDetails()));

        userDetailsKTable.toStream().foreach(
            (k, userDetails) -> logger.info(
                "Key = " + k + " Value = " + userDetails
            )
        );


        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), properties);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}