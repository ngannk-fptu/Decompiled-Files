/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheEvictionConfigReadOnly;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.util.Preconditions;

@Deprecated
@BinaryInterface
public class CacheEvictionConfig
extends EvictionConfig {
    public CacheEvictionConfig() {
    }

    public CacheEvictionConfig(int size, EvictionConfig.MaxSizePolicy maxSizePolicy, EvictionPolicy evictionPolicy) {
        super(size, maxSizePolicy, evictionPolicy);
    }

    public CacheEvictionConfig(int size, CacheMaxSizePolicy cacheMaxSizePolicy, EvictionPolicy evictionPolicy) {
        super(size, Preconditions.checkNotNull(cacheMaxSizePolicy, "Cache max-size policy cannot be null!").toMaxSizePolicy(), evictionPolicy);
    }

    public CacheEvictionConfig(int size, EvictionConfig.MaxSizePolicy maxSizePolicy, String comparatorClassName) {
        super(size, maxSizePolicy, comparatorClassName);
    }

    public CacheEvictionConfig(int size, CacheMaxSizePolicy cacheMaxSizePolicy, String comparatorClassName) {
        super(size, Preconditions.checkNotNull(cacheMaxSizePolicy, "Cache max-size policy cannot be null!").toMaxSizePolicy(), comparatorClassName);
    }

    public CacheEvictionConfig(int size, EvictionConfig.MaxSizePolicy maxSizePolicy, EvictionPolicyComparator comparator) {
        super(size, maxSizePolicy, comparator);
    }

    public CacheEvictionConfig(int size, CacheMaxSizePolicy cacheMaxSizePolicy, EvictionPolicyComparator comparator) {
        super(size, Preconditions.checkNotNull(cacheMaxSizePolicy, "Cache max-size policy cannot be null!").toMaxSizePolicy(), comparator);
    }

    public CacheEvictionConfig(EvictionConfig config) {
        super(config);
    }

    @Override
    public CacheEvictionConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new CacheEvictionConfigReadOnly(this);
        }
        return (CacheEvictionConfig)this.readOnly;
    }

    public CacheMaxSizePolicy getMaxSizePolicy() {
        return CacheMaxSizePolicy.fromMaxSizePolicy(this.getMaximumSizePolicy());
    }

    public CacheEvictionConfig setMaxSizePolicy(CacheMaxSizePolicy cacheMaxSizePolicy) {
        Preconditions.checkNotNull(cacheMaxSizePolicy, "Cache Max-Size policy cannot be null!");
        this.setMaximumSizePolicy(cacheMaxSizePolicy.toMaxSizePolicy());
        return this;
    }

    @Override
    public CacheEvictionConfig setMaximumSizePolicy(EvictionConfig.MaxSizePolicy maxSizePolicy) {
        super.setMaximumSizePolicy(maxSizePolicy);
        return this;
    }

    @Override
    public CacheEvictionConfig setSize(int size) {
        super.setSize(size);
        return this;
    }

    @Override
    public CacheEvictionConfig setEvictionPolicy(EvictionPolicy evictionPolicy) {
        super.setEvictionPolicy(evictionPolicy);
        return this;
    }

    @Override
    public CacheEvictionConfig setComparatorClassName(String comparatorClassName) {
        super.setComparatorClassName(comparatorClassName);
        return this;
    }

    @Override
    public CacheEvictionConfig setComparator(EvictionPolicyComparator comparator) {
        super.setComparator(comparator);
        return this;
    }

    @Override
    public String toString() {
        return "CacheEvictionConfig{size=" + this.size + ", maxSizePolicy=" + (Object)((Object)this.maxSizePolicy) + ", evictionPolicy=" + (Object)((Object)this.evictionPolicy) + ", comparatorClassName=" + this.comparatorClassName + ", comparator=" + this.comparator + '}';
    }

    public static enum CacheMaxSizePolicy {
        ENTRY_COUNT,
        USED_NATIVE_MEMORY_SIZE,
        USED_NATIVE_MEMORY_PERCENTAGE,
        FREE_NATIVE_MEMORY_SIZE,
        FREE_NATIVE_MEMORY_PERCENTAGE;


        public EvictionConfig.MaxSizePolicy toMaxSizePolicy() {
            switch (this) {
                case ENTRY_COUNT: {
                    return EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
                }
                case USED_NATIVE_MEMORY_SIZE: {
                    return EvictionConfig.MaxSizePolicy.USED_NATIVE_MEMORY_SIZE;
                }
                case USED_NATIVE_MEMORY_PERCENTAGE: {
                    return EvictionConfig.MaxSizePolicy.USED_NATIVE_MEMORY_PERCENTAGE;
                }
                case FREE_NATIVE_MEMORY_SIZE: {
                    return EvictionConfig.MaxSizePolicy.FREE_NATIVE_MEMORY_SIZE;
                }
                case FREE_NATIVE_MEMORY_PERCENTAGE: {
                    return EvictionConfig.MaxSizePolicy.FREE_NATIVE_MEMORY_PERCENTAGE;
                }
            }
            throw new IllegalArgumentException("Invalid Cache Max-Size policy for converting to MaxSizePolicy");
        }

        public static CacheMaxSizePolicy fromMaxSizePolicy(EvictionConfig.MaxSizePolicy maxSizePolicy) {
            switch (maxSizePolicy) {
                case ENTRY_COUNT: {
                    return ENTRY_COUNT;
                }
                case USED_NATIVE_MEMORY_SIZE: {
                    return USED_NATIVE_MEMORY_SIZE;
                }
                case USED_NATIVE_MEMORY_PERCENTAGE: {
                    return USED_NATIVE_MEMORY_PERCENTAGE;
                }
                case FREE_NATIVE_MEMORY_SIZE: {
                    return FREE_NATIVE_MEMORY_SIZE;
                }
                case FREE_NATIVE_MEMORY_PERCENTAGE: {
                    return FREE_NATIVE_MEMORY_PERCENTAGE;
                }
            }
            throw new IllegalArgumentException("Invalid Max-Size policy for converting to CacheMaxSizePolicy");
        }
    }
}

