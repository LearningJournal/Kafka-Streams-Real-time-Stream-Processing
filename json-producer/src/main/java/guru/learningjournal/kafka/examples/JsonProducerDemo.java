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
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Kafka producer demo to send NSE data events from file
 * Reads CSV data from file and converts into Json objects
 * Sends Json messages to Kafka producer
 * Starts one thread for each file
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class JsonProducerDemo {
    private static final Logger logger = LogManager.getLogger();
    private static final String kafkaConfig = "/kafka.properties";

    /**
     * private static method to read data from given dataFile
     * @param dataFile data file name in resource folder
     * @return List of StockData Instance
     * @throws IOException, NullPointerException
     */
    private static List<StockData> getStocks(String dataFile) throws IOException, NullPointerException {

        URL fileURI = ClassLoader.class.getResource(dataFile);
        File file = new File(fileURI.getFile());
        MappingIterator<StockData> stockDataIterator = new CsvMapper().readerWithTypedSchemaFor(StockData.class).readValues(file);
        return stockDataIterator.readAll();
    }

    /**
     * Application entry point
     * you must provide the topic name and at least one event file
     *
     * @param args topicName (Name of the Kafka topic) list of files (list of files in the classpath)
     */
    public static void main(String[] args) {

        final KafkaProducer<String, JsonNode> producer;
        final String topicName;
        final ObjectMapper objectMapper = new ObjectMapper();
        List<Thread> dispatchers = new ArrayList<>();
        InputStream kafkaConfigStream;

        if (args.length < 2) {
            System.out.println("Please provide command line arguments: topicName EventFiles");
            System.exit(-1);
        }

        logger.info("Starting JsonProducerDemo...");
        topicName = args[0];
        String[] eventFiles = Arrays.copyOfRange(args, 1, args.length);
        //ArrayList<ArrayList<JsonNode>> stockArrayOfList = new ArrayList<>();
        List<JsonNode>[] stockArrayOfList = new List[eventFiles.length];
        for(int i=0;i<stockArrayOfList.length;i++){
            stockArrayOfList[i]=new ArrayList<>();
        }


        logger.trace("Creating Kafka producer...");
        Properties properties = new Properties();
        try {
            kafkaConfigStream = ClassLoader.class.getResourceAsStream(kafkaConfig);
            properties.load(kafkaConfigStream);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        } catch (IOException e) {
            logger.error("Cannot open Kafka config " + kafkaConfig);
            System.exit(-1);
        }
        producer = new KafkaProducer<>(properties);

        //For each data file
        for (int i = 0; i < eventFiles.length; i++) {
            logger.info("Preparing data for " + eventFiles[i]);
            try {
                for (StockData s : getStocks(eventFiles[i])) {
                    stockArrayOfList[i].add(objectMapper.valueToTree(s));
                }

            } catch (IOException | NullPointerException e) {
                throw new RuntimeException("Cannot read data file. Skipping " + eventFiles[i] + "...",e);
            }
            dispatchers.add(new Thread(new Dispatcher(producer, topicName, eventFiles[i], stockArrayOfList[i]), eventFiles[i]));
            dispatchers.get(i).start();
        }
        //Wait for threads
        try {
            for (Thread t : dispatchers) {
                t.join();
            }
        } catch (InterruptedException e) {
            logger.error("Thread Interrupted " + e.getMessage());
        } finally {
            producer.close();
            logger.info("Finished JsonDispatcherDemo - Closing Kafka Producer.");
        }

    }

}
