/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event.sequence;

import com.hazelcast.map.impl.querycache.event.sequence.PartitionSequencer;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultPartitionSequencer
implements PartitionSequencer {
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public long nextSequence() {
        return this.sequence.incrementAndGet();
    }

    @Override
    public void setSequence(long update) {
        this.sequence.set(update);
    }

    @Override
    public boolean compareAndSetSequence(long expect, long update) {
        return this.sequence.compareAndSet(expect, update);
    }

    @Override
    public long getSequence() {
        return this.sequence.get();
    }

    @Override
    public void reset() {
        this.sequence.set(0L);
    }
}

