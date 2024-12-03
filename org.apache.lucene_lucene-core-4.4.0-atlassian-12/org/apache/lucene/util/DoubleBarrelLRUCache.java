/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class DoubleBarrelLRUCache<K extends CloneableKey, V> {
    private final Map<K, V> cache1;
    private final Map<K, V> cache2;
    private final AtomicInteger countdown;
    private volatile boolean swapped;
    private final int maxSize;

    public DoubleBarrelLRUCache(int maxSize) {
        this.maxSize = maxSize;
        this.countdown = new AtomicInteger(maxSize);
        this.cache1 = new ConcurrentHashMap();
        this.cache2 = new ConcurrentHashMap();
    }

    public V get(K key) {
        Map<K, V> secondary;
        Map<K, V> primary;
        if (this.swapped) {
            primary = this.cache2;
            secondary = this.cache1;
        } else {
            primary = this.cache1;
            secondary = this.cache2;
        }
        V result = primary.get(key);
        if (result == null && (result = secondary.get(key)) != null) {
            this.put(((CloneableKey)key).clone(), result);
        }
        return result;
    }

    public void put(K key, V value) {
        Map<K, V> secondary;
        Map<K, V> primary;
        if (this.swapped) {
            primary = this.cache2;
            secondary = this.cache1;
        } else {
            primary = this.cache1;
            secondary = this.cache2;
        }
        primary.put(key, value);
        if (this.countdown.decrementAndGet() == 0) {
            secondary.clear();
            this.swapped = !this.swapped;
            this.countdown.set(this.maxSize);
        }
    }

    public static abstract class CloneableKey {
        public abstract CloneableKey clone();
    }
}

