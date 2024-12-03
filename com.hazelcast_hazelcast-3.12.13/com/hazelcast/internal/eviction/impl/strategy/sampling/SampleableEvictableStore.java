/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction.impl.strategy.sampling;

import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.EvictableStore;
import com.hazelcast.internal.eviction.EvictionCandidate;

public interface SampleableEvictableStore<A, E extends Evictable>
extends EvictableStore<A, E> {
    public <C extends EvictionCandidate<A, E>> Iterable<C> sample(int var1);
}

