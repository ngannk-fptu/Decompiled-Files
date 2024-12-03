/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheEvictionConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.nio.serialization.BinaryInterface;

@Deprecated
@BinaryInterface
public class CacheEvictionConfigReadOnly
extends CacheEvictionConfig {
    public CacheEvictionConfigReadOnly(EvictionConfig config) {
        super(config);
    }

    @Override
    public CacheEvictionConfigReadOnly setSize(int size) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public CacheEvictionConfigReadOnly setMaximumSizePolicy(EvictionConfig.MaxSizePolicy maxSizePolicy) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public CacheEvictionConfig setMaxSizePolicy(CacheEvictionConfig.CacheMaxSizePolicy cacheMaxSizePolicy) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public CacheEvictionConfigReadOnly setEvictionPolicy(EvictionPolicy evictionPolicy) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public CacheEvictionConfig setComparatorClassName(String comparatorClassName) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public CacheEvictionConfig setComparator(EvictionPolicyComparator comparator) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

