/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.Extractors;

final class SimpleEntry<K, V>
extends QueryableEntry<K, V> {
    private K key;
    private V value;

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public Data getKeyData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Data getValueData() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object getTargetObject(boolean key) {
        return key ? this.key : this.value;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V newValue) {
        V oldValue = this.value;
        this.value = newValue;
        return oldValue;
    }

    void setKey(K key) {
        this.key = key;
    }

    void setSerializationService(InternalSerializationService serializationService) {
        this.serializationService = serializationService;
    }

    void setExtractors(Extractors extractors) {
        this.extractors = extractors;
    }
}

