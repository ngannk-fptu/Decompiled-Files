/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ognl.OgnlCache;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OgnlDefaultCache<K, V>
implements OgnlCache<K, V> {
    private final ConcurrentHashMap<K, V> ognlCache;
    private final AtomicInteger cacheEvictionLimit;

    public OgnlDefaultCache(int evictionLimit, int initialCapacity, float loadFactor) {
        this.cacheEvictionLimit = new AtomicInteger(evictionLimit);
        this.ognlCache = new ConcurrentHashMap(initialCapacity, loadFactor);
    }

    @Override
    public V get(K key) {
        return this.ognlCache.get(key);
    }

    @Override
    public void put(K key, V value) {
        this.ognlCache.put(key, value);
        this.clearIfEvictionLimitExceeded();
    }

    @Override
    public void putIfAbsent(K key, V value) {
        this.ognlCache.putIfAbsent(key, value);
        this.clearIfEvictionLimitExceeded();
    }

    @Override
    public int size() {
        return this.ognlCache.size();
    }

    @Override
    public void clear() {
        this.ognlCache.clear();
    }

    @Override
    public int getEvictionLimit() {
        return this.cacheEvictionLimit.get();
    }

    @Override
    public void setEvictionLimit(int cacheEvictionLimit) {
        this.cacheEvictionLimit.set(cacheEvictionLimit);
    }

    private void clearIfEvictionLimitExceeded() {
        if (this.ognlCache.size() > this.cacheEvictionLimit.get()) {
            this.ognlCache.clear();
        }
    }
}

