/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.sun.jersey.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;

public class UnmodifiableMultivaluedMap<K, V>
implements MultivaluedMap<K, V> {
    private final MultivaluedMap<K, V> delegate;

    public UnmodifiableMultivaluedMap(MultivaluedMap<K, V> delegate) {
        this.delegate = delegate;
    }

    public void putSingle(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public void add(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public V getFirst(K key) {
        return (V)this.delegate.getFirst(key);
    }

    public int size() {
        return this.delegate.size();
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    public List<V> get(Object key) {
        return (List)this.delegate.get(key);
    }

    public List<V> put(K key, List<V> value) {
        throw new UnsupportedOperationException();
    }

    public List<V> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends K, ? extends List<V>> m) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }

    public Collection<List<V>> values() {
        return Collections.unmodifiableCollection(this.delegate.values());
    }

    public Set<Map.Entry<K, List<V>>> entrySet() {
        return Collections.unmodifiableSet(this.delegate.entrySet());
    }
}

