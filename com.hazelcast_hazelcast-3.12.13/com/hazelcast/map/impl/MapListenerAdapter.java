/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryExpiredListener;
import com.hazelcast.map.listener.EntryLoadedListener;
import com.hazelcast.map.listener.EntryMergedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import com.hazelcast.map.listener.MapEvictedListener;

public class MapListenerAdapter<K, V>
implements EntryAddedListener<K, V>,
EntryUpdatedListener<K, V>,
EntryRemovedListener<K, V>,
EntryEvictedListener<K, V>,
EntryExpiredListener<K, V>,
EntryMergedListener<K, V>,
EntryLoadedListener<K, V>,
MapClearedListener,
MapEvictedListener {
    @Override
    public void entryAdded(EntryEvent<K, V> event) {
        this.onEntryEvent(event);
    }

    @Override
    public void entryRemoved(EntryEvent<K, V> event) {
        this.onEntryEvent(event);
    }

    @Override
    public void entryUpdated(EntryEvent<K, V> event) {
        this.onEntryEvent(event);
    }

    @Override
    public void entryEvicted(EntryEvent<K, V> event) {
        this.onEntryEvent(event);
    }

    @Override
    public void entryExpired(EntryEvent<K, V> event) {
        this.onEntryEvent(event);
    }

    @Override
    public void entryMerged(EntryEvent<K, V> event) {
        this.onEntryEvent(event);
    }

    @Override
    public void entryLoaded(EntryEvent<K, V> event) {
        this.onEntryEvent(event);
    }

    @Override
    public void mapEvicted(MapEvent event) {
        this.onMapEvent(event);
    }

    @Override
    public void mapCleared(MapEvent event) {
        this.onMapEvent(event);
    }

    public void onEntryEvent(EntryEvent<K, V> event) {
    }

    public void onMapEvent(MapEvent event) {
    }
}

