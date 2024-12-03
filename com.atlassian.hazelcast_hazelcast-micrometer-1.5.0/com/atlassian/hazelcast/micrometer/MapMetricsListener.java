/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.EntryEvent
 *  com.hazelcast.core.MapEvent
 *  com.hazelcast.map.listener.EntryAddedListener
 *  com.hazelcast.map.listener.EntryEvictedListener
 *  com.hazelcast.map.listener.EntryLoadedListener
 *  com.hazelcast.map.listener.EntryMergedListener
 *  com.hazelcast.map.listener.EntryRemovedListener
 *  com.hazelcast.map.listener.EntryUpdatedListener
 *  com.hazelcast.map.listener.MapClearedListener
 *  com.hazelcast.map.listener.MapEvictedListener
 *  com.hazelcast.map.listener.MapListener
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.hazelcast.micrometer;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryLoadedListener;
import com.hazelcast.map.listener.EntryMergedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import com.hazelcast.map.listener.MapEvictedListener;
import com.hazelcast.map.listener.MapListener;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Arrays;
import java.util.Collection;

final class MapMetricsListener<K, V>
implements MapListener,
MapClearedListener,
MapEvictedListener,
EntryAddedListener<K, V>,
EntryEvictedListener<K, V>,
EntryRemovedListener<K, V>,
EntryMergedListener<K, V>,
EntryUpdatedListener<K, V>,
EntryLoadedListener<K, V> {
    private static final String METER_PREFIX = "hazelcast.map.";
    private final MeterRegistry meterRegistry;

    MapMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void entryAdded(EntryEvent<K, V> event) {
        this.incrementCounter("entryAdded", this.tags(event));
    }

    public void entryEvicted(EntryEvent<K, V> event) {
        this.incrementCounter("entryEvicted", this.tags(event));
    }

    public void entryRemoved(EntryEvent<K, V> event) {
        this.incrementCounter("entryRemoved", this.tags(event));
    }

    public void entryUpdated(EntryEvent<K, V> event) {
        this.incrementCounter("entryUpdated", this.tags(event));
    }

    public void mapCleared(MapEvent event) {
        this.incrementCounter("cleared", this.tags(event));
    }

    public void mapEvicted(MapEvent event) {
        this.incrementCounter("evicted", this.tags(event));
    }

    public void entryLoaded(EntryEvent<K, V> event) {
        this.incrementCounter("entryLoaded", this.tags(event));
    }

    public void entryMerged(EntryEvent<K, V> event) {
        this.incrementCounter("entryMerged", this.tags(event));
    }

    private void incrementCounter(String name, Collection<Tag> tags) {
        this.meterRegistry.counter(METER_PREFIX + name, tags).increment();
    }

    private Collection<Tag> tags(EntryEvent<K, V> event) {
        return Arrays.asList(Tag.of((String)"mapName", (String)event.getName()));
    }

    private Collection<Tag> tags(MapEvent event) {
        return Arrays.asList(Tag.of((String)"entriesAffected", (String)String.valueOf(event.getNumberOfEntriesAffected())), Tag.of((String)"mapName", (String)event.getName()));
    }
}

