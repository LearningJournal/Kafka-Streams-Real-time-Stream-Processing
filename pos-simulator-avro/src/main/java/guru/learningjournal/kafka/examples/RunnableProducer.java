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

import guru.learningjournal.kafka.examples.datagenerator.InvoiceGenerator;
import guru.learningjournal.kafka.examples.types.PosInvoice;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunnableProducer implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private final AtomicBoolean stopper = new AtomicBoolean(false);
    private final KafkaProducer<String, PosInvoice> producer;
    private final String topicName;
    private InvoiceGenerator invoiceGenerator;
    private int produceSpeed;
    private int id;

    RunnableProducer(int id, KafkaProducer<String, PosInvoice> producer, String topicName, int produceSpeed) {
        this.topicName = topicName;
        this.producer = producer;
        this.invoiceGenerator = InvoiceGenerator.getInstance();
        this.produceSpeed = produceSpeed;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            logger.info("Starting producer " + id);
            while (!stopper.get()) {
                PosInvoice posInvoice = invoiceGenerator.getNextInvoice();
                RecordMetadata metadata = producer.send(new ProducerRecord<>(topicName, posInvoice.getStoreID().toString(), posInvoice)).get();
                logger.info("Message persisted with offset " + metadata.offset()
                        + " and timestamp on " + new Timestamp(metadata.timestamp()));
                Thread.sleep(produceSpeed);
            }

        } catch (Exception e) {
            logger.error("Exception in Producer " + id,e);
            throw new RuntimeException(e);
        }
    }

    void shutdown() {
        logger.info("Shutting down producer " + id);
        stopper.set(true);

    }
}