/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache$Entry
 */
package com.hazelcast.cache.impl;

import java.util.Map;
import javax.cache.Cache;

public class CacheEntry<K, V>
implements Cache.Entry<K, V>,
Map.Entry<K, V> {
    private final K key;
    private V value;

    public CacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.getClass())) {
            return clazz.cast(this);
        }
        throw new IllegalArgumentException("Unwrapping to " + clazz + " is not supported by this implementation");
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
}

