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
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheReplicator;

public final class AsyncReplicationCache<K, V>
extends AbstractAsyncHybridCache<K, V> {
    private final CacheInvalidator<K> invalidator;
    private final CacheReplicator<K, V> replicator;

    public AsyncReplicationCache(Cache<K, V> localCache, ManagedCache localManagedCache, CacheInvalidator<K> invalidator, CacheReplicator<K, V> replicator) {
        super(localCache, localManagedCache);
        this.invalidator = invalidator;
        this.replicator = replicator;
    }

    public boolean isReplicateViaCopy() {
        return true;
    }

    @Override
    protected void onPut(K key, V value) {
        this.replicator.replicate(key, value);
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

