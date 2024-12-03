/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality.impl;

import com.hazelcast.cardinality.CardinalityEstimator;
import com.hazelcast.cardinality.impl.CardinalityEstimatorService;
import com.hazelcast.cardinality.impl.operations.AggregateOperation;
import com.hazelcast.cardinality.impl.operations.EstimateOperation;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.Preconditions;

public class CardinalityEstimatorProxy
extends AbstractDistributedObject<CardinalityEstimatorService>
implements CardinalityEstimator {
    private final String name;
    private final int partitionId;

    CardinalityEstimatorProxy(String name, NodeEngine nodeEngine, CardinalityEstimatorService service) {
        super(nodeEngine, service);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cardinalityEstimatorService";
    }

    @Override
    public void add(Object obj) {
        this.addAsync(obj).join();
    }

    @Override
    public long estimate() {
        return (Long)this.estimateAsync().join();
    }

    public InternalCompletableFuture<Void> addAsync(Object obj) {
        Preconditions.checkNotNull(obj, "Object is null.");
        Object data = this.getNodeEngine().getSerializationService().toData(obj);
        Operation operation = new AggregateOperation(this.name, data.hash64()).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Long> estimateAsync() {
        Operation operation = new EstimateOperation(this.name).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    @Override
    public String toString() {
        return "CardinalityEstimator{name='" + this.name + '\'' + '}';
    }
}

