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

import guru.learningjournal.kafka.examples.common.AppConfigs;
import guru.learningjournal.kafka.examples.common.AppSerdes;
import guru.learningjournal.kafka.examples.types.AdClick;
import guru.learningjournal.kafka.examples.types.AdImpression;
import guru.learningjournal.kafka.examples.types.CampaignPerformance;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Testing for Campaign Performance
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppTopologyTest {

    private static TopologyTestDriver topologyTestDriver;
    private static final Logger logger = LogManager.getLogger();

    @BeforeAll
    static void setUp() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,
            AppConfigs.applicationID);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
            AppConfigs.bootstrapServers);
        properties.put(StreamsConfig.STATE_DIR_CONFIG,
            AppConfigs.stateStoreLocationUT);

        StreamsBuilder builder = new StreamsBuilder();
        AppTopology.withBuilder(builder);
        Topology topology = builder.build();

        topologyTestDriver = new TopologyTestDriver(topology, properties);
    }


    @Test
    @Order(1)
    @DisplayName("Send first Impression for 'ABC Ltd' and validate the flow")
    void testImpressionFlow() {

        AdImpression adImpression = new AdImpression()
            .withImpressionID("100001")
            .withCampaigner("ABC Ltd");

        ConsumerRecordFactory<String, AdImpression> impressionFactory =
            new ConsumerRecordFactory<>(AppConfigs.impressionTopic,
                AppSerdes.String().serializer(), AppSerdes.AdImpression().serializer());

        topologyTestDriver.pipeInput(impressionFactory.create(
            AppConfigs.impressionTopic,
            "100001",
            adImpression
        ));

        ProducerRecord<String, CampaignPerformance> record =
            topologyTestDriver.readOutput(
                AppConfigs.outputTopic,
                AppSerdes.String().deserializer(),
                AppSerdes.CampaignPerformance().deserializer()
            );

        logger.info(record.value());
        assertAll(() -> assertEquals("ABC Ltd", record.value().getCampaigner()),
            () -> assertEquals("1", record.value().getAdImpressions().toString())
        );
    }

    @Test
    @Order(2)
    @DisplayName("Send second Impression for 'ABC Ltd' and validate the count")
    void testImpressionCount() {

        AdImpression adImpression = new AdImpression()
            .withImpressionID("100002")
            .withCampaigner("ABC Ltd");

        ConsumerRecordFactory<String, AdImpression> impressionFactory =
            new ConsumerRecordFactory<>(AppConfigs.impressionTopic,
                AppSerdes.String().serializer(), AppSerdes.AdImpression().serializer());

        topologyTestDriver.pipeInput(impressionFactory.create(
            AppConfigs.impressionTopic,
            "100002",
            adImpression
        ));

        ProducerRecord<String, CampaignPerformance> record =
            topologyTestDriver.readOutput(
                AppConfigs.outputTopic,
                AppSerdes.String().deserializer(),
                AppSerdes.CampaignPerformance().deserializer()
            );

        logger.info(record.value());
        assertAll(() -> assertEquals("ABC Ltd", record.value().getCampaigner()),
            () -> assertEquals("2", record.value().getAdImpressions().toString())
        );
    }

    @Test
    @Order(3)
    @DisplayName("Send first click for 'ABC Ltd' and validate the count")
    void testClicks() {
        AdClick adClick = new AdClick()
            .withImpressionID("100001")
            .withCampaigner("ABC Ltd");

        ConsumerRecordFactory<String, AdClick> adClickFactory =
            new ConsumerRecordFactory<>(AppConfigs.clicksTopic,
                AppSerdes.String().serializer(),
                AppSerdes.AdClick().serializer());

        topologyTestDriver.pipeInput(adClickFactory.create(
            AppConfigs.clicksTopic,
            "100001",
            adClick
        ));

        ProducerRecord<String, CampaignPerformance> record =
            topologyTestDriver.readOutput(
                AppConfigs.outputTopic,
                AppSerdes.String().deserializer(),
                AppSerdes.CampaignPerformance().deserializer()
            );

        logger.info(record.value());
        assertAll(() -> assertEquals("ABC Ltd", record.value().getCampaigner()),
            () -> assertEquals("2", record.value().getAdImpressions().toString()),
            () -> assertEquals("1", record.value().getAdClicks().toString())
        );
    }

    @Test
    @Order(4)
    @DisplayName("Check current state store values and validate end state")
    void testStateStore(){
        KeyValueStore<String,CampaignPerformance> store =
            topologyTestDriver.getKeyValueStore(
            AppConfigs.stateStoreNameCP);

        CampaignPerformance cpValue =  store.get("ABC Ltd");
        logger.info(cpValue);

        assertAll(() -> assertEquals("ABC Ltd", cpValue.getCampaigner()),
            () -> assertEquals("2", cpValue.getAdImpressions().toString()),
            () -> assertEquals("1", cpValue.getAdClicks().toString())
        );
    }

    @AfterAll
    static void tearDown() throws IOException {
        //topologyTestDriver.close() throwing error on windows 10 - Refer KAFKA-6647
        //Manually cleaning up state store after test
        try {
            topologyTestDriver.close();
        } catch (Exception e) {
            FileUtils.deleteDirectory(new File(AppConfigs.stateStoreLocationUT));
        }
    }
}