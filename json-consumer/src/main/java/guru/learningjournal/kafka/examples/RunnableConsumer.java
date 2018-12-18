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

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunnableConsumer implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer<String, JsonNode> consumer;
    private final List<String> topics;
    private int consumerID;
    private static final String kafkaConfig = "/kafka.properties";

    private ObjectCodec objectMapper;

    RunnableConsumer(int id, String groupID, List<String> topics) {
        InputStream kafkaConfigStream;
        this.topics = topics;
        this.consumerID = id;
        objectMapper = new ObjectMapper();
        Properties properties = new Properties();

        try {
            kafkaConfigStream = ClassLoader.class.getResourceAsStream(kafkaConfig);
            properties.load(kafkaConfigStream);
            properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "JsonConsumer-" + id);
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
            //Set autocommit to false so you can execute it again for the same set of messages
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        } catch (IOException e) {
            logger.error("Can't read kafka config");
            throw new RuntimeException(e);
        }
        this.consumer = new KafkaConsumer<>(properties);
    }

    @Override
    public void run() {
        try {
            logger.info("Staring consumer " + consumerID);
            consumer.subscribe(topics);
            while (!closed.get()) {
                ConsumerRecords<String, JsonNode> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, JsonNode> record : records) {
                    System.out.println(objectMapper.treeToValue(record.value(), StockData.class));
                }
            }
        } catch (WakeupException e) {
            if (!closed.get()) throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            logger.info("Closing consumer " + consumerID);
            consumer.close();
        }

    }

    void shutdown() {
        closed.set(false);
        consumer.wakeup();
    }
}
