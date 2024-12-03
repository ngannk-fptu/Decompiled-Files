/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.ManagedCache
 *  javax.annotation.Nullable
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.Cache;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.hazelcast.asyncinvalidation.AbstractDelegatingCache;
import javax.annotation.Nullable;

abstract class AbstractAsyncHybridCache<K, V>
extends AbstractDelegatingCache<K, V> {
    public AbstractAsyncHybridCache(Cache<K, V> localCache, ManagedCache localManagedCache) {
        super(localCache, localManagedCache);
    }

    @Override
    public void clear() {
        super.clear();
        this.onClear();
    }

    public final boolean isReplicateAsynchronously() {
        return true;
    }

    @Override
    public void put(K key, V value) {
        super.put(key, value);
        this.onPut(key, value);
    }

    @Override
    @Nullable
    public V putIfAbsent(K key, V value) {
        V existingValue = super.putIfAbsent(key, value);
        if (existingValue == null) {
            this.onPut(key, value);
        }
        return existingValue;
    }

    @Override
    public void remove(K key) {
        super.remove(key);
        this.onRemove(key);
    }

    @Override
    public boolean remove(K key, V value) {
        boolean removed = super.remove(key, value);
        if (removed) {
            this.onRemove(key);
        }
        return removed;
    }

    @Override
    public void removeAll() {
        super.removeAll();
        this.onClear();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean replaced = super.replace(key, oldValue, newValue);
        if (replaced) {
            this.onPut(key, newValue);
        }
        return replaced;
    }

    public boolean isLocal() {
        return false;
    }

    protected abstract void onPut(K var1, V var2);

    protected abstract void onRemove(K var1);

    protected abstract void onClear();
}

