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

package guru.learningjournal.kafka.examples.common;

import guru.learningjournal.kafka.examples.serde.JsonDeserializer;
import guru.learningjournal.kafka.examples.serde.JsonSerializer;
import guru.learningjournal.kafka.examples.types.PaymentConfirmation;
import guru.learningjournal.kafka.examples.types.PaymentRequest;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.HashMap;
import java.util.Map;

/**
 * Application level Serdes
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class AppSerdes extends Serdes {

    static final class PaymentRequestSerde extends WrapperSerde<PaymentRequest> {
        PaymentRequestSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<PaymentRequest> PaymentRequest() {
        PaymentRequestSerde serde = new PaymentRequestSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("specific.class.name", PaymentRequest.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

    static final class PaymentConfirmationSerde extends WrapperSerde<PaymentConfirmation> {
        PaymentConfirmationSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<PaymentConfirmation> PaymentConfirmation() {
        PaymentConfirmationSerde serde = new PaymentConfirmationSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("specific.class.name", PaymentConfirmation.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

}
