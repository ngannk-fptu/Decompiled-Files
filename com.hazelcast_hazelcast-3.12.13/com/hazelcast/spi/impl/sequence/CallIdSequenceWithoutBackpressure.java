/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.sequence;

import com.hazelcast.spi.impl.sequence.CallIdSequence;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class CallIdSequenceWithoutBackpressure
implements CallIdSequence {
    private static final AtomicLongFieldUpdater<CallIdSequenceWithoutBackpressure> HEAD = AtomicLongFieldUpdater.newUpdater(CallIdSequenceWithoutBackpressure.class, "head");
    private volatile long head;

    @Override
    public long getLastCallId() {
        return this.head;
    }

    @Override
    public int getMaxConcurrentInvocations() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long next() {
        return this.forceNext();
    }

    @Override
    public long forceNext() {
        return HEAD.incrementAndGet(this);
    }

    @Override
    public void complete() {
    }

    @Override
    public long concurrentInvocations() {
        return -1L;
    }
}

