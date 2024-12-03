/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.impl.task.MappingPhase;
import java.util.Collection;
import java.util.Map;

public class KeyValueSourceMappingPhase<KeyIn, ValueIn, KeyOut, ValueOut>
extends MappingPhase<KeyIn, ValueIn, KeyOut, ValueOut> {
    public KeyValueSourceMappingPhase(Collection<? extends KeyIn> keys, KeyPredicate<? super KeyIn> predicate) {
        super(keys, predicate);
    }

    @Override
    public void executeMappingPhase(KeyValueSource<KeyIn, ValueIn> keyValueSource, Mapper<KeyIn, ValueIn, KeyOut, ValueOut> mapper, Context<KeyOut, ValueOut> context) {
        while (keyValueSource.hasNext()) {
            if (this.matches(keyValueSource.key())) {
                Map.Entry<KeyIn, ValueIn> entry = keyValueSource.element();
                mapper.map(entry.getKey(), entry.getValue(), context);
            }
            if (!this.isCancelled()) continue;
            return;
        }
    }
}

