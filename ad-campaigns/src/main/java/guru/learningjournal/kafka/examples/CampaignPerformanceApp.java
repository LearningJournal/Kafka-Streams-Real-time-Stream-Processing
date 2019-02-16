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
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class CampaignPerformanceApp {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,
            AppConfigs.applicationID);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
            AppConfigs.bootstrapServers);
        properties.put(StreamsConfig.STATE_DIR_CONFIG,
            AppConfigs.stateStoreName);

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, AdImpression> KS0 = streamsBuilder.stream(
            AppConfigs.impressionTopic,
            Consumed.with(AppSerdes.String(), AppSerdes.AdImpression())
        );

        KTable<String, Long> adImpressionCount = KS0.groupBy(
            (k, v) -> v.getCampaigner(),
            Grouped.with(AppSerdes.String(),
                AppSerdes.AdImpression()))
            .count();

        KStream<String, AdClick> KS1 = streamsBuilder.stream(
            AppConfigs.clicksTopic,
            Consumed.with(AppSerdes.String(), AppSerdes.AdClick())
        );

        KTable<String, Long> adClickCount = KS1.groupBy(
            (k, v) -> v.getCampaigner(),
            Grouped.with(AppSerdes.String(),
                AppSerdes.AdClick()))
            .count();

        KTable<String, CampaignPerformance> campaignPerformance =
            adImpressionCount.leftJoin(adClickCount,
                (impCount, clkCount) -> new CampaignPerformance()
                    .withAdImpressions(impCount)
                    .withAdClicks(clkCount))
                .mapValues((k, v) -> v.withCampaigner(k));

        campaignPerformance.toStream()
            .foreach((k, v) -> logger.info(v));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), properties);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
