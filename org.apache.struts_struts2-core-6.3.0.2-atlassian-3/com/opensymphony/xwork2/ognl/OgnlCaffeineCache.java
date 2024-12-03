/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.benmanes.caffeine.cache.Cache
 *  com.github.benmanes.caffeine.cache.Caffeine
 *  com.github.benmanes.caffeine.cache.Policy$Eviction
 */
package com.opensymphony.xwork2.ognl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Policy;
import com.opensymphony.xwork2.ognl.OgnlCache;

public class OgnlCaffeineCache<K, V>
implements OgnlCache<K, V> {
    private final Cache<K, V> cache;

    public OgnlCaffeineCache(int evictionLimit, int initialCapacity) {
        this.cache = Caffeine.newBuilder().initialCapacity(initialCapacity).maximumSize((long)evictionLimit).build();
    }

    @Override
    public V get(K key) {
        return (V)this.cache.getIfPresent(key);
    }

    @Override
    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    @Override
    public void putIfAbsent(K key, V value) {
        this.cache.asMap().putIfAbsent(key, value);
    }

    @Override
    public int size() {
        return this.cache.asMap().size();
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

    @Override
    public int getEvictionLimit() {
        return Math.toIntExact(((Policy.Eviction)this.cache.policy().eviction().orElseThrow(IllegalStateException::new)).getMaximum());
    }

    @Override
    public void setEvictionLimit(int cacheEvictionLimit) {
        ((Policy.Eviction)this.cache.policy().eviction().orElseThrow(IllegalStateException::new)).setMaximum((long)cacheEvictionLimit);
    }
}

