/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.MapLoader;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import java.util.Collection;
import java.util.Map;

public class LatencyTrackingMapLoader<K, V>
implements MapLoader<K, V> {
    static final String KEY = "MapStoreLatency";
    private final StoreLatencyPlugin.LatencyProbe loadProbe;
    private final StoreLatencyPlugin.LatencyProbe loadAllKeysProbe;
    private final StoreLatencyPlugin.LatencyProbe loadAllProbe;
    private final MapLoader<K, V> delegate;

    public LatencyTrackingMapLoader(MapLoader<K, V> delegate, StoreLatencyPlugin plugin, String mapName) {
        this.delegate = delegate;
        this.loadProbe = plugin.newProbe(KEY, mapName, "load");
        this.loadAllProbe = plugin.newProbe(KEY, mapName, "loadAll");
        this.loadAllKeysProbe = plugin.newProbe(KEY, mapName, "loadAllKeys");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V load(K key) {
        long startNanos = System.nanoTime();
        try {
            V v = this.delegate.load(key);
            return v;
        }
        finally {
            this.loadProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<K, V> loadAll(Collection<K> keys) {
        long startNanos = System.nanoTime();
        try {
            Map<K, V> map = this.delegate.loadAll(keys);
            return map;
        }
        finally {
            this.loadAllProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterable<K> loadAllKeys() {
        long startNanos = System.nanoTime();
        try {
            Iterable<K> iterable = this.delegate.loadAllKeys();
            return iterable;
        }
        finally {
            this.loadAllKeysProbe.recordValue(System.nanoTime() - startNanos);
        }
    }
}

