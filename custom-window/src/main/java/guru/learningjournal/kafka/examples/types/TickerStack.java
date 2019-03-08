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

package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.learningjournal.kafka.examples.AppConfigs;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "latestRecordTime",
    "ltpStack"
})
public class TickerStack {
    private ObjectMapper objectMapper = new ObjectMapper();
    @JsonProperty("latestRecordTime")
    private long latestRecordTime = Instant.now().toEpochMilli();

    @JsonProperty("ltpStack")
    private ArrayDeque<Double> ltpStack = new ArrayDeque<>(AppConfigs.ltpStackSize);

    @JsonProperty("latestRecordTime")
    public long getLatestRecordTime() {
        return latestRecordTime;
    }

    @JsonProperty("lastSentTime")
    public void setLatestRecordTime(long timestamp) {
        latestRecordTime = timestamp;
    }

    @JsonProperty("ltpStack")
    public String getLtpStack() {
        String str = "{ERROR}";
        try {
            str = objectMapper.writeValueAsString(ltpStack.descendingIterator());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return str;
    }

    @JsonProperty("ltpStack")
    public void setLtpStack(String jsonValues) throws IOException {
        Double[] values = objectMapper.readValue(jsonValues, Double[].class);
        for (Double value : values) {
            if (ltpStack.size() == AppConfigs.ltpStackSize) {
                ltpStack.removeLast();
            }
            ltpStack.addFirst(value);
        }
    }

    public void push(Double lastTradedPrice) {
        setLatestRecordTime(Instant.now().toEpochMilli());
        if (ltpStack.size() == AppConfigs.ltpStackSize) {
            ltpStack.removeLast();
        }
        ltpStack.addFirst(lastTradedPrice);
    }

    public Double avgValue() {
        return (double) Math.round(
            ltpStack.stream().reduce(0.0D, Double::sum) /
                ltpStack.size()
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("latestRecordTime", latestRecordTime)
            .append("ltpStack", getLtpStack())
            .toString();
    }
}