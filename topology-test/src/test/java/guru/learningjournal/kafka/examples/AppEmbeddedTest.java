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
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.test.TestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration Testing for campaign performance
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
@EnableRuleMigrationSupport
class AppEmbeddedTest {
    private static final Logger logger = LogManager.getLogger();
    private static final int NUM_BROKERS = 1;
    private static final String CONSUMER_GROUP = "embedded-test-consumer";
    private static EmbeddedKafkaCluster kafkaCluster;
    private static KafkaStreams streams;


    @BeforeAll
    static void setUp() throws InterruptedException, IOException {
        //Start Kafka Cluster
        kafkaCluster = new EmbeddedKafkaCluster(NUM_BROKERS);
        kafkaCluster.start();


        //Create topics
        kafkaCluster.createTopic(AppConfigs.impressionTopic);
        kafkaCluster.createTopic(AppConfigs.clicksTopic);
        kafkaCluster.createTopic(AppConfigs.outputTopic);

        //Create and start Streams application
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,
            AppConfigs.applicationID);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafkaCluster.bootstrapServers());
        properties.put(StreamsConfig.STATE_DIR_CONFIG,
            AppConfigs.stateStoreLocationUT);

        StreamsBuilder builder = new StreamsBuilder();
        AppTopology.withBuilder(builder);
        Topology topology = builder.build();

        streams = new KafkaStreams(topology, properties);
        streams.start();
    }

    @Test
    @DisplayName("End to End flow testing with embedded cluster")
    void testImpressionFlow() throws ExecutionException, InterruptedException {
        //Setup data for Impressions
        List<KeyValue<String, AdImpression>> impList = new ArrayList<>();
        impList.add(KeyValue.pair("100001", new AdImpression()
            .withImpressionID("100001").withCampaigner("ABC Ltd")));
        impList.add(KeyValue.pair("100002", new AdImpression()
            .withImpressionID("100002").withCampaigner("ABC Ltd")));

        //Setup a producer for Impressions
        Properties impProperties = TestUtils.producerConfig(
            kafkaCluster.bootstrapServers(),
            AppSerdes.String().serializer().getClass(),
            AppSerdes.AdImpression().serializer().getClass());

        IntegrationTestUtils.produceKeyValuesSynchronously(
            AppConfigs.impressionTopic,
            impList,
            impProperties,
            Time.SYSTEM);

        //Setup data for Clicks
        List<KeyValue<String, AdClick>> clkList = new ArrayList<>();
        clkList.add(KeyValue.pair("100001", new AdClick()
            .withImpressionID("100001").withCampaigner("ABC Ltd")));

        //Setup a producer for Clicks
        Properties clkProperties = TestUtils.producerConfig(
            kafkaCluster.bootstrapServers(),
            AppSerdes.String().serializer().getClass(),
            AppSerdes.AdClick().serializer().getClass());

        IntegrationTestUtils.produceKeyValuesSynchronously(
            AppConfigs.clicksTopic,
            clkList,
            clkProperties,
            Time.SYSTEM);

        Properties serdeProps = new Properties();
        serdeProps.put("specific.class.name", CampaignPerformance.class);

        Properties cmpProperties = TestUtils.consumerConfig(
            kafkaCluster.bootstrapServers(),
            CONSUMER_GROUP,
            AppSerdes.String().deserializer().getClass(),
            AppSerdes.CampaignPerformance().deserializer().getClass(),
            serdeProps
        );

        List<KeyValue<String, CampaignPerformance>> outputList =
            IntegrationTestUtils.waitUntilMinKeyValueRecordsReceived(
                cmpProperties, AppConfigs.outputTopic, 1
            );

        outputList.forEach((record) -> {
                logger.info(record.value);
                assertAll(() -> assertEquals("ABC Ltd", record.value.getCampaigner()),
                    () -> assertEquals("2", record.value.getAdImpressions().toString()),
                    () -> assertEquals("1", record.value.getAdClicks().toString())
                );
            }
        );

    }

    @AfterAll
    static void tearDown() throws IOException {
        streams.close();
        try {
            IntegrationTestUtils.cleanStateAfterTest(kafkaCluster, streams);
        } catch (Exception e) {
            FileUtils.deleteDirectory(new File(AppConfigs.stateStoreLocationUT));
        }

    }
}
