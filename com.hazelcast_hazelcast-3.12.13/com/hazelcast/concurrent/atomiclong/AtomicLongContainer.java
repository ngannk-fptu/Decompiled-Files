/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong;

public class AtomicLongContainer {
    private long value;

    public long get() {
        return this.value;
    }

    public long addAndGet(long delta) {
        this.value += delta;
        return this.value;
    }

    public void set(long value) {
        this.value = value;
    }

    public boolean compareAndSet(long expect, long value) {
        if (this.value != expect) {
            return false;
        }
        this.value = value;
        return true;
    }

    public long getAndAdd(long delta) {
        long tempValue = this.value;
        this.value += delta;
        return tempValue;
    }

    public long getAndSet(long value) {
        long tempValue = this.value;
        this.value = value;
        return tempValue;
    }
}

