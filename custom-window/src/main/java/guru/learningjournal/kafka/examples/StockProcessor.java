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

import guru.learningjournal.kafka.examples.types.StockTicker;
import guru.learningjournal.kafka.examples.types.TickerStack;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

@SuppressWarnings("unchecked")
public class StockProcessor implements Processor<String, StockTicker> {

    static final String PROCESSOR_NAME = "stock-tick-processor";
    static final String STATE_STORE_NAME = "ticker-stack-store";
    private static final Logger logger = LogManager.getLogger();
    private KeyValueStore<String, TickerStack> stateStore;


    @Override
    public void init(ProcessorContext processorContext) {
        this.stateStore = (KeyValueStore<String, TickerStack>)
            processorContext.getStateStore(STATE_STORE_NAME);

        processorContext.schedule(
            Duration.ofSeconds(AppConfigs.secondsDelay),
            PunctuationType.WALL_CLOCK_TIME,
            new StockPunctuator(processorContext, stateStore)
        );
    }

    @Override
    public void process(String key, StockTicker stockTicker) {
        TickerStack stack = stateStore.get(key);
        if (stack == null) {
            stack = new TickerStack();
        }
        stack.push(stockTicker.getLastTradedPrice());
        stateStore.put(key, stack);
        logger.info("Saving " + key + " to State Store : " + stack);
    }

    @Override
    public void close() {

    }
}