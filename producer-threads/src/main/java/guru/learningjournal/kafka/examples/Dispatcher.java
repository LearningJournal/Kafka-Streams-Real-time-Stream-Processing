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

import java.io.File;
import java.util.Scanner;

/**
 * Runnable Kafka message dispatcher
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class Dispatcher implements Runnable {

    private final KafkaProducer<Integer, String> producer;
    private final String topicName;
    private final String fileLocation;
    private static final Logger logger = LogManager.getLogger(Dispatcher.class);

    /**
     * A dispatcher thread takes a producer and sends Kafka messages to the given topic.
     * The Events are supplied in a file
     *
     * @param producer     A valid producer instance
     * @param topicName    Name of the Kafka topic
     * @param fileLocation File location containing events.
     *                     Location of file is relative to the class path.
     *                     each line in the file is considered an event.
     */
    Dispatcher(KafkaProducer<Integer, String> producer, String topicName, String fileLocation) {
        this.producer = producer;
        this.topicName = topicName;
        this.fileLocation = fileLocation;
    }

    @Override
    public void run() {
        logger.info("Start processing " + fileLocation + "...");
        File file = new File(fileLocation);
        int msgKey = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                producer.send(new ProducerRecord<>(topicName, msgKey, line));
                msgKey++;
            }
            logger.trace("Finished sending " + msgKey + " messages from " + fileLocation);
        } catch (Exception e) {
            logger.error("Exception in thread " + fileLocation);
            throw new RuntimeException(e);
        }
    }
}
