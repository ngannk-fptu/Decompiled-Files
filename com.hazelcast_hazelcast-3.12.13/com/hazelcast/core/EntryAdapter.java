/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;

public class EntryAdapter<K, V>
implements EntryListener<K, V> {
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

