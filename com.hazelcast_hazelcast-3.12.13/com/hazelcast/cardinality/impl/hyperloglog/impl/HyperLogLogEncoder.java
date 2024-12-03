/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl.hyperloglog.impl;

import com.hazelcast.cardinality.impl.hyperloglog.impl.HyperLogLogEncoding;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public interface HyperLogLogEncoder
extends IdentifiedDataSerializable {
    public long estimate();

    public boolean add(long var1);

    public int getMemoryFootprint();

    public HyperLogLogEncoding getEncodingType();

    public HyperLogLogEncoder merge(HyperLogLogEncoder var1);
}

