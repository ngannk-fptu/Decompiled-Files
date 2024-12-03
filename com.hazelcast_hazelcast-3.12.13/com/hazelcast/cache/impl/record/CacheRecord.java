/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.record;

import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.Expirable;
import com.hazelcast.nio.serialization.impl.Versioned;

public interface CacheRecord<V, E>
extends Expirable,
Evictable<V>,
Versioned {
    public static final long TIME_NOT_AVAILABLE = -1L;

    public void setValue(V var1);

    public void setCreationTime(long var1);

    public void setAccessTime(long var1);

    public void setAccessHit(int var1);

    public void incrementAccessHit();

    public void resetAccessHit();

    public void setExpiryPolicy(E var1);

    public E getExpiryPolicy();
}

