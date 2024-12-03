/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.core.PartitioningStrategy;

public class PartitioningStrategyConfigReadOnly
extends PartitioningStrategyConfig {
    public PartitioningStrategyConfigReadOnly(PartitioningStrategyConfig config) {
        super(config);
    }

    @Override
    public PartitioningStrategyConfig setPartitioningStrategyClass(String partitionStrategyClass) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public PartitioningStrategyConfig setPartitionStrategy(PartitioningStrategy partitionStrategy) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public PartitioningStrategyConfig setPartitioningStrategy(PartitioningStrategy partitionStrategy) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

