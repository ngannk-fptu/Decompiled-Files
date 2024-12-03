/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ConcurrentSoftHashMap<K, V>
extends AbstractMap<K, V>
implements ConcurrentMap<K, V> {
    private ConcurrentHashMap<K, SoftReference<V>> map = new ConcurrentHashMap();

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
        return this.unwrap(this.map.get(key));
    }

    @Override
    public V put(K key, V value) {
        return this.unwrap(this.map.put(key, this.wrap(value)));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return this.unwrap(this.map.putIfAbsent(key, this.wrap(value)));
    }

    @Override
    public V remove(Object key) {
        return this.unwrap(this.map.remove(key));
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V replace(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    private SoftReference<V> wrap(V value) {
        return new SoftReference<V>(value);
    }

    private V unwrap(SoftReference<V> value) {
        return value == null ? null : (V)value.get();
    }
}

