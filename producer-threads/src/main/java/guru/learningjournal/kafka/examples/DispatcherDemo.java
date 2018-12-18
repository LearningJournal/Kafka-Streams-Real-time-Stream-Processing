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
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Kafka producer to send events from a file.
 * You can start one thread for each file
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class DispatcherDemo {
    private static final Logger logger = LogManager.getLogger();
    private static final String kafkaConfig = "/kafka.properties";

    /**
     * Application entry point
     * You must provide the topic name and at least one event file
     *
     * @param args topicName (Name of the Kafka topic) list of files (list of files in the classpath)
     */
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Please provide command line arguments: topicName EventFiles");
            System.exit(-1);
        }

        String topicName = args[0];
        String[] eventFiles = Arrays.copyOfRange(args, 1, args.length);

        Properties properties = new Properties();
        try {
            InputStream configStream = ClassLoader.class.getResourceAsStream(kafkaConfig);
            properties.load(configStream);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        } catch (IOException e) {
            logger.error("Cannot open Kafka config " + kafkaConfig);
            throw new RuntimeException(e);
        }

        logger.trace("Starting dispatcher threads...");
        KafkaProducer<Integer, String> producer = new KafkaProducer<>(properties);
        Thread[] dispatchers = new Thread[eventFiles.length];
        for (int i = 0; i < eventFiles.length; i++) {
            dispatchers[i] = new Thread(new Dispatcher(producer, topicName, eventFiles[i]));
            dispatchers[i].start();
        }

        try {
            for (Thread t : dispatchers)
                t.join();
        } catch (InterruptedException e) {
            logger.error("Thread Interrupted ");
        } finally {
            producer.close();
            logger.info("Finished dispatcher demo - Closing Kafka Producer.");
        }
    }
}
