/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.ResultProcessor;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collection;

public class QueryResultProcessor
implements ResultProcessor<QueryResult> {
    private final SerializationService serializationService;

    public QueryResultProcessor(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public QueryResult populateResult(Query query, long resultLimit, Collection<QueryableEntry> entries, Collection<Integer> partitionIds) {
        QueryResult result = new QueryResult(query.getIterationType(), query.getProjection(), this.serializationService, resultLimit, false);
        for (QueryableEntry entry : entries) {
            result.add(entry);
        }
        result.setPartitionIds(partitionIds);
        return result;
    }

    @Override
    public QueryResult populateResult(Query query, long resultLimit) {
        return new QueryResult(query.getIterationType(), query.getProjection(), this.serializationService, resultLimit, false);
    }
}

