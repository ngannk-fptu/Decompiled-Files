/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InternalListMultiMap<K, V> {
    private final Map<K, List<V>> backingMap = new HashMap<K, List<V>>();

    public void put(K key, V value) {
        List<V> values = this.backingMap.get(key);
        if (values == null) {
            values = new ArrayList<V>();
            this.backingMap.put(key, values);
        }
        values.add(value);
    }

    public Collection<V> get(K key) {
        return this.backingMap.get(key);
    }

    public Set<Map.Entry<K, List<V>>> entrySet() {
        return this.backingMap.entrySet();
    }
}

