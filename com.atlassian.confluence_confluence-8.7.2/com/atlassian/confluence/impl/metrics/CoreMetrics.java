/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.Counter
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 *  io.micrometer.core.instrument.Timer
 */
package com.atlassian.confluence.impl.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.util.Arrays;
import java.util.function.Function;

public enum CoreMetrics {
    HIBERNATE_ENTITY_FETCH("confluence.hibernate.entity.fetch"),
    HIBERNATE_ENTITY_LOAD("confluence.hibernate.entity.load"),
    HIBERNATE_ENTITY_INSERT("confluence.hibernate.entity.insert"),
    HIBERNATE_ENTITY_UPDATE("confluence.hibernate.entity.update"),
    HIBERNATE_ENTITY_DELETE("confluence.hibernate.entity.delete"),
    HIBERNATE_ENTITY_LOCK_FAILURE("confluence.hibernate.entity.lockfailure"),
    HIBERNATE_COLLECTION_FETCH("confluence.hibernate.collection.fetch"),
    HIBERNATE_COLLECTION_LOAD("confluence.hibernate.collection.load"),
    HIBERNATE_COLLECTION_RECREATE("confluence.hibernate.collection.recreate"),
    HIBERNATE_COLLECTION_REMOVE("confluence.hibernate.collection.remove"),
    HIBERNATE_COLLECTION_UPDATE("confluence.hibernate.collection.update"),
    HIBERNATE_COLLECTION_DELETE("confluence.hibernate.collection.delete"),
    HIBERNATE_L2CACHE_GET("confluence.hibernate.l2cache.get"),
    HIBERNATE_L2CACHE_PUT("confluence.hibernate.l2cache.put"),
    HIBERNATE_L2CACHE_ELEMENTS("confluence.hibernate.l2cache.elements"),
    HIBERNATE_L2CACHE_SIZE("confluence.hibernate.l2cache.size"),
    LOCK_OPERATION_TIMER("LockOperationTimer"),
    HAZELCAST_CLUSTER_EVENT_TOTAL_WAIT_TIME("HazelcastExecutorClusterEventService.publish.totalWaitTime"),
    HAZELCAST_CLUSTER_EVENT_MEMBER_WAIT_TIME("HazelcastExecutorClusterEventService.publish.memberWaitTime"),
    LOG_EVENT("confluence.logging.event"),
    ASYNC_INVALIDATION_CACHE_PUBLISH_SEQUENCE_SNAPSHOT("confluence.cache.asyncinvalidationcache.publishsequencesnapshot"),
    ASYNC_INVALIDATION_CACHE_SEQUENCE_SNAPSHOT_INCONSISTENT("confluence.cache.asyncinvalidationcache.sequencesnapshotinconsistent"),
    ASYNC_INVALIDATION_CACHE_INVALIDATION_OUT_OF_SEQUENCE("confluence.cache.asyncinvalidationcache.cacheinvalidationoutofsequence"),
    INDEX_TASK_QUEUE_SIZE("confluence.indexTaskQueue.size"),
    HAZELCAST_CACHE_COMPACTION("confluence.cache.hazelcastcachecompaction");

    private final String meterName;

    private CoreMetrics(String meterName) {
        this.meterName = meterName;
    }

    public <T> T resolve(Function<String, T> resolver) {
        return resolver.apply(this.meterName);
    }

    public Timer timer(MeterRegistry meterRegistry, String ... tags) {
        return meterRegistry.timer(this.meterName, tags);
    }

    public Timer timer(MeterRegistry meterRegistry, Tag ... tags) {
        return meterRegistry.timer(this.meterName, Arrays.asList(tags));
    }

    public Counter counter(MeterRegistry meterRegistry, String ... tags) {
        return meterRegistry.counter(this.meterName, tags);
    }
}

