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

import guru.learningjournal.kafka.examples.types.SimpleInvoice;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

/**
 * Extract timestamp from Invoice field
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

    public class InvoiceTimeExtractor implements TimestampExtractor {

        @Override
        public long extract(ConsumerRecord<Object, Object> consumerRecord, long prevTime) {
            SimpleInvoice invoice = (SimpleInvoice) consumerRecord.value();
            return ((invoice.getCreatedTime() > 0) ? invoice.getCreatedTime() : prevTime);
        }
    }
