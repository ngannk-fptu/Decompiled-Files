/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.collections;

import java.util.Map;

class LazyMapEntry<K, V>
implements Map.Entry<K, V> {
    private final Map<K, V> map;
    private final K key;

    LazyMapEntry(Map<K, V> map, K key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.map.get(this.key);
    }

    @Override
    public V setValue(V value) {
        return this.map.put(this.key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        V value = this.getValue();
        Map.Entry e = (Map.Entry)o;
        return LazyMapEntry.eq(this.key, e.getKey()) && LazyMapEntry.eq(value, e.getValue());
    }

    @Override
    public int hashCode() {
        V value = this.getValue();
        return (this.key == null ? 0 : this.key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    private static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }
}

