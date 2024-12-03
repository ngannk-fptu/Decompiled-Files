/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.nio.serialization.Data;
import java.util.Map;

class MapReduceSimpleEntry<K, V>
implements Map.Entry<K, V> {
    private Data keyData;
    private K key;
    private V value;

    public MapReduceSimpleEntry() {
        this(null, null);
    }

    public MapReduceSimpleEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Data getKeyData() {
        return this.keyData;
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

    public void setKeyData(Data keyData) {
        this.keyData = keyData;
    }

    public K setKey(K key) {
        K oldKey = this.key;
        this.key = key;
        return oldKey;
    }
}

