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
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public class StockPunctuator implements Punctuator {
    private static final Logger logger = LogManager.getLogger();
    private ProcessorContext context;
    private KeyValueStore<String, TickerStack> stateStore;

    StockPunctuator(ProcessorContext context,
                    KeyValueStore<String, TickerStack> stateStore) {
        this.context = context;
        this.stateStore = stateStore;
    }

    @Override
    public void punctuate(long currentTime) {
        long lastPunctuateTime = currentTime -
            Duration.ofSeconds(AppConfigs.secondsDelay)
                .toMillis();

        stateStore.all().forEachRemaining((kv) -> {
            //logger.info("Last Record Time: " + kv.value.getLatestRecordTime() +
            // " PunctuateTime: " + currentTime);
            if (kv.value.getLatestRecordTime() > lastPunctuateTime) {
                StockTicker ticker = new StockTicker()
                    .withTickerSymbol(kv.key)
                    .withTickerTime(currentTime)
                    .withLastTradedPrice(kv.value.avgValue());
                logger.info("Forward ticker " + ticker.toString() );
                context.forward(kv.key, ticker);
            }
        });
    }
}