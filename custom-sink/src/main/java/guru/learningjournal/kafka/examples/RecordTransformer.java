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

import guru.learningjournal.kafka.examples.types.GenericRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;

/**
 * Transform record to modify key as topic name
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

public class RecordTransformer
    implements Transformer<String, GenericRecord, KeyValue<String, GenericRecord>> {

    private ProcessorContext context;

    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
    }

    @Override
    public KeyValue<String, GenericRecord> transform(String key, GenericRecord genericRecord) {
        return KeyValue.pair(context.topic(), genericRecord);
    }

    @Override
    public void close() {
    }
}
