/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.strategy;

import com.hazelcast.core.PartitionAware;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;

@SerializableByConvention
public final class StringAndPartitionAwarePartitioningStrategy
implements PartitioningStrategy {
    public static final StringAndPartitionAwarePartitioningStrategy INSTANCE = new StringAndPartitionAwarePartitioningStrategy();

    private StringAndPartitionAwarePartitioningStrategy() {
    }

    public Object getPartitionKey(Object key) {
        if (key instanceof String) {
            return StringPartitioningStrategy.getPartitionKey((String)key);
        }
        if (key instanceof PartitionAware) {
            return ((PartitionAware)key).getPartitionKey();
        }
        return null;
    }
}

