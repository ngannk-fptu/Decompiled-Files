/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.sequence;

import com.hazelcast.spi.impl.sequence.CallIdSequence;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.atomic.AtomicLongArray;

public abstract class AbstractCallIdSequence
implements CallIdSequence {
    private static final int INDEX_HEAD = 7;
    private static final int INDEX_TAIL = 15;
    private final AtomicLongArray longs = new AtomicLongArray(24);
    private final int maxConcurrentInvocations;

    public AbstractCallIdSequence(int maxConcurrentInvocations) {
        Preconditions.checkPositive(maxConcurrentInvocations, "maxConcurrentInvocations should be a positive number. maxConcurrentInvocations=" + maxConcurrentInvocations);
        this.maxConcurrentInvocations = maxConcurrentInvocations;
    }

    @Override
    public long next() {
        if (!this.hasSpace()) {
            this.handleNoSpaceLeft();
        }
        return this.forceNext();
    }

    protected abstract void handleNoSpaceLeft();

    @Override
    public long getLastCallId() {
        return this.longs.get(7);
    }

    @Override
    public int getMaxConcurrentInvocations() {
        return this.maxConcurrentInvocations;
    }

    @Override
    public void complete() {
        long newTail = this.longs.incrementAndGet(15);
        assert (newTail <= this.longs.get(7));
    }

    @Override
    public long forceNext() {
        return this.longs.incrementAndGet(7);
    }

    long getTail() {
        return this.longs.get(15);
    }

    protected boolean hasSpace() {
        return this.concurrentInvocations() < (long)this.maxConcurrentInvocations;
    }

    @Override
    public long concurrentInvocations() {
        return this.longs.get(7) - this.longs.get(15);
    }
}

