/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.hibernate.internal.util.collections.BoundedConcurrentHashMap;

final class StatsNamedContainer<V> {
    private final ConcurrentMap<String, Object> map;
    private static final Object NULL_TOKEN = new Object();

    StatsNamedContainer(int capacity, int concurrencyLevel) {
        this.map = new BoundedConcurrentHashMap<String, Object>(capacity, concurrencyLevel, BoundedConcurrentHashMap.Eviction.LRU);
    }

    StatsNamedContainer() {
        this.map = new ConcurrentHashMap<String, Object>();
    }

    public void clear() {
        this.map.clear();
    }

    public String[] keysAsArray() {
        return this.map.keySet().toArray(new String[0]);
    }

    public V getOrCompute(String key, Function<String, V> function) {
        Object v1 = this.map.get(key);
        if (v1 != null) {
            if (v1 == NULL_TOKEN) {
                return null;
            }
            return v1;
        }
        V v2 = function.apply(key);
        if (v2 == null) {
            this.map.put(key, NULL_TOKEN);
            return null;
        }
        Object v3 = this.map.putIfAbsent(key, v2);
        if (v3 == null) {
            return v2;
        }
        return (V)v3;
    }

    public V get(String key) {
        Object o = this.map.get(key);
        if (o == NULL_TOKEN) {
            return null;
        }
        return o;
    }
}

