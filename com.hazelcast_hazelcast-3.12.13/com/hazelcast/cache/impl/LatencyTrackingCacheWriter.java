/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache$Entry
 *  javax.cache.integration.CacheWriter
 *  javax.cache.integration.CacheWriterException
 */
package com.hazelcast.cache.impl;

import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import java.util.Collection;
import javax.cache.Cache;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;

public class LatencyTrackingCacheWriter<K, V>
implements CacheWriter<K, V> {
    static final String KEY = "CacheStoreLatency";
    private final CacheWriter<K, V> delegate;
    private final StoreLatencyPlugin.LatencyProbe writeProbe;
    private final StoreLatencyPlugin.LatencyProbe writeAllProbe;
    private final StoreLatencyPlugin.LatencyProbe deleteProbe;
    private final StoreLatencyPlugin.LatencyProbe deleteAllProbe;

    public LatencyTrackingCacheWriter(CacheWriter<K, V> delegate, StoreLatencyPlugin plugin, String cacheName) {
        this.delegate = delegate;
        this.writeProbe = plugin.newProbe(KEY, cacheName, "write");
        this.writeAllProbe = plugin.newProbe(KEY, cacheName, "writeAll");
        this.deleteProbe = plugin.newProbe(KEY, cacheName, "delete");
        this.deleteAllProbe = plugin.newProbe(KEY, cacheName, "deleteAll");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(Cache.Entry<? extends K, ? extends V> entry) throws CacheWriterException {
        long startNanos = System.nanoTime();
        try {
            this.delegate.write(entry);
        }
        finally {
            this.writeProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeAll(Collection<Cache.Entry<? extends K, ? extends V>> collection) throws CacheWriterException {
        long startNanos = System.nanoTime();
        try {
            this.delegate.writeAll(collection);
        }
        finally {
            this.writeAllProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void delete(Object o) throws CacheWriterException {
        long startNanos = System.nanoTime();
        try {
            this.delegate.delete(o);
        }
        finally {
            this.deleteProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteAll(Collection<?> collection) throws CacheWriterException {
        long startNanos = System.nanoTime();
        try {
            this.delegate.deleteAll(collection);
        }
        finally {
            this.deleteAllProbe.recordValue(System.nanoTime() - startNanos);
        }
    }
}

