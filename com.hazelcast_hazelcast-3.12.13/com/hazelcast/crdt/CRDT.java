/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.cluster.impl.VectorClock;

public interface CRDT<T extends CRDT<T>> {
    public void merge(T var1);

    public VectorClock getCurrentVectorClock();
}

