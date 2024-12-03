/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.Extractors;

public class QueryEntry
extends QueryableEntry {
    private Data key;
    private Object value;

    public QueryEntry() {
    }

    public QueryEntry(InternalSerializationService serializationService, Data key, Object value, Extractors extractors) {
        this.init(serializationService, key, value, extractors);
    }

    public void init(InternalSerializationService serializationService, Data key, Object value, Extractors extractors) {
        if (key == null) {
            throw new IllegalArgumentException("keyData cannot be null");
        }
        this.serializationService = serializationService;
        this.key = key;
        this.value = value;
        this.extractors = extractors;
    }

    @Override
    public Object getKey() {
        return this.serializationService.toObject(this.key);
    }

    @Override
    public Object getValue() {
        return this.serializationService.toObject(this.value);
    }

    @Override
    public Data getKeyData() {
        return this.key;
    }

    @Override
    public Data getValueData() {
        return this.serializationService.toData(this.value);
    }

    @Override
    protected Object getTargetObject(boolean key) {
        return key ? this.key : this.value;
    }

    @Override
    public Object setValue(Object value) {
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
        QueryEntry that = (QueryEntry)o;
        return this.key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
}

