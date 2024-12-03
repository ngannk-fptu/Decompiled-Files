/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.counters;

import com.hazelcast.internal.util.counters.Counter;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class MwCounter
implements Counter {
    private static final AtomicLongFieldUpdater<MwCounter> COUNTER = AtomicLongFieldUpdater.newUpdater(MwCounter.class, "value");
    private volatile long value;

    private MwCounter(long initialValue) {
        this.value = initialValue;
    }

    @Override
    public long get() {
        return this.value;
    }

    @Override
    public long inc() {
        return COUNTER.incrementAndGet(this);
    }

    @Override
    public long inc(long amount) {
        return COUNTER.addAndGet(this, amount);
    }

    public String toString() {
        return "Counter{value=" + this.value + '}';
    }

    public static MwCounter newMwCounter() {
        return MwCounter.newMwCounter(0L);
    }

    public static MwCounter newMwCounter(long initialValue) {
        return new MwCounter(initialValue);
    }
}

