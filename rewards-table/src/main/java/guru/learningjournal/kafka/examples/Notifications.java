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
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Reducer;

/**
 * Utility class for Notification
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

class Notifications {

    private static Notification getNotification(PosInvoice invoice) {
        return new Notification()
                .withInvoiceNumber(invoice.getInvoiceNumber())
                .withCustomerCardNo(invoice.getCustomerCardNo())
                .withTotalAmount(invoice.getTotalAmount())
                .withEarnedLoyaltyPoints(invoice.getTotalAmount() * AppConfigs.LOYALTY_FACTOR)
                .withTotalLoyaltyPoints(invoice.getTotalAmount() * AppConfigs.LOYALTY_FACTOR);
    }

    static final class NotificationMapper
            implements KeyValueMapper<String, PosInvoice, KeyValue<String, Notification>> {

        @Override
        public KeyValue<String, Notification> apply(String key, PosInvoice value) {
            return new KeyValue<>(value.getCustomerCardNo(),
                    Notifications.getNotification(value));
        }
    }

    static final class NotificationReducer implements Reducer<Notification> {

        @Override
        public Notification apply(Notification storeValue, Notification newValue) {
            return new Notification()
                    .withInvoiceNumber(newValue.getInvoiceNumber())
                    .withCustomerCardNo(newValue.getCustomerCardNo())
                    .withTotalAmount(newValue.getTotalAmount())
                    .withEarnedLoyaltyPoints(newValue.getEarnedLoyaltyPoints())
                    .withTotalLoyaltyPoints(newValue.getEarnedLoyaltyPoints()
                            + storeValue.getTotalLoyaltyPoints());
        }
    }

    static NotificationMapper notificationMapper() {
        return new NotificationMapper();
    }

    static NotificationReducer sumRewards() {
        return new NotificationReducer();
    }

}
