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

import guru.learningjournal.kafka.examples.types.PosInvoice;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Properties;

/**
 * Demo Timestamp Extractor
 * Comment withTimestampExtractor and execute with default timestamp extractor once
 * Uncomment withTimestampExtractor and use InvoiceTimeExtractor to see the difference
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

public class TimeCheckDemo {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);



        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, PosInvoice> KS0 = streamsBuilder.stream(AppConfigs.posTopicName,
                Consumed.with(PosSerdes.String(), PosSerdes.PosInvoice())
                        .withTimestampExtractor(new InvoiceTimeExtractor())
        );

        KS0.transformValues(() -> new ValueTransformer<PosInvoice, PosInvoice>() {
            private ProcessorContext context;

            @Override
            public void init(ProcessorContext processorContext) {
                this.context = processorContext;
            }

            @Override
            public PosInvoice transform(PosInvoice invoice) {
                logger.info("Invoice Time: " + new Timestamp(invoice.getCreatedTime()) +
                        " Event Time: " + new Timestamp(context.timestamp()));
                return invoice;
            }

            @Override
            public void close() {
            }
        });

        logger.info("Starting Kafka Streams");
        KafkaStreams myStream = new KafkaStreams(streamsBuilder.build(), props);
        myStream.start();

        Runtime.getRuntime().addShutdownHook(new Thread(myStream::close));
    }
}
