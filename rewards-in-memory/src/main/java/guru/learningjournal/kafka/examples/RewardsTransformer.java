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

import guru.learningjournal.kafka.examples.types.Notification;
import guru.learningjournal.kafka.examples.types.PosInvoice;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

/**
 * Transform PosInvoice to Notification
 * Uses state store to get total accumulated rewards for the customer
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

@SuppressWarnings("unchecked")
public class RewardsTransformer implements ValueTransformer<PosInvoice, Notification> {
    private KeyValueStore<String, Double> stateStore;

    @Override
    public void init(ProcessorContext processorContext) {
        this.stateStore = (KeyValueStore) processorContext.getStateStore(
                AppConfigs.REWARDS_STORE_NAME);
    }

    @Override
    public Notification transform(PosInvoice invoice) {
        Notification notification = RecordBuilder.getNotification(invoice);
        Double accumulatedRewards = stateStore.get(notification.getCustomerCardNo());
        Double totalRewards;

        if (accumulatedRewards != null)
            totalRewards = accumulatedRewards + notification.getEarnedLoyaltyPoints();
        else
            totalRewards = notification.getEarnedLoyaltyPoints();

        notification.setTotalLoyaltyPoints(totalRewards);
        stateStore.put(notification.getCustomerCardNo(),
                totalRewards);

        return notification;
    }

    @Override
    public void close() {
    }
}
