/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.integration.CacheLoader
 *  javax.cache.integration.CacheLoaderException
 */
package com.hazelcast.cache.impl;

import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import java.util.Map;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;

public class LatencyTrackingCacheLoader<K, V>
implements CacheLoader<K, V> {
    static final String KEY = "CacheLoaderLatency";
    private final CacheLoader<K, V> delegate;
    private final StoreLatencyPlugin.LatencyProbe loadProbe;
    private final StoreLatencyPlugin.LatencyProbe loadAllProbe;

    public LatencyTrackingCacheLoader(CacheLoader<K, V> delegate, StoreLatencyPlugin plugin, String cacheName) {
        this.delegate = delegate;
        this.loadProbe = plugin.newProbe(KEY, cacheName, "load");
        this.loadAllProbe = plugin.newProbe(KEY, cacheName, "loadAll");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V load(K k) throws CacheLoaderException {
        long startNanos = System.nanoTime();
        try {
            Object object = this.delegate.load(k);
            return (V)object;
        }
        finally {
            this.loadProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<K, V> loadAll(Iterable<? extends K> iterable) throws CacheLoaderException {
        long startNanos = System.nanoTime();
        try {
            Map map = this.delegate.loadAll(iterable);
            return map;
        }
        finally {
            this.loadAllProbe.recordValue(System.nanoTime() - startNanos);
        }
    }
}

