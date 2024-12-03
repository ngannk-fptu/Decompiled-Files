/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.MappingJob;
import com.hazelcast.mapreduce.TopologyChangedStrategy;

@Deprecated
public interface Job<KeyIn, ValueIn> {
    public Job<KeyIn, ValueIn> onKeys(Iterable<? extends KeyIn> var1);

    public Job<KeyIn, ValueIn> onKeys(KeyIn ... var1);

    public Job<KeyIn, ValueIn> chunkSize(int var1);

    public Job<KeyIn, ValueIn> topologyChangedStrategy(TopologyChangedStrategy var1);

    public Job<KeyIn, ValueIn> keyPredicate(KeyPredicate<? super KeyIn> var1);

    public <KeyOut, ValueOut> MappingJob<KeyIn, KeyOut, ValueOut> mapper(Mapper<KeyIn, ValueIn, KeyOut, ValueOut> var1);
}

