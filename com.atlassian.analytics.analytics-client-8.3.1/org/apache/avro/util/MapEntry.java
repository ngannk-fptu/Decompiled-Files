/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.util.Map;

public class MapEntry<K, V>
implements Map.Entry<K, V> {
    K key;
    V value;

    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }
}

