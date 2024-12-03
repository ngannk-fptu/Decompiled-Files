/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.util;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class ConcurrentCache<K, V> {
    private final int size;
    private final Map<K, V> eden;
    private final Map<K, V> longterm;

    public ConcurrentCache(int size) {
        this.size = size;
        this.eden = new ConcurrentHashMap(size);
        this.longterm = new WeakHashMap(size);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V get(K k) {
        V v = this.eden.get(k);
        if (v == null) {
            Map<K, V> map = this.longterm;
            synchronized (map) {
                v = this.longterm.get(k);
            }
            if (v != null) {
                this.eden.put(k, v);
            }
        }
        return v;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void put(K k, V v) {
        if (this.eden.size() >= this.size) {
            Map<K, V> map = this.longterm;
            synchronized (map) {
                this.longterm.putAll(this.eden);
            }
            this.eden.clear();
        }
        this.eden.put(k, v);
    }
}

