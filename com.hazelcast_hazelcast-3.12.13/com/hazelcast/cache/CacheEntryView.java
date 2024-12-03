/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache;

import com.hazelcast.internal.eviction.EvictableEntryView;

public interface CacheEntryView<K, V>
extends EvictableEntryView<K, V> {
    @Override
    public K getKey();

    @Override
    public V getValue();

    public long getExpirationTime();

    @Override
    public long getLastAccessTime();

    @Override
    public long getAccessHit();

    public Object getExpiryPolicy();
}

