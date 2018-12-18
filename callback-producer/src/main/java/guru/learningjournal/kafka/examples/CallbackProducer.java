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

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Properties;

/**
 * A Kafka producer that sends numEvents (# of messages) to a given topicName
 * Producer demonstrates callback implementation
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class CallbackProducer {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Applications entry point
     *
     * @param args topicName (name of the Kafka topic) numEvents (# of messages)
     */
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Please provide command line arguments: topicName numEvents");
            System.exit(-1);
        }
        String topicName = args[0];
        int numEvents = Integer.valueOf(args[1]);
        logger.debug("topicName=" + topicName + ", numEvents=" + numEvents);

        logger.trace("Creating Kafka Producer...");
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "CallbackProducer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
        logger.trace("Start sending messages...");

        for (int j = 1; j <= numEvents; j++) {
            int i = j;
            producer.send(new ProducerRecord<>(topicName, i, "Simple Message-" + i),
                    (recordMetadata, e) -> {
                        if (e != null)
                            logger.error("Error sending message with key " + i + " Error - " + e.getMessage());
                        else
                            logger.info("Message " + i + " persisted with offset " + recordMetadata.offset()
                                    + " and timestamp on " + new Timestamp(recordMetadata.timestamp()));
                    });
        }

        logger.info("Finished Application - Closing Kafka Producer.");
        producer.close();
    }
}
