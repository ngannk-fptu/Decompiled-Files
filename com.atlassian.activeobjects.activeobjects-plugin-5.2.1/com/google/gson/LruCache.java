/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Cache;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class LruCache<K, V>
extends LinkedHashMap<K, V>
implements Cache<K, V> {
    private static final long serialVersionUID = 1L;
    private final int maxCapacity;

    public LruCache(int maxCapacity) {
        super(maxCapacity, 0.7f, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    public synchronized void addElement(K key, V value) {
        this.put(key, value);
    }

    @Override
    public synchronized V getElement(K key) {
        return this.get(key);
    }

    @Override
    public synchronized V removeElement(K key) {
        return this.remove(key);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
        return this.size() > this.maxCapacity;
    }
}

