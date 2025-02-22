/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.strategy;

import com.hazelcast.core.PartitionAware;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.serialization.SerializableByConvention;

@SerializableByConvention
public class DefaultPartitioningStrategy
implements PartitioningStrategy {
    public Object getPartitionKey(Object key) {
        if (key instanceof PartitionAware) {
            return ((PartitionAware)key).getPartitionKey();
        }
        return null;
    }
}

