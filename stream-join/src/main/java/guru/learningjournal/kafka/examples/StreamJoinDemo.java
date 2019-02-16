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

import guru.learningjournal.kafka.examples.common.AppConfigs;
import guru.learningjournal.kafka.examples.common.AppSerdes;
import guru.learningjournal.kafka.examples.common.AppTimeExtractor;
import guru.learningjournal.kafka.examples.types.PaymentConfirmation;
import guru.learningjournal.kafka.examples.types.PaymentRequest;
import guru.learningjournal.kafka.examples.types.TransactionStatus;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Properties;

/**
 * Demo application for stream-stream join
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

public class StreamJoinDemo {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,
            AppConfigs.applicationID);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
            AppConfigs.bootstrapServers);
        properties.put(StreamsConfig.STATE_DIR_CONFIG,
            AppConfigs.stateStoreName);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, PaymentRequest> KS0 = streamsBuilder.stream(
            AppConfigs.paymentRequestTopicName,
            Consumed.with(AppSerdes.String(), AppSerdes.PaymentRequest())
                .withTimestampExtractor(AppTimeExtractor.PaymentRequest())
        );

        KStream<String, PaymentConfirmation> KS1 = streamsBuilder.stream(
            AppConfigs.paymentConfirmationTopicName,
            Consumed.with(AppSerdes.String(), AppSerdes.PaymentConfirmation())
                .withTimestampExtractor(AppTimeExtractor.PaymentConfirmation())
        );

        //Implement stream-stream inner Join
        KStream<String, TransactionStatus> KS3 = KS0.join(KS1,
            (paymentRequest, paymentConfirmation) ->
                new TransactionStatus()
                    .withTransactionID(paymentRequest.getTransactionID())
                    .withStatus((paymentRequest.getOTP()
                        .equals(paymentConfirmation.getOTP()) ? "Success" : "Failure")),
            JoinWindows.of(Duration.ofMinutes(5)),
            Joined.with(AppSerdes.String(),
                AppSerdes.PaymentRequest(),
                AppSerdes.PaymentConfirmation())
        );

        KS3.foreach((k, v) ->
            logger.info("Transaction ID = " + k +
                " Status = " + v.getStatus())
        );

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), properties);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
