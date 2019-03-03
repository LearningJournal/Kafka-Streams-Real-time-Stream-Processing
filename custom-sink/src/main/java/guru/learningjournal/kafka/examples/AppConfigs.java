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

import java.util.regex.Pattern;

/**
 * Application Config
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

class AppConfigs {
    final static String applicationID = "CustomSinkApp";
    final static String bootstrapServers = "localhost:9092";
    final static Pattern topicPattern = Pattern.compile("topic\\d");
    final static String topicTableMap = "table-map";
    final static String stateStoreLocation = "logs/state-store";
    final static String targetTableField = "TargetTable";
}
