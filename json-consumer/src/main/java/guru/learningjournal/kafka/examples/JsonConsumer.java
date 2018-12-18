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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Kafka consumer demo to read Json serialized messages from Kafka
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class JsonConsumer {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Application entry point
     *
     * @param args topicName and groupName
     */
    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("Please provide command line arguments: NoOfConsumers groupName topicNames");
            System.exit(-1);
        }
        int noOfConsumers = new Integer(args[0]);
        String groupName = args[1];
        String[] topicNames = Arrays.copyOfRange(args, 2, args.length);
        logger.info("Starting Kafka Json consumer...");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        final List<RunnableConsumer> consumers = new ArrayList<>();
        for (int i = 0; i < noOfConsumers; i++) {
            RunnableConsumer consumer = new RunnableConsumer(i, groupName, Arrays.asList(topicNames));
            consumers.add(consumer);
            executor.submit(consumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Consumers...");
            for (RunnableConsumer c : consumers) {
                c.shutdown();
            }
            logger.info("Closing Application");
            executor.shutdown();
            try {
                executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

    }
}
