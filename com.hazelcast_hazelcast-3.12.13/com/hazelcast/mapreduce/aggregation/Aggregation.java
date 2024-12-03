/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.aggregation.Supplier;
import java.util.Map;

@Deprecated
public interface Aggregation<Key, Supplied, Result> {
    public Collator<Map.Entry, Result> getCollator();

    public Mapper getMapper(Supplier<Key, ?, Supplied> var1);

    public CombinerFactory getCombinerFactory();

    public ReducerFactory getReducerFactory();
}

