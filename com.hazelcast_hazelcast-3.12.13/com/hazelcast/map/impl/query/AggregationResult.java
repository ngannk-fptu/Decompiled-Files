/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AggregationResult
implements Result<AggregationResult> {
    private Aggregator aggregator;
    private Collection<Integer> partitionIds;
    private final transient SerializationService serializationService;

    public AggregationResult() {
        this.serializationService = null;
    }

    public AggregationResult(Aggregator aggregator, SerializationService serializationService) {
        this.aggregator = aggregator;
        this.serializationService = serializationService;
    }

    public <R> Aggregator<?, R> getAggregator() {
        return this.aggregator;
    }

    @Override
    public Collection<Integer> getPartitionIds() {
        return this.partitionIds;
    }

    @Override
    public void combine(AggregationResult result) {
        Collection<Integer> otherPartitionIds = result.getPartitionIds();
        if (otherPartitionIds == null) {
            return;
        }
        if (this.partitionIds == null) {
            this.partitionIds = new ArrayList<Integer>(otherPartitionIds.size());
        }
        this.partitionIds.addAll(otherPartitionIds);
        this.aggregator.combine(result.aggregator);
    }

    @Override
    public void onCombineFinished() {
        if (this.aggregator != null) {
            this.aggregator.onCombinationFinished();
        }
    }

    @Override
    public void add(QueryableEntry entry) {
        this.aggregator.accumulate(entry);
    }

    @Override
    public AggregationResult createSubResult() {
        Aggregator aggregatorClone = (Aggregator)this.serializationService.toObject(this.serializationService.toData(this.aggregator));
        return new AggregationResult(aggregatorClone, this.serializationService);
    }

    @Override
    public void orderAndLimit(PagingPredicate pagingPredicate, Map.Entry<Integer, Map.Entry> nearestAnchorEntry) {
    }

    @Override
    public void completeConstruction(Collection<Integer> partitionIds) {
        this.setPartitionIds(partitionIds);
    }

    @Override
    public void setPartitionIds(Collection<Integer> partitionIds) {
        this.partitionIds = new ArrayList<Integer>(partitionIds);
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 115;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        int partitionSize = this.partitionIds == null ? 0 : this.partitionIds.size();
        out.writeInt(partitionSize);
        if (partitionSize > 0) {
            for (Integer partitionId : this.partitionIds) {
                out.writeInt(partitionId);
            }
        }
        out.writeObject(this.aggregator);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int partitionSize = in.readInt();
        if (partitionSize > 0) {
            this.partitionIds = new ArrayList<Integer>(partitionSize);
            for (int i = 0; i < partitionSize; ++i) {
                this.partitionIds.add(in.readInt());
            }
        }
        this.aggregator = (Aggregator)in.readObject();
    }
}

