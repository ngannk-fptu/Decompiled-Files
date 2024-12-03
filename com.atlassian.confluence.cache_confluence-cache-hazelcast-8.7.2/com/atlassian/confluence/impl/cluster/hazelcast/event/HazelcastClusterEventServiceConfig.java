/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.impl.cluster.event.AvailabilityCheckingClusterEventService
 *  com.atlassian.confluence.impl.cluster.event.ClusterEventService
 *  com.atlassian.confluence.impl.cluster.event.TopicEventCluster
 *  com.atlassian.confluence.impl.cluster.event.TopicEventPublisher
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.event.api.EventPublisher
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.Member
 *  io.micrometer.core.instrument.MeterRegistry
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.cluster.hazelcast.event;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.hazelcast.HazelcastExecutorClusterEventService;
import com.atlassian.confluence.impl.cluster.event.AvailabilityCheckingClusterEventService;
import com.atlassian.confluence.impl.cluster.event.ClusterEventService;
import com.atlassian.confluence.impl.cluster.event.TopicEventCluster;
import com.atlassian.confluence.impl.cluster.event.TopicEventPublisher;
import com.atlassian.confluence.impl.cluster.hazelcast.event.HazelcastTopicEvent;
import com.atlassian.confluence.impl.cluster.hazelcast.event.HazelcastTopicEventCluster;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import io.micrometer.core.instrument.MeterRegistry;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class HazelcastClusterEventServiceConfig {
    @Resource
    private ClusterManager clusterManager;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private HazelcastInstance hazelcastInstance;
    @Resource
    private MeterRegistry micrometerMeterRegistry;
    @Resource
    private DarkFeaturesManager darkFeaturesManager;

    HazelcastClusterEventServiceConfig() {
    }

    @Bean
    ClusterEventService clusterEventService() {
        return new AvailabilityCheckingClusterEventService(this.topicEventPublisher(), new HazelcastExecutorClusterEventService(this.hazelcastInstance, this.micrometerMeterRegistry).asClusterEventService());
    }

    @Bean
    TopicEventPublisher<HazelcastTopicEvent, Member> topicEventPublisher() {
        return TopicEventPublisher.create((ClusterManager)this.clusterManager, (EventPublisher)this.eventPublisher, (TopicEventCluster)this.topicEventCluster(), (DarkFeaturesManager)this.darkFeaturesManager);
    }

    @Bean
    HazelcastTopicEventCluster topicEventCluster() {
        return new HazelcastTopicEventCluster(this.hazelcastInstance);
    }
}

