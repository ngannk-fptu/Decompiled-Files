/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import java.util.concurrent.ConcurrentMap;

public interface AccumulatorInfoSupplier {
    public AccumulatorInfo getAccumulatorInfoOrNull(String var1, String var2);

    public void putIfAbsent(String var1, String var2, AccumulatorInfo var3);

    public void remove(String var1, String var2);

    public ConcurrentMap<String, ConcurrentMap<String, AccumulatorInfo>> getAll();
}

