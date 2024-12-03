/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheEntryEvent
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CacheEntryEvent;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;

public class DefaultCacheEntryEvent<K, V>
implements CacheEntryEvent<K, V> {
    private final K key;
    private final V value;
    private final V oldValue;

    public DefaultCacheEntryEvent(@Nonnull K key, V value, V oldValue) {
        this.key = Preconditions.checkNotNull(key);
        this.value = value;
        this.oldValue = oldValue;
    }

    public DefaultCacheEntryEvent(@Nonnull K key) {
        this(key, null, null);
    }

    @Nonnull
    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public V getOldValue() {
        return this.oldValue;
    }
}

