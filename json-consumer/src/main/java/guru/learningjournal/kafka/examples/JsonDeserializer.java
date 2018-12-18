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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Json Deserializer
 */
public class JsonDeserializer implements Deserializer<JsonNode> {
    private ObjectMapper objectMapper;

    public JsonDeserializer() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {
        //nothing to configure
    }

    /**
     * Deserialize to a JsonNode
     * @param topic topic name
     * @param data message bytes
     * @return JsonNode
     */
    @Override
    public JsonNode deserialize(String topic, byte[] data) {
        if(data==null){
            return null;
        }
        try{
            return objectMapper.readTree(data);
        }catch (Exception e){
            throw new SerializationException(e);
        }
    }

    @Override
    public void close() {
        //nothing to close
    }
}
