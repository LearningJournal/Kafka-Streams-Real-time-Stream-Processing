/*
 * Copyright (c) 2018. Prashant Kumar Pandey
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Kafka consumer demo to read Json serialized messages from Kafka
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class JsonConsumer {
    private static final Logger logger = LogManager.getLogger();
    private static final String kafkaConfig = "/kafka.properties";

    /**
     * Application entry point
     *
     * @param args topicName and groupName
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        final String topicName;
        final String groupName;
        InputStream kafkaConfigStream;
        ObjectMapper objectMapper = new ObjectMapper();

        if (args.length < 2) {
            System.out.println("Please provide command line arguments: topicName groupName");
            System.exit(-1);
        }
        topicName = args[0];
        groupName = args[1];
        logger.info("Starting Kafka avro consumer...");

        Properties properties = new Properties();
        try {
            kafkaConfigStream = ClassLoader.class.getResourceAsStream(kafkaConfig);
            properties.load(kafkaConfigStream);
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
            //Set autocommit to false so you can execute it again for the same set of messages
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        } catch (IOException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }

        try (final KafkaConsumer<String, JsonNode> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(Collections.singletonList(topicName));
            logger.info("Starting to consume...");
            while (true) {
                ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofMillis(100));
                try {
                    for (ConsumerRecord<String, JsonNode> record : records)
                        System.out.println(objectMapper.treeToValue(record.value(), StockData.class));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
