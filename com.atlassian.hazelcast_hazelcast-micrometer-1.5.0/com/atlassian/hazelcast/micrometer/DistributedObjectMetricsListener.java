/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.DistributedObjectEvent
 *  com.hazelcast.core.DistributedObjectListener
 *  com.hazelcast.core.ICollection
 *  com.hazelcast.core.IMap
 *  com.hazelcast.core.ITopic
 *  com.hazelcast.monitor.LocalMapStats
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.hazelcast.micrometer;

import com.atlassian.hazelcast.micrometer.ItemMetricsListener;
import com.atlassian.hazelcast.micrometer.LocalMapStatsMetrics;
import com.atlassian.hazelcast.micrometer.MapMetricsListener;
import com.atlassian.hazelcast.micrometer.MessageMetricsListener;
import com.atlassian.hazelcast.micrometer.NearCacheStatsMetrics;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.ICollection;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.monitor.LocalMapStats;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

final class DistributedObjectMetricsListener
implements DistributedObjectListener {
    private static final String METER_PREFIX = "hazelcast.distributedObject.";
    private final MeterRegistry meterRegistry;

    DistributedObjectMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void distributedObjectCreated(DistributedObjectEvent event) {
        this.incrementMeter("created", this.tags(event));
        DistributedObjectMetricsListener.ifTypeThen(event.getDistributedObject(), IMap.class, this::registerLocalMapStats);
        DistributedObjectMetricsListener.ifTypeThen(event.getDistributedObject(), IMap.class, this::registerMapListener);
        DistributedObjectMetricsListener.ifTypeThen(event.getDistributedObject(), ICollection.class, this::registerCollectionListener);
        DistributedObjectMetricsListener.ifTypeThen(event.getDistributedObject(), ITopic.class, this::registerTopicListener);
    }

    public void distributedObjectDestroyed(DistributedObjectEvent event) {
        this.incrementMeter("destroyed", this.tags(event));
    }

    private void registerMapListener(IMap<?, ?> map) {
        map.addEntryListener(new MapMetricsListener(this.meterRegistry), false);
    }

    private void registerCollectionListener(ICollection<?> collection) {
        collection.addItemListener(new ItemMetricsListener(this.meterRegistry), false);
    }

    private void registerTopicListener(ITopic<?> topic) {
        topic.addMessageListener(new MessageMetricsListener(this.meterRegistry));
    }

    private void registerLocalMapStats(IMap<?, ?> map) {
        LocalMapStats localMapStats = map.getLocalMapStats();
        List<Tag> tags = Collections.singletonList(Tag.of((String)"mapName", (String)map.getName()));
        new LocalMapStatsMetrics(this.meterRegistry).bind(localMapStats, tags);
        new NearCacheStatsMetrics(this.meterRegistry).bind(localMapStats.getNearCacheStats(), tags);
    }

    private void incrementMeter(String suffix, Collection<Tag> tags) {
        this.meterRegistry.counter(METER_PREFIX + suffix, tags).increment();
    }

    private Collection<Tag> tags(DistributedObjectEvent event) {
        return Arrays.asList(Tag.of((String)"serviceName", (String)String.valueOf(event.getServiceName())), Tag.of((String)"objectName", (String)String.valueOf(event.getObjectName())));
    }

    private static <T> void ifTypeThen(Object obj, Class<T> type, Consumer<T> consumer) {
        if (type.isInstance(obj)) {
            consumer.accept(type.cast(obj));
        }
    }
}

