/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheLoader
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.hazelcast.CacheVersion;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;

public class CacheVersionAwareCacheLoader<K, V>
implements CacheLoader<K, V> {
    private final CacheLoader<K, V> delegate;
    private final CacheVersion cacheVersion;

    CacheVersionAwareCacheLoader(CacheLoader<K, V> delegate, CacheVersion cacheVersion) {
        this.delegate = (CacheLoader)Preconditions.checkNotNull(delegate);
        this.cacheVersion = (CacheVersion)Preconditions.checkNotNull((Object)cacheVersion);
    }

    @Nonnull
    public V load(@Nonnull K key) {
        Object v;
        long currentVersion;
        do {
            currentVersion = this.cacheVersion.get();
            v = this.delegate.load(key);
        } while (currentVersion != this.cacheVersion.get());
        return (V)v;
    }
}

