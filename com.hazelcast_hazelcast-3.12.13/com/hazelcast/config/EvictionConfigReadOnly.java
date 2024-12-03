/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.nio.serialization.BinaryInterface;

@BinaryInterface
public class EvictionConfigReadOnly
extends EvictionConfig {
    public EvictionConfigReadOnly(EvictionConfig config) {
        super(config);
    }

    @Override
    public EvictionConfigReadOnly setSize(int size) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public EvictionConfigReadOnly setMaximumSizePolicy(EvictionConfig.MaxSizePolicy maxSizePolicy) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public EvictionConfigReadOnly setEvictionPolicy(EvictionPolicy evictionPolicy) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public EvictionConfig setComparatorClassName(String comparatorClassName) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public EvictionConfig setComparator(EvictionPolicyComparator comparator) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

