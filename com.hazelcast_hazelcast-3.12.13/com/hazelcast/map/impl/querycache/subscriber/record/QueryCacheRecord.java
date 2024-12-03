/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.record;

import com.hazelcast.internal.eviction.Evictable;

public interface QueryCacheRecord<V>
extends Evictable {
    @Override
    public V getValue();

    public void setAccessTime(long var1);

    public void incrementAccessHit();
}

