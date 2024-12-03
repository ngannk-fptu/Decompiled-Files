/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ognl.OgnlCache;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OgnlLRUCache<K, V>
implements OgnlCache<K, V> {
    private final Map<K, V> ognlLRUCache;
    private final AtomicInteger cacheEvictionLimit;

    public OgnlLRUCache(int evictionLimit, int initialCapacity, float loadFactor) {
        this.cacheEvictionLimit = new AtomicInteger(evictionLimit);
        this.ognlLRUCache = Collections.synchronizedMap(new LinkedHashMap<K, V>(initialCapacity, loadFactor, true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return this.size() > OgnlLRUCache.this.cacheEvictionLimit.get();
            }
        });
    }

    @Override
    public V get(K key) {
        return this.ognlLRUCache.get(key);
    }

    @Override
    public void put(K key, V value) {
        this.ognlLRUCache.put(key, value);
    }

    @Override
    public void putIfAbsent(K key, V value) {
        this.ognlLRUCache.putIfAbsent(key, value);
    }

    @Override
    public int size() {
        return this.ognlLRUCache.size();
    }

    @Override
    public void clear() {
        this.ognlLRUCache.clear();
    }

    @Override
    public int getEvictionLimit() {
        return this.cacheEvictionLimit.get();
    }

    @Override
    public void setEvictionLimit(int cacheEvictionLimit) {
        if (cacheEvictionLimit < this.size()) {
            this.clear();
        }
        this.cacheEvictionLimit.set(cacheEvictionLimit);
    }
}

