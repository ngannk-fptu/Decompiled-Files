/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.EvictableEntryView;

public interface EvictionCandidate<A, E extends Evictable>
extends EvictableEntryView {
    public A getAccessor();

    public E getEvictable();
}

