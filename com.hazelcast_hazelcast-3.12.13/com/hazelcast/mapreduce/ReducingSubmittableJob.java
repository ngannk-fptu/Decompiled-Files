/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import java.util.Map;

@Deprecated
public interface ReducingSubmittableJob<EntryKey, KeyIn, ValueIn> {
    public ReducingSubmittableJob<EntryKey, KeyIn, ValueIn> onKeys(Iterable<EntryKey> var1);

    public ReducingSubmittableJob<EntryKey, KeyIn, ValueIn> onKeys(EntryKey ... var1);

    public ReducingSubmittableJob<EntryKey, KeyIn, ValueIn> keyPredicate(KeyPredicate<EntryKey> var1);

    public ReducingSubmittableJob<EntryKey, KeyIn, ValueIn> chunkSize(int var1);

    public ReducingSubmittableJob<EntryKey, KeyIn, ValueIn> topologyChangedStrategy(TopologyChangedStrategy var1);

    public JobCompletableFuture<Map<KeyIn, ValueIn>> submit();

    public <ValueOut> JobCompletableFuture<ValueOut> submit(Collator<Map.Entry<KeyIn, ValueIn>, ValueOut> var1);
}

