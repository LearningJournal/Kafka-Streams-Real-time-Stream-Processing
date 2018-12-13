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
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * A Kafka producer that sends numEvents (# of messages) to a given topicName
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class PartitionedProducer {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Applications entry point
     *
     * @param args topicName (name of the Kafka topic) numEvents (# of messages)
     */
    public static void main(String[] args) {
        RecordMetadata metadata;
        String topicName;
        int numEvents;

        if (args.length != 2) {
            System.out.println("Please provide command line arguments: topicName numEvents");
            System.exit(-1);
        }
        topicName = args[0];
        numEvents = Integer.valueOf(args[1]);
        logger.info("Starting PartitionedProducer...");
        logger.debug("topicName=" + topicName + ", numEvents=" + numEvents);

        logger.trace("Creating Kafka Producer...");
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "PartitionedProducer");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, OddEvenPartitioner.class.getName());

        try (KafkaProducer<Integer, String> producer = new KafkaProducer<>(props)) {
            logger.trace("Start sending messages...");
            for (int i = 1; i <= numEvents; i++) {
                metadata = producer.send(new ProducerRecord<>(topicName, i, "Simple Message-" + i)).get();
                logger.info("Message " + i + " persisted with offset " + metadata.offset()
                        + " in partition " + metadata.partition());
            }
        } catch (KafkaException | InterruptedException | ExecutionException e) {
            logger.error("Exception occurred - Check log for more details.\n" + e.getMessage());
            System.exit(-1);
        } finally {
            logger.info("Finished PartitionedProducer - Closing Kafka Producer.");
        }

    }
}
