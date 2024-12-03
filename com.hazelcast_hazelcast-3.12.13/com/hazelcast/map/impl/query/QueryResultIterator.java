/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.IterationType;
import java.util.AbstractMap;
import java.util.Iterator;

final class QueryResultIterator
implements Iterator {
    private final Iterator<QueryResultRow> iterator;
    private final IterationType iteratorType;
    private final boolean binary;
    private final SerializationService serializationService;

    QueryResultIterator(Iterator<QueryResultRow> iterator, IterationType iteratorType, boolean binary, SerializationService serializationService) {
        this.iterator = iterator;
        this.iteratorType = iteratorType;
        this.binary = binary;
        this.serializationService = serializationService;
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        QueryResultRow row = this.iterator.next();
        switch (this.iteratorType) {
            case VALUE: {
                return this.binary ? row.getValue() : this.serializationService.toObject(row.getValue());
            }
            case KEY: {
                return this.binary ? row.getKey() : this.serializationService.toObject(row.getKey());
            }
            case ENTRY: {
                if (this.binary) {
                    return new AbstractMap.SimpleImmutableEntry<Data, Data>(row.getKey(), row.getValue());
                }
                Object key = this.serializationService.toObject(row.getKey());
                Object value = this.serializationService.toObject(row.getValue());
                return new AbstractMap.SimpleImmutableEntry(key, value);
            }
        }
        throw new IllegalStateException("Unrecognized iteratorType:" + (Object)((Object)this.iteratorType));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

