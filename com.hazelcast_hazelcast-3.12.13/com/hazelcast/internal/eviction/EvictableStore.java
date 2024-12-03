/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.EvictionCandidate;
import com.hazelcast.internal.eviction.EvictionListener;

public interface EvictableStore<A, E extends Evictable> {
    public <C extends EvictionCandidate<A, E>> boolean tryEvict(C var1, EvictionListener<A, E> var2);
}

