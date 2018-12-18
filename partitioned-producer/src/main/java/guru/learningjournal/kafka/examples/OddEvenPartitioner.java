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

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.record.InvalidRecordException;

import java.util.Map;

/**
 * A custom partitioner class
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class OddEvenPartitioner implements Partitioner {
    /**
     * Simple partitioning on the message key
     * Odd keys to partition 0
     * Even keys to partition 1
     *
     * @param topic topic Name
     * @param key Message Key
     * @param keyBytes Key Bytes
     * @param value Message Value
     * @param valueBytes Value Bytes
     * @param cluster Cluster Object
     * @return Partition Id
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        if ((keyBytes == null) || (!(key instanceof Integer)))
            throw new InvalidRecordException("Topic Key must have a valid Integer value.");

        if (cluster.partitionsForTopic(topic).size() != 2)
            throw new InvalidTopicException("Topic must have exactly two partitions");

        return (Integer) key % 2;
    }

    @Override
    public void close() {
        //Nothing to close
    }

    @Override
    public void configure(Map<String, ?> map) {
        //Nothing to configure
    }
}
