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
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom Processor to mock HBase Sink
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class SinkProcessor implements Processor<String, GenericRecord> {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void init(ProcessorContext processorContext) {

    }

    @Override
    public void process(String key, GenericRecord valueRecord) {
        logger.info("Mocking Sink to HBase....");
        logger.info("Table: " + key + " RowID: " + valueRecord.getRowID());
        valueRecord.getAdditionalProperties().forEach((k, v) ->
            logger.info("Column: " + k + " Value: " + v)
        );
    }

    @Override
    public void close() {

    }
}
