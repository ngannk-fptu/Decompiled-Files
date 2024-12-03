/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImmutableMapParameter<K, V>
implements Map<K, V> {
    private static final String UNMODIFIABLE_MESSAGE = "This is an immutable map.";
    private static final String DUPLICATED_KEY_MESSAGE = "Duplicate keys are provided.";
    private final Map<K, V> map;

    private ImmutableMapParameter(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder();
    }

    public static <K, V> ImmutableMapParameter<K, V> of(K k0, V v0) {
        Map<K, V> map = Collections.singletonMap(k0, v0);
        return new ImmutableMapParameter<K, V>(map);
    }

    public static <K, V> ImmutableMapParameter<K, V> of(K k0, V v0, K k1, V v1) {
        HashMap map = new HashMap();
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k0, v0);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k1, v1);
        return new ImmutableMapParameter(map);
    }

    public static <K, V> ImmutableMapParameter<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
        HashMap map = new HashMap();
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k0, v0);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k1, v1);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k2, v2);
        return new ImmutableMapParameter(map);
    }

    public static <K, V> ImmutableMapParameter<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
        HashMap map = new HashMap();
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k0, v0);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k1, v1);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k2, v2);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k3, v3);
        return new ImmutableMapParameter(map);
    }

    public static <K, V> ImmutableMapParameter<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        HashMap map = new HashMap();
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k0, v0);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k1, v1);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k2, v2);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k3, v3);
        ImmutableMapParameter.putAndWarnDuplicateKeys(map, k4, v4);
        return new ImmutableMapParameter(map);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public V get(Object key) {
        return this.map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Collection<V> values() {
        return this.map.values();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    private static <K, V> void putAndWarnDuplicateKeys(Map<K, V> map, K key, V value) {
        if (map.containsKey(key)) {
            throw new IllegalArgumentException(DUPLICATED_KEY_MESSAGE);
        }
        map.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        return this.map.equals(o);
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    public String toString() {
        return this.map.toString();
    }

    public static class Builder<K, V> {
        private final Map<K, V> entries = new HashMap();

        public Builder<K, V> put(K key, V value) {
            ImmutableMapParameter.putAndWarnDuplicateKeys(this.entries, key, value);
            return this;
        }

        public ImmutableMapParameter<K, V> build() {
            HashMap<K, V> builtMap = new HashMap<K, V>();
            builtMap.putAll(this.entries);
            return new ImmutableMapParameter(builtMap);
        }
    }
}

