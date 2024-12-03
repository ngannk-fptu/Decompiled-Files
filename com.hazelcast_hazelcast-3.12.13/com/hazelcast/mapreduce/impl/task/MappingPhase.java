/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.spi.partition.IPartitionService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MappingPhase<KeyIn, ValueIn, KeyOut, ValueOut> {
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private final KeyPredicate<? super KeyIn> predicate;
    private final Object[] keys;
    private Object[][] partitionMappedKeys;
    private Object[] partitionKeys;

    public MappingPhase(Collection<? extends KeyIn> keys, KeyPredicate<? super KeyIn> predicate) {
        this.predicate = predicate;
        this.keys = keys != null ? keys.toArray(new Object[0]) : null;
    }

    public void cancel() {
        this.cancelled.set(true);
    }

    protected boolean isCancelled() {
        return this.cancelled.get();
    }

    protected boolean processingPartitionNecessary(int partitionId, IPartitionService partitionService) {
        if (partitionId == -1) {
            this.partitionKeys = null;
            return true;
        }
        this.partitionKeys = this.prepareKeys(partitionId, partitionService);
        if (this.keys == null || this.keys.length == 0 || this.predicate != null) {
            return true;
        }
        return this.partitionKeys != null && this.partitionKeys.length > 0;
    }

    protected boolean matches(KeyIn key) {
        if (this.partitionKeys == null && this.predicate == null) {
            return true;
        }
        if (this.partitionKeys != null && this.partitionKeys.length > 0) {
            for (Object matcher : this.partitionKeys) {
                if (key != matcher && !key.equals(matcher)) continue;
                return true;
            }
        }
        return this.predicate != null && this.predicate.evaluate(key);
    }

    private Object[] prepareKeys(int partitionId, IPartitionService partitionService) {
        if (this.keys == null || this.keys.length == 0) {
            return null;
        }
        if (this.partitionMappedKeys != null) {
            return this.partitionMappedKeys[partitionId];
        }
        this.partitionMappedKeys = this.buildCache(partitionService);
        return this.partitionMappedKeys[partitionId];
    }

    private Object[][] buildCache(IPartitionService partitionService) {
        List<Object>[] mapping = this.buildMapping(partitionService);
        Object[][] cache = new Object[mapping.length][];
        for (int i = 0; i < cache.length; ++i) {
            List<Object> keys = mapping[i];
            if (keys == null) continue;
            cache[i] = keys.toArray(new Object[0]);
        }
        return cache;
    }

    private List<Object>[] buildMapping(IPartitionService partitionService) {
        List[] mapping = new List[partitionService.getPartitionCount()];
        for (Object key : this.keys) {
            int pid = partitionService.getPartitionId(key);
            ArrayList<Object> list = mapping[pid];
            if (list == null) {
                mapping[pid] = list = new ArrayList<Object>();
            }
            list.add(key);
        }
        return mapping;
    }

    protected abstract void executeMappingPhase(KeyValueSource<KeyIn, ValueIn> var1, Mapper<KeyIn, ValueIn, KeyOut, ValueOut> var2, Context<KeyOut, ValueOut> var3);
}

