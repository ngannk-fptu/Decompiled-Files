/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.map.impl.query.AccumulationExecutor;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.ResultProcessor;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collection;

public class AggregationResultProcessor
implements ResultProcessor<AggregationResult> {
    private final AccumulationExecutor accumulationExecutor;
    private final SerializationService serializationService;

    public AggregationResultProcessor(AccumulationExecutor accumulationExecutor, SerializationService serializationService) {
        this.accumulationExecutor = accumulationExecutor;
        this.serializationService = serializationService;
    }

    @Override
    public AggregationResult populateResult(Query query, long resultLimit, Collection<QueryableEntry> entries, Collection<Integer> partitionIds) {
        return this.accumulationExecutor.execute(query.getAggregator(), entries, partitionIds);
    }

    @Override
    public AggregationResult populateResult(Query query, long resultLimit) {
        Aggregator resultAggregator = (Aggregator)this.serializationService.toObject(this.serializationService.toData(query.getAggregator()));
        return new AggregationResult(resultAggregator, this.serializationService);
    }
}

