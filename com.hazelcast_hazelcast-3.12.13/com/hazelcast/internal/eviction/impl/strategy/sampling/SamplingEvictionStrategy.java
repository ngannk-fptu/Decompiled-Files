/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction.impl.strategy.sampling;

import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.EvictionChecker;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.internal.eviction.impl.evaluator.EvictionPolicyEvaluator;
import com.hazelcast.internal.eviction.impl.strategy.sampling.SampleableEvictableStore;

public final class SamplingEvictionStrategy<A, E extends Evictable, S extends SampleableEvictableStore<A, E>> {
    public static final SamplingEvictionStrategy INSTANCE = new SamplingEvictionStrategy();
    private static final int SAMPLE_COUNT = 15;

    private SamplingEvictionStrategy() {
    }

    public boolean evict(S evictableStore, EvictionPolicyEvaluator<A, E> evictionPolicyEvaluator, EvictionChecker evictionChecker, EvictionListener<A, E> evictionListener) {
        if (evictionChecker != null) {
            if (evictionChecker.isEvictionRequired()) {
                return this.evictInternal(evictableStore, evictionPolicyEvaluator, evictionListener);
            }
            return false;
        }
        return this.evictInternal(evictableStore, evictionPolicyEvaluator, evictionListener);
    }

    protected boolean evictInternal(S sampleableEvictableStore, EvictionPolicyEvaluator<A, E> evictionPolicyEvaluator, EvictionListener<A, E> evictionListener) {
        Iterable samples = sampleableEvictableStore.sample(15);
        Object evictionCandidate = evictionPolicyEvaluator.evaluate(samples);
        return sampleableEvictableStore.tryEvict(evictionCandidate, evictionListener);
    }
}

