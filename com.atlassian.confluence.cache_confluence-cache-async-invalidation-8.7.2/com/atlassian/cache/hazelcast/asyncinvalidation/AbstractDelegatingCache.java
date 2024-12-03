/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.hazelcast.ManagedHybridCacheSupport
 *  javax.annotation.Nullable
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.hazelcast.ManagedHybridCacheSupport;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;

abstract class AbstractDelegatingCache<K, V>
extends ManagedHybridCacheSupport
implements Cache<K, V> {
    private final Cache<K, V> cache;
    private final ManagedCache managedCache;

    public AbstractDelegatingCache(Cache<K, V> cache, ManagedCache managedCache) {
        super(cache.getName(), null);
        this.cache = cache;
        this.managedCache = managedCache;
    }

    protected ManagedCache getLocalCache() {
        return this.managedCache;
    }

    public void clear() {
        this.managedCache.clear();
    }

    public boolean isFlushable() {
        return true;
    }

    public boolean containsKey(K key) {
        return this.cache.containsKey(key);
    }

    public Collection<K> getKeys() {
        return this.cache.getKeys();
    }

    @Nullable
    public V get(K key) {
        return (V)this.cache.get(key);
    }

    public V get(K key, Supplier<? extends V> valueSupplier) {
        return (V)this.cache.get(key, valueSupplier);
    }

    public Map<K, V> getBulk(Set<K> keys, Function<Set<K>, Map<K, V>> valuesSupplier) {
        return this.cache.getBulk(keys, valuesSupplier);
    }

    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    @Nullable
    public V putIfAbsent(K key, V value) {
        return (V)this.cache.putIfAbsent(key, value);
    }

    public void remove(K key) {
        this.cache.remove(key);
    }

    public boolean remove(K key, V value) {
        return this.cache.remove(key, value);
    }

    public void removeAll() {
        this.cache.removeAll();
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return this.cache.replace(key, oldValue, newValue);
    }

    public void addListener(CacheEntryListener<K, V> listener, boolean includeValues) {
        this.cache.addListener(listener, includeValues);
    }

    public void removeListener(CacheEntryListener<K, V> listener) {
        this.cache.removeListener(listener);
    }
}

