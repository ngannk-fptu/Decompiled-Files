/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.ReducingJob;
import com.hazelcast.mapreduce.ReducingSubmittableJob;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import java.util.List;
import java.util.Map;

@Deprecated
public interface MappingJob<EntryKey, KeyIn, ValueIn> {
    public MappingJob<EntryKey, KeyIn, ValueIn> onKeys(Iterable<? extends EntryKey> var1);

    public MappingJob<EntryKey, KeyIn, ValueIn> onKeys(EntryKey ... var1);

    public MappingJob<EntryKey, KeyIn, ValueIn> keyPredicate(KeyPredicate<? super EntryKey> var1);

    public MappingJob<EntryKey, KeyIn, ValueIn> chunkSize(int var1);

    public MappingJob<EntryKey, KeyIn, ValueIn> topologyChangedStrategy(TopologyChangedStrategy var1);

    public <ValueOut> ReducingJob<EntryKey, KeyIn, ValueOut> combiner(CombinerFactory<? super KeyIn, ? super ValueIn, ? extends ValueOut> var1);

    public <ValueOut> ReducingSubmittableJob<EntryKey, KeyIn, ValueOut> reducer(ReducerFactory<? super KeyIn, ? super ValueIn, ? extends ValueOut> var1);

    public JobCompletableFuture<Map<KeyIn, List<ValueIn>>> submit();

    public <ValueOut> JobCompletableFuture<ValueOut> submit(Collator<Map.Entry<KeyIn, List<ValueIn>>, ValueOut> var1);
}

