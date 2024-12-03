/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.Extractors;

public class CachedQueryEntry<K, V>
extends QueryableEntry<K, V> {
    protected Data keyData;
    protected Data valueData;
    protected K keyObject;
    protected V valueObject;

    public CachedQueryEntry() {
    }

    public CachedQueryEntry(InternalSerializationService serializationService, Data key, Object value, Extractors extractors) {
        this.init(serializationService, key, value, extractors);
    }

    public void init(InternalSerializationService serializationService, Data key, Object value, Extractors extractors) {
        if (key == null) {
            throw new IllegalArgumentException("keyData cannot be null");
        }
        this.serializationService = serializationService;
        this.keyData = key;
        this.keyObject = null;
        if (value instanceof Data) {
            this.valueData = (Data)value;
            this.valueObject = null;
        } else {
            this.valueObject = value;
            this.valueData = null;
        }
        this.extractors = extractors;
    }

    @Override
    public K getKey() {
        if (this.keyObject == null) {
            this.keyObject = this.serializationService.toObject(this.keyData);
        }
        return this.keyObject;
    }

    @Override
    public V getValue() {
        if (this.valueObject == null) {
            this.valueObject = this.serializationService.toObject(this.valueData);
        }
        return this.valueObject;
    }

    @Override
    public Data getKeyData() {
        return this.keyData;
    }

    @Override
    public Data getValueData() {
        if (this.valueData == null) {
            this.valueData = this.serializationService.toData(this.valueObject);
        }
        return this.valueData;
    }

    @Override
    protected Object getTargetObject(boolean key) {
        Data targetObject = key ? (this.keyData.isPortable() || this.keyData.isJson() ? this.keyData : this.getKey()) : (this.valueObject == null ? (this.valueData.isPortable() || this.valueData.isJson() ? this.valueData : this.getValue()) : (this.valueObject instanceof Portable ? this.getValueData() : this.getValue()));
        return targetObject;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CachedQueryEntry that = (CachedQueryEntry)o;
        return this.keyData.equals(that.keyData);
    }

    @Override
    public int hashCode() {
        return this.keyData.hashCode();
    }
}

