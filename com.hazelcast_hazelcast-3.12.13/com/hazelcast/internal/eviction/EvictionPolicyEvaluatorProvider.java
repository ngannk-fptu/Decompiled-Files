/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.EvictionConfiguration;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.internal.eviction.impl.comparator.LFUEvictionPolicyComparator;
import com.hazelcast.internal.eviction.impl.comparator.LRUEvictionPolicyComparator;
import com.hazelcast.internal.eviction.impl.comparator.RandomEvictionPolicyComparator;
import com.hazelcast.internal.eviction.impl.evaluator.EvictionPolicyEvaluator;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;

public final class EvictionPolicyEvaluatorProvider {
    private EvictionPolicyEvaluatorProvider() {
    }

    private static EvictionPolicyComparator createEvictionPolicyComparator(EvictionPolicy evictionPolicy) {
        switch (evictionPolicy) {
            case LRU: {
                return new LRUEvictionPolicyComparator();
            }
            case LFU: {
                return new LFUEvictionPolicyComparator();
            }
            case RANDOM: {
                return new RandomEvictionPolicyComparator();
            }
            case NONE: {
                return null;
            }
        }
        throw new IllegalArgumentException("Unsupported eviction policy: " + (Object)((Object)evictionPolicy));
    }

    public static <A, E extends Evictable> EvictionPolicyEvaluator<A, E> getEvictionPolicyEvaluator(EvictionConfiguration evictionConfig, ClassLoader classLoader) {
        EvictionPolicyComparator evictionPolicyComparator;
        Preconditions.checkNotNull(evictionConfig);
        String evictionPolicyComparatorClassName = evictionConfig.getComparatorClassName();
        if (!StringUtil.isNullOrEmpty(evictionPolicyComparatorClassName)) {
            try {
                evictionPolicyComparator = (EvictionPolicyComparator)ClassLoaderUtil.newInstance(classLoader, evictionPolicyComparatorClassName);
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        } else {
            EvictionPolicyComparator comparator = evictionConfig.getComparator();
            evictionPolicyComparator = comparator != null ? comparator : EvictionPolicyEvaluatorProvider.createEvictionPolicyComparator(evictionConfig.getEvictionPolicy());
        }
        return new EvictionPolicyEvaluator(evictionPolicyComparator);
    }
}

