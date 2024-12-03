/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultIterator;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.IterationType;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class QueryResultCollection<E>
extends AbstractSet<E> {
    private final Collection<QueryResultRow> rows;
    private final SerializationService serializationService;
    private final IterationType iterationType;
    private final boolean binary;

    public QueryResultCollection(SerializationService serializationService, IterationType iterationType, boolean binary, boolean distinct, QueryResult queryResult) {
        this.serializationService = serializationService;
        this.iterationType = iterationType;
        this.binary = binary;
        this.rows = distinct ? new HashSet<QueryResultRow>(queryResult.getRows()) : queryResult.getRows();
    }

    Collection<QueryResultRow> getRows() {
        return this.rows;
    }

    public IterationType getIterationType() {
        return this.iterationType;
    }

    @Override
    public Iterator<E> iterator() {
        return new QueryResultIterator(this.rows.iterator(), this.iterationType, this.binary, this.serializationService);
    }

    @Override
    public int size() {
        return this.rows.size();
    }
}

