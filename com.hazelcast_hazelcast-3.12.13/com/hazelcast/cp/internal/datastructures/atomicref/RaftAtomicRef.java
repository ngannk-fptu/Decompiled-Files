/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.nio.serialization.Data;

public class RaftAtomicRef {
    private final CPGroupId groupId;
    private final String name;
    private Data value;

    RaftAtomicRef(CPGroupId groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }

    RaftAtomicRef(CPGroupId groupId, String name, Data value) {
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

    public Data get() {
        return this.value;
    }

    public void set(Data value) {
        this.value = value;
    }

    public boolean contains(Data expected) {
        return this.value != null ? this.value.equals(expected) : expected == null;
    }
}

