/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.client.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class CopyOnWriteHashMap<K, V>
implements Map<K, V> {
    private volatile Map<K, V> core;
    volatile Map<K, V> view;
    private final AtomicBoolean requiresCopyOnWrite;

    public CopyOnWriteHashMap() {
        this.core = new HashMap();
        this.requiresCopyOnWrite = new AtomicBoolean(false);
    }

    private CopyOnWriteHashMap(CopyOnWriteHashMap<K, V> that) {
        this.core = that.core;
        this.requiresCopyOnWrite = new AtomicBoolean(true);
    }

    public CopyOnWriteHashMap<K, V> clone() {
        try {
            CopyOnWriteHashMap<K, V> copyOnWriteHashMap = new CopyOnWriteHashMap<K, V>(this);
            return copyOnWriteHashMap;
        }
        finally {
            this.requiresCopyOnWrite.set(true);
        }
    }

    private void copy() {
        if (this.requiresCopyOnWrite.compareAndSet(true, false)) {
            this.core = new HashMap<K, V>(this.core);
            this.view = null;
        }
    }

    @Override
    public int size() {
        return this.core.size();
    }

    @Override
    public boolean isEmpty() {
        return this.core.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.core.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.core.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.core.get(key);
    }

    @Override
    public V put(K key, V value) {
        this.copy();
        return this.core.put(key, value);
    }

    @Override
    public V remove(Object key) {
        this.copy();
        return this.core.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        this.copy();
        this.core.putAll(t);
    }

    @Override
    public void clear() {
        this.core = new HashMap();
        this.view = null;
        this.copy();
    }

    @Override
    public Set<K> keySet() {
        return this.getView().keySet();
    }

    @Override
    public Collection<V> values() {
        return this.getView().values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.getView().entrySet();
    }

    public String toString() {
        return this.core.toString();
    }

    private Map<K, V> getView() {
        Map<K, V> result = this.view;
        if (result == null) {
            this.view = result = Collections.unmodifiableMap(this.core);
        }
        return result;
    }
}

