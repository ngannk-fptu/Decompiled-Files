/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.internal.eviction.EvictionPolicyType;
import com.hazelcast.internal.eviction.EvictionStrategyType;

public interface EvictionConfiguration {
    public EvictionStrategyType getEvictionStrategyType();

    public EvictionPolicy getEvictionPolicy();

    @Deprecated
    public EvictionPolicyType getEvictionPolicyType();

    public String getComparatorClassName();

    public EvictionPolicyComparator getComparator();
}

