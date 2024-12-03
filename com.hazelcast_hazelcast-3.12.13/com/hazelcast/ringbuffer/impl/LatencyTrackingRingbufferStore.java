/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.core.RingbufferStore;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.spi.ObjectNamespace;

class LatencyTrackingRingbufferStore<T>
implements RingbufferStore<T> {
    static final String KEY = "RingbufferStoreLatency";
    private final StoreLatencyPlugin.LatencyProbe loadProbe;
    private final StoreLatencyPlugin.LatencyProbe getLargestSequenceProbe;
    private final StoreLatencyPlugin.LatencyProbe storeProbe;
    private final StoreLatencyPlugin.LatencyProbe storeAllProbe;
    private final RingbufferStore<T> delegate;

    LatencyTrackingRingbufferStore(RingbufferStore<T> delegate, StoreLatencyPlugin plugin, ObjectNamespace namespace) {
        String nsDescription = namespace.getServiceName() + ":" + namespace.getObjectName();
        this.delegate = delegate;
        this.loadProbe = plugin.newProbe(KEY, nsDescription, "load");
        this.getLargestSequenceProbe = plugin.newProbe(KEY, nsDescription, "getLargestSequence");
        this.storeProbe = plugin.newProbe(KEY, nsDescription, "store");
        this.storeAllProbe = plugin.newProbe(KEY, nsDescription, "storeAll");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void store(long sequence, T data) {
        long startNanos = System.nanoTime();
        try {
            this.delegate.store(sequence, data);
        }
        finally {
            this.storeProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void storeAll(long firstItemSequence, T[] items) {
        long startNanos = System.nanoTime();
        try {
            this.delegate.storeAll(firstItemSequence, items);
        }
        finally {
            this.storeAllProbe.recordValue(System.nanoTime() - startNanos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T load(long sequence) {
        long startNanos = System.nanoTime();
        try {
            T t = this.delegate.load(sequence);
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
    public long getLargestSequence() {
        long startNanos = System.nanoTime();
        try {
            long l = this.delegate.getLargestSequence();
            return l;
        }
        finally {
            this.getLargestSequenceProbe.recordValue(System.nanoTime() - startNanos);
        }
    }
}

