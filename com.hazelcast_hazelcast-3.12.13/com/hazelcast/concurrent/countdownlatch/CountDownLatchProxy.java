/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.countdownlatch.operations.AwaitOperation;
import com.hazelcast.concurrent.countdownlatch.operations.CountDownOperation;
import com.hazelcast.concurrent.countdownlatch.operations.GetCountOperation;
import com.hazelcast.concurrent.countdownlatch.operations.SetCountOperation;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CountDownLatchProxy
extends AbstractDistributedObject<CountDownLatchService>
implements ICountDownLatch {
    private final String name;
    private final int partitionId;

    public CountDownLatchProxy(String name, NodeEngine nodeEngine) {
        super(nodeEngine, null);
        this.name = name;
        Data nameAsPartitionAwareData = this.getNameAsPartitionAwareData();
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(nameAsPartitionAwareData);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(unit, "unit can't be null");
        Operation op = new AwaitOperation(this.name, unit.toMillis(timeout)).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        try {
            return (Boolean)f.get();
        }
        catch (ExecutionException e) {
            throw ExceptionUtil.rethrowAllowInterrupted(e);
        }
    }

    @Override
    public void countDown() {
        Operation op = new CountDownOperation(this.name).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        f.join();
    }

    @Override
    public int getCount() {
        Operation op = new GetCountOperation(this.name).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        return (Integer)f.join();
    }

    @Override
    public boolean trySetCount(int count) {
        Preconditions.checkNotNegative(count, "count can't be negative");
        Operation op = new SetCountOperation(this.name, count).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(op);
        return (Boolean)f.join();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:countDownLatchService";
    }

    @Override
    public String toString() {
        return "ICountDownLatch{name='" + this.name + '\'' + '}';
    }
}

