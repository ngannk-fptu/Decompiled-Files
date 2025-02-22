/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong;

import com.hazelcast.cp.CPGroupId;

public class RaftAtomicLong {
    private final CPGroupId groupId;
    private final String name;
    private long value;

    RaftAtomicLong(CPGroupId groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }

    RaftAtomicLong(CPGroupId groupId, String name, long value) {
        this.groupId = groupId;
        this.name = name;
        this.value = value;
    }

    public CPGroupId groupId() {
        return this.groupId;
    }

    public String name() {
        return this.name;
    }

    public long addAndGet(long delta) {
        this.value += delta;
        return this.value;
    }

    public long getAndAdd(long delta) {
        long v = this.value;
        this.value += delta;
        return v;
    }

    public long getAndSet(long value) {
        long v = this.value;
        this.value = value;
        return v;
    }

    public boolean compareAndSet(long currentValue, long newValue) {
        if (this.value == currentValue) {
            this.value = newValue;
            return true;
        }
        return false;
    }

    public long value() {
        return this.value;
    }

    public String toString() {
        return "RaftAtomicLong{groupId=" + this.groupId + ", name='" + this.name + '\'' + ", value=" + this.value + '}';
    }
}

