/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.MapStore;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import java.util.Collection;
import java.util.Map;

public class LatencyTrackingMapStore<K, V>
implements MapStore<K, V> {
    static final String KEY = "MapStoreLatency";
    private final StoreLatencyPlugin.LatencyProbe deleteProbe;
    private final StoreLatencyPlugin.LatencyProbe deleteAllProbe;
    private final StoreLatencyPlugin.LatencyProbe storeProbe;
    private final StoreLatencyPlugin.LatencyProbe storeAllProbe;
    private final MapStore<K, V> delegate;

    public LatencyTrackingMapStore(MapStore<K, V> delegate, StoreLatencyPlugin plugin, String mapName) {
        this.delegate = delegate;
        this.deleteProbe = plugin.newProbe(KEY, mapName, "delete");
        this.deleteAllProbe = plugin.newProbe(KEY, mapName, "deleteAll");
        this.storeProbe = plugin.newProbe(KEY, mapName, "store");
        this.storeAllProbe = plugin.newProbe(KEY, mapName, "storeAll");
    }

    @Override
    public V load(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<K, V> loadAll(Collection<K> keys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<K> loadAllKeys() {
        throw new UnsupportedOperationException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void store(K key, V value) {
        long startNanos = System.nanoTime();
        try {
            this.delegate.store(key, value);
        }
        finally {
            this.storeProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void storeAll(Map<K, V> map) {
        long startNanos = System.nanoTime();
        try {
            this.delegate.storeAll(map);
        }
        finally {
            this.storeAllProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void delete(K key) {
        long startNanos = System.nanoTime();
        try {
            this.delegate.delete(key);
        }
        finally {
            this.deleteProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deleteAll(Collection<K> keys) {
        long startNanos = System.nanoTime();
        try {
            this.delegate.deleteAll(keys);
        }
        finally {
            this.deleteAllProbe.recordValue(System.nanoTime() - startNanos);
        }
    }
}

