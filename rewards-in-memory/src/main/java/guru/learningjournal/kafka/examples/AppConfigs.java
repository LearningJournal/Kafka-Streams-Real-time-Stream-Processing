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

/**
 * Configurations for the PosFanOutApp
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
class AppConfigs {

    final static String applicationID = "RewardsApp";
    final static String bootstrapServers = "localhost:9092";
    final static String posTopicName = "pos";
    final static String notificationTopic = "loyalty";
    final static String CUSTOMER_TYPE_PRIME = "PRIME";
    final static Double LOYALTY_FACTOR = 0.02;
    final static String REWARDS_STORE_NAME = "CustomerRewardsStore";
}
