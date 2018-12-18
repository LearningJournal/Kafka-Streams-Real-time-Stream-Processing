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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TransactionalProducer {
    private static final Logger logger = LogManager.getLogger();
    private static final String kafkaConfig = "/kafka.properties";

    /**
     * private static method to read data from given dataFile
     *
     * @param dataFile data file name in resource folder
     * @return List of StockData Instance
     * @throws IOException, NullPointerException
     */
    private static List<StockData> getStocks(String dataFile) throws IOException, NullPointerException {

        File file = new File(dataFile);
        MappingIterator<StockData> stockDataIterator = new CsvMapper().readerWithTypedSchemaFor(StockData.class).readValues(file);
        return stockDataIterator.readAll();
    }

    /**
     * Application entry point
     * you must provide the topic name and at least one event file
     *
     * @param args topicName (Name of the Kafka topic) list of files (list of files in the classpath)
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        final ObjectMapper objectMapper = new ObjectMapper();


        if (args.length != 4) {
            System.out.println("Please provide command line arguments: topicName1 topicName2 EventFile1 EventFile2");
            System.exit(-1);
        }
        String[] topicNames = Arrays.copyOfRange(args, 0, 2);
        String[] eventFiles = Arrays.copyOfRange(args, 2, 4);
        List<JsonNode>[] stockArrayOfList = new List[eventFiles.length];
        for (int i = 0; i < stockArrayOfList.length; i++) {
            stockArrayOfList[i] = new ArrayList<>();
        }

        logger.trace("Creating Kafka producer...");
        Properties properties = new Properties();
        try {
            InputStream kafkaConfigStream = ClassLoader.class.getResourceAsStream(kafkaConfig);
            properties.load(kafkaConfigStream);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
            properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transaction_id-1");

        } catch (IOException e) {
            logger.error("Cannot open Kafka config " + kafkaConfig);
            System.exit(-1);
        }
        KafkaProducer<String, JsonNode> producer = new KafkaProducer<>(properties);
        producer.initTransactions();
        producer.beginTransaction();

        //For each data file
        List<Thread> dispatchers = new ArrayList<>();
        try {
            for (int i = 0; i < eventFiles.length; i++) {
                logger.info("Preparing data for " + eventFiles[i]);
                for (StockData s : getStocks(eventFiles[i])) {
                    stockArrayOfList[i].add(objectMapper.valueToTree(s));
                }
                dispatchers.add(new Thread(new Dispatcher(producer, topicNames[i], eventFiles[i], stockArrayOfList[i]), eventFiles[i]));
                dispatchers.get(i).start();
            }
        } catch (Exception e) {
            logger.error("Cannot read data files");
            producer.abortTransaction();
            producer.close();
            throw new RuntimeException(e);
        }
        //Wait for threads
        try {
            for (Thread t : dispatchers) {
                t.join();
            }
            //Comment the commit transaction and uncomment abort transaction for testing
            producer.commitTransaction();
            //producer.abortTransaction();
        } catch (InterruptedException e) {
            producer.abortTransaction();
            logger.error("Thread Interrupted " + e.getMessage());
        } finally {
            producer.close();
            logger.info("Finished Application - Closing Kafka Producer.");
        }

    }

}