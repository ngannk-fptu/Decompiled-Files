/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.aggregation.Supplier;
import java.util.Map;

public interface AggType<KeyIn, ValueIn, KeyOut, SuppliedValue, CombinerValue, ReducerValue, Result> {
    public Collator<Map.Entry<KeyOut, ReducerValue>, Result> getCollator();

    public Mapper<KeyIn, ValueIn, KeyOut, SuppliedValue> getMapper(Supplier<KeyIn, ValueIn, SuppliedValue> var1);

    public CombinerFactory<KeyOut, SuppliedValue, CombinerValue> getCombinerFactory();

    public ReducerFactory<KeyOut, CombinerValue, ReducerValue> getReducerFactory();
}

