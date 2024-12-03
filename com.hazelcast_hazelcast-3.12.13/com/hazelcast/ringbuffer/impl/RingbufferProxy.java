/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.ringbuffer.impl.operations.AddAllOperation;
import com.hazelcast.ringbuffer.impl.operations.AddOperation;
import com.hazelcast.ringbuffer.impl.operations.GenericOperation;
import com.hazelcast.ringbuffer.impl.operations.ReadManyOperation;
import com.hazelcast.ringbuffer.impl.operations.ReadOneOperation;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collection;

public class RingbufferProxy<E>
extends AbstractDistributedObject<RingbufferService>
implements Ringbuffer<E> {
    public static final int MAX_BATCH_SIZE = 1000;
    private final String name;
    private final int partitionId;
    private final RingbufferConfig config;

    public RingbufferProxy(NodeEngine nodeEngine, RingbufferService service, String name, RingbufferConfig config) {
        super(nodeEngine, service);
        this.name = name;
        this.partitionId = service.getRingbufferPartitionId(name);
        this.config = config;
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
        return "hz:impl:ringbufferService";
    }

    @Override
    public long capacity() {
        ((RingbufferService)this.getService()).ensureQuorumPresent(this.name, QuorumType.READ);
        return this.config.getCapacity();
    }

    @Override
    public long size() {
        Operation op = new GenericOperation(this.name, 0).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        return (Long)f.join();
    }

    @Override
    public long tailSequence() {
        Operation op = new GenericOperation(this.name, 1).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        return (Long)f.join();
    }

    @Override
    public long headSequence() {
        Operation op = new GenericOperation(this.name, 2).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        return (Long)f.join();
    }

    @Override
    public long remainingCapacity() {
        if (this.config.getTimeToLiveSeconds() == 0) {
            ((RingbufferService)this.getService()).ensureQuorumPresent(this.name, QuorumType.READ);
            return this.config.getCapacity();
        }
        Operation op = new GenericOperation(this.name, 3).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        return (Long)f.join();
    }

    @Override
    public long add(E item) {
        Preconditions.checkNotNull(item, "item can't be null");
        Operation op = new AddOperation(this.name, this.toData(item), OverflowPolicy.OVERWRITE).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        return (Long)f.join();
    }

    @Override
    public ICompletableFuture<Long> addAsync(E item, OverflowPolicy overflowPolicy) {
        Preconditions.checkNotNull(item, "item can't be null");
        Preconditions.checkNotNull(overflowPolicy, "overflowPolicy can't be null");
        Operation op = new AddOperation(this.name, this.toData(item), overflowPolicy).setPartitionId(this.partitionId);
        return this.invokeOnPartition(op);
    }

    @Override
    public E readOne(long sequence) throws InterruptedException {
        RingbufferProxy.checkSequence(sequence);
        Operation op = new ReadOneOperation(this.name, sequence).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        try {
            return (E)f.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrowAllowInterrupted(t);
        }
    }

    @Override
    public ICompletableFuture<Long> addAllAsync(Collection<? extends E> collection, OverflowPolicy overflowPolicy) {
        Preconditions.checkNotNull(collection, "collection can't be null");
        Preconditions.checkNotNull(overflowPolicy, "overflowPolicy can't be null");
        Preconditions.checkFalse(collection.isEmpty(), "collection can't be empty");
        Preconditions.checkTrue(collection.size() <= 1000, "collection can't be larger than 1000");
        Operation op = new AddAllOperation(this.name, this.toDataArray(collection), overflowPolicy).setPartitionId(this.partitionId);
        OperationService operationService = this.getOperationService();
        return operationService.createInvocationBuilder(null, op, this.partitionId).setCallTimeout(Long.MAX_VALUE).invoke();
    }

    private Data[] toDataArray(Collection<? extends E> collection) {
        Data[] items = new Data[collection.size()];
        int k = 0;
        for (E item : collection) {
            Preconditions.checkNotNull(item, "collection can't contains null items");
            items[k] = this.toData(item);
            ++k;
        }
        return items;
    }

    @Override
    public ICompletableFuture<ReadResultSet<E>> readManyAsync(long startSequence, int minCount, int maxCount, IFunction<E, Boolean> filter) {
        RingbufferProxy.checkSequence(startSequence);
        Preconditions.checkNotNegative(minCount, "minCount can't be smaller than 0");
        Preconditions.checkTrue(maxCount >= minCount, "maxCount should be equal or larger than minCount");
        Preconditions.checkTrue(maxCount <= this.config.getCapacity(), "the maxCount should be smaller than or equal to the capacity");
        Preconditions.checkTrue(maxCount <= 1000, "maxCount can't be larger than 1000");
        Operation op = new ReadManyOperation<E>(this.name, startSequence, minCount, maxCount, filter).setPartitionId(this.partitionId);
        OperationService operationService = this.getOperationService();
        return operationService.createInvocationBuilder(null, op, this.partitionId).setCallTimeout(Long.MAX_VALUE).invoke();
    }

    private static void checkSequence(long sequence) {
        if (sequence < 0L) {
            throw new IllegalArgumentException("sequence can't be smaller than 0, but was: " + sequence);
        }
    }

    @Override
    public String toString() {
        return String.format("Ringbuffer{name='%s'}", this.name);
    }
}

