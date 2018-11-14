/*
 * Copyright (c) 2018. Prashant Kumar Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package guru.learningjournal.kafka.examples;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * A simple producer that sends 10 messages to the given topic.
 * Each message is an Integer Key and a String value pair
 */

public class SimpleProducer extends Thread {
    private final String topic;
    private final Integer messageNumber;
    private final KafkaProducer<Integer, String> producer;

    /**
     * A simple producer that sends 10 messages to the given topic.
     * Each message is an Integer Key and a String value pair
     *
     * @param topic         Name of the topic
     * @param messageNumber Starting value of the Message Key
     */

    public SimpleProducer(String topic, Integer messageNumber) {
        //Producer Configs
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG,
                "SimpleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        //Create Producer Instance
        producer = new KafkaProducer<Integer, String>(props);
        this.topic = topic;
        this.messageNumber = messageNumber;
    }

    public void run() {
        int stopCounter = messageNumber;
        //Send 10 messages
        while (stopCounter < messageNumber + 10) {
            String messageStr = "Simple_Message_" + stopCounter;
            try {
                producer.send(new ProducerRecord<>(topic,
                        messageNumber,
                        messageStr)).get();
                System.out.println("Sent Message: " +
                        stopCounter + " -> " +
                        messageStr);
            } catch (InterruptedException |
                    ExecutionException e) {
                e.printStackTrace();
            }
            ++stopCounter;
        }
    }
}
