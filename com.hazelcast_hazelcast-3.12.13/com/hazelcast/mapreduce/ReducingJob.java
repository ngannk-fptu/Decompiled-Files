/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.ReducingSubmittableJob;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import java.util.List;
import java.util.Map;

@Deprecated
public interface ReducingJob<EntryKey, KeyIn, ValueIn> {
    public ReducingJob<EntryKey, KeyIn, ValueIn> onKeys(Iterable<EntryKey> var1);

    public ReducingJob<EntryKey, KeyIn, ValueIn> onKeys(EntryKey ... var1);

    public ReducingJob<EntryKey, KeyIn, ValueIn> keyPredicate(KeyPredicate<EntryKey> var1);

    public ReducingJob<EntryKey, KeyIn, ValueIn> chunkSize(int var1);

    public ReducingJob<EntryKey, KeyIn, ValueIn> topologyChangedStrategy(TopologyChangedStrategy var1);

    public <ValueOut> ReducingSubmittableJob<EntryKey, KeyIn, ValueOut> reducer(ReducerFactory<KeyIn, ValueIn, ValueOut> var1);

    public JobCompletableFuture<Map<KeyIn, List<ValueIn>>> submit();

    public <ValueOut> JobCompletableFuture<ValueOut> submit(Collator<Map.Entry<KeyIn, List<ValueIn>>, ValueOut> var1);
}

