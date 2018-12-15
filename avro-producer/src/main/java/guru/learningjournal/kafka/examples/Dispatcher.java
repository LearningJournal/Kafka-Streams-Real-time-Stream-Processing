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

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * A runnable class to dispatch a set of messages to Kafka producer
 */
public class Dispatcher implements Runnable {
    private final KafkaProducer<String, Object> producer;
    private final String topicName;
    private final String messageKey;
    private final List<Object> dataList;
    private static final Logger logger = LogManager.getLogger();

    /**
     * A dispatcher thread takes a kafka producer and send a batch of messages to the given topic
     *
     * @param producer   A valid producer instance
     * @param topicName  Name of the Kafka Topic
     * @param messageKey Message key for the entire batch
     * @param dataList   List of message Objects
     */
    Dispatcher(KafkaProducer<String, Object> producer, String topicName, String messageKey, List<Object> dataList) {
        this.producer = producer;
        this.topicName = topicName;
        this.messageKey = messageKey;
        this.dataList = dataList;
    }

    @Override
    public void run() {
        int messageCounter = 1;
        String producerName = Thread.currentThread().getName();

        logger.trace("Starting Producer thread" + producerName);
        for (Object data : dataList) {
            producer.send(new ProducerRecord<>(topicName, messageKey, data));
            messageCounter++;
        }
        logger.trace("Finished Producer thread" + producerName + " sent " + messageCounter + " messages");
    }

}