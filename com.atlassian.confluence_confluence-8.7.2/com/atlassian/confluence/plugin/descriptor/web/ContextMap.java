/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ContextMap<K, V>
extends Map<K, V> {
    public Map<K, V> toMap();

    @Override
    default public int size() {
        return this.toMap().size();
    }

    @Override
    default public boolean isEmpty() {
        return this.toMap().isEmpty();
    }

    @Override
    default public boolean containsKey(Object key) {
        return this.toMap().containsKey(key);
    }

    @Override
    default public boolean containsValue(Object value) {
        return this.toMap().containsValue(value);
    }

    @Override
    default public V get(Object key) {
        return this.toMap().get(key);
    }

    @Override
    default public V put(K key, V value) {
        return this.toMap().put(key, value);
    }

    @Override
    default public V remove(Object key) {
        return this.toMap().remove(key);
    }

    @Override
    default public void putAll(Map<? extends K, ? extends V> m) {
        this.toMap().putAll(m);
    }

    @Override
    default public void clear() {
        this.toMap().clear();
    }

    @Override
    default public Set<K> keySet() {
        return this.toMap().keySet();
    }

    @Override
    default public Collection<V> values() {
        return this.toMap().values();
    }

    @Override
    default public Set<Map.Entry<K, V>> entrySet() {
        return this.toMap().entrySet();
    }
}

