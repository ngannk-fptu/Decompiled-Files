/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference;

import com.hazelcast.nio.serialization.Data;

public class AtomicReferenceContainer {
    private Data value;

    public Data get() {
        return this.value;
    }

    public void set(Data value) {
        this.value = value;
    }

    public boolean compareAndSet(Data expect, Data value) {
        if (!this.contains(expect)) {
            return false;
        }
        this.value = value;
        return true;
    }

    public boolean contains(Data expected) {
        if (this.value == null) {
            return expected == null;
        }
        return this.value.equals(expected);
    }

    public Data getAndSet(Data value) {
        Data tempValue = this.value;
        this.value = value;
        return tempValue;
    }

    public boolean isNull() {
        return this.value == null;
    }
}

