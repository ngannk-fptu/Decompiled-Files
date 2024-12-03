/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.hyperloglog;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;

public interface HyperLogLog
extends IdentifiedDataSerializable,
Versioned {
    public long estimate();

    public void add(long var1);

    public void addAll(long[] var1);

    public void merge(HyperLogLog var1);
}

