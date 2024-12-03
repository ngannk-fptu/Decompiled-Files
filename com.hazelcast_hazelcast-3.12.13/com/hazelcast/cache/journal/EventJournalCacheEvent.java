/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.journal;

import com.hazelcast.cache.CacheEventType;

public interface EventJournalCacheEvent<K, V> {
    public K getKey();

    public V getNewValue();

    public V getOldValue();

    public CacheEventType getType();
}

