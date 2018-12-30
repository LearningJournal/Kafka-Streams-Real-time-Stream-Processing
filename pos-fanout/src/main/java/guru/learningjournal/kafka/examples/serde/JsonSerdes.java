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

package guru.learningjournal.kafka.examples.serde;

import guru.learningjournal.kafka.examples.types.HadoopRecord;
import guru.learningjournal.kafka.examples.types.Notification;
import guru.learningjournal.kafka.examples.types.PosInvoice;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Specific Serdes for Java Objects
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

public class JsonSerdes {

    /**
     * Create Serde for PosInvoice Object
     *
     * @return Serde for PosInvoice
     */
    public static Serde<PosInvoice> PosInvoice() {
        final Serializer<PosInvoice> invoiceSerializer = new JsonSerializer<>();
        final Deserializer<PosInvoice> invoiceDeserializer = new JsonDeserializer<>();
        Map<String, Object> invoiceSerdeConfigs = new HashMap<>();
        invoiceSerdeConfigs.put("specific.class.name", PosInvoice.class);
        invoiceDeserializer.configure(invoiceSerdeConfigs, false);
        return Serdes.serdeFrom(invoiceSerializer, invoiceDeserializer);
    }

    /**
     * Create Serde for Notification Object
     *
     * @return Serde for Notification
     */
    public static Serde<Notification> Notification() {
        final Serializer<Notification> notificationSerializer = new JsonSerializer<>();
        final Deserializer<Notification> notificationDeserializer = new JsonDeserializer<>();
        Map<String, Object> notificationSerdeConfigs = new HashMap<>();
        notificationSerdeConfigs.put("specific.class.name", Notification.class);
        notificationDeserializer.configure(notificationSerdeConfigs, false);
        return Serdes.serdeFrom(notificationSerializer, notificationDeserializer);
    }

    /**
     * Create Serde for HadoopRecord
     *
     * @return Serde for HadoopRecord
     */
    public static Serde<HadoopRecord> HadoopRecord() {
        final Serializer<HadoopRecord> hadoopRecordSerializer = new JsonSerializer<>();
        final Deserializer<HadoopRecord> hadoopRecordDeserializer = new JsonDeserializer<>();
        Map<String, Object> hadoopRecordSerdeConfigs = new HashMap<>();
        hadoopRecordSerdeConfigs.put("specific.class.name", HadoopRecord.class);
        hadoopRecordDeserializer.configure(hadoopRecordSerdeConfigs, false);
        return Serdes.serdeFrom(hadoopRecordSerializer, hadoopRecordDeserializer);
    }
}
