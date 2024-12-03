/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.core.QueueStore;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LatencyTrackingQueueStore<T>
implements QueueStore<T> {
    static final String KEY = "QueueStoreLatency";
    private final StoreLatencyPlugin.LatencyProbe loadProbe;
    private final StoreLatencyPlugin.LatencyProbe loadAllKeysProbe;
    private final StoreLatencyPlugin.LatencyProbe loadAllProbe;
    private final StoreLatencyPlugin.LatencyProbe deleteProbe;
    private final StoreLatencyPlugin.LatencyProbe deleteAllProbe;
    private final StoreLatencyPlugin.LatencyProbe storeProbe;
    private final StoreLatencyPlugin.LatencyProbe storeAllProbe;
    private final QueueStore<T> delegate;

    public LatencyTrackingQueueStore(QueueStore<T> delegate, StoreLatencyPlugin plugin, String queueName) {
        this.delegate = delegate;
        this.loadProbe = plugin.newProbe(KEY, queueName, "load");
        this.loadAllProbe = plugin.newProbe(KEY, queueName, "loadAll");
        this.loadAllKeysProbe = plugin.newProbe(KEY, queueName, "loadAllKeys");
        this.deleteProbe = plugin.newProbe(KEY, queueName, "delete");
        this.deleteAllProbe = plugin.newProbe(KEY, queueName, "deleteAll");
        this.storeProbe = plugin.newProbe(KEY, queueName, "store");
        this.storeAllProbe = plugin.newProbe(KEY, queueName, "storeAll");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void store(Long key, T value) {
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
    public void storeAll(Map<Long, T> map) {
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
    public void delete(Long key) {
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
    public void deleteAll(Collection<Long> keys) {
        long startNanos = System.nanoTime();
        try {
            this.delegate.deleteAll(keys);
        }
        finally {
            this.deleteAllProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T load(Long key) {
        long startNanos = System.nanoTime();
        try {
            T t = this.delegate.load(key);
            return t;
        }
        finally {
            this.loadProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Long, T> loadAll(Collection<Long> keys) {
        long startNanos = System.nanoTime();
        try {
            Map<Long, T> map = this.delegate.loadAll(keys);
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
    public Set<Long> loadAllKeys() {
        long startNanos = System.nanoTime();
        try {
            Set<Long> set = this.delegate.loadAllKeys();
            return set;
        }
        finally {
            this.loadAllKeysProbe.recordValue(System.nanoTime() - startNanos);
        }
    }
}

