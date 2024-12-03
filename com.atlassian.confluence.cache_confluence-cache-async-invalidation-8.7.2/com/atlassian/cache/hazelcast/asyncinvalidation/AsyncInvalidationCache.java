/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.ManagedCache
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.Cache;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.hazelcast.asyncinvalidation.AbstractAsyncHybridCache;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidator;

public final class AsyncInvalidationCache<K, V>
extends AbstractAsyncHybridCache<K, V> {
    private final CacheInvalidator<K> invalidator;

    public AsyncInvalidationCache(Cache<K, V> localCache, ManagedCache localManagedCache, CacheInvalidator<K> invalidator) {
        super(localCache, localManagedCache);
        this.invalidator = invalidator;
    }

    public boolean isReplicateViaCopy() {
        return false;
    }

    @Override
    protected void onPut(K key, V value) {
        this.invalidator.invalidateEntry(key);
    }

    @Override
    protected void onRemove(K key) {
        this.invalidator.invalidateEntry(key);
    }

    @Override
    protected void onClear() {
        this.invalidator.invalidateAllEntries();
    }
}

