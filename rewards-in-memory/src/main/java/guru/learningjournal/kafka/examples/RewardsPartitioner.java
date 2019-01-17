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
import org.apache.kafka.streams.processor.StreamPartitioner;

/**
 * Partitioner for customer ID
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

public class RewardsPartitioner implements StreamPartitioner<String, PosInvoice> {
    @Override
    public Integer partition(String topic, String key,
                             PosInvoice value, int numPartitions) {

        return value.getCustomerCardNo().hashCode() % numPartitions;
    }
}


