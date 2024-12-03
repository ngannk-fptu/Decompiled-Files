/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore;

import com.hazelcast.concurrent.semaphore.SemaphoreService;
import com.hazelcast.concurrent.semaphore.operations.AcquireOperation;
import com.hazelcast.concurrent.semaphore.operations.AvailableOperation;
import com.hazelcast.concurrent.semaphore.operations.DrainOperation;
import com.hazelcast.concurrent.semaphore.operations.IncreaseOperation;
import com.hazelcast.concurrent.semaphore.operations.InitOperation;
import com.hazelcast.concurrent.semaphore.operations.ReduceOperation;
import com.hazelcast.concurrent.semaphore.operations.ReleaseOperation;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.TimeUnit;

public class SemaphoreProxy
extends AbstractDistributedObject<SemaphoreService>
implements ISemaphore {
    private final String name;
    private final int partitionId;

    public SemaphoreProxy(String name, SemaphoreService service, NodeEngine nodeEngine) {
        super(nodeEngine, service);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean init(int permits) {
        Preconditions.checkNotNegative(permits, "permits can't be negative");
        Operation operation = new InitOperation(this.name, permits).setPartitionId(this.partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(operation);
        return (Boolean)future.join();
    }

    @Override
    public void acquire() throws InterruptedException {
        this.acquire(1);
    }

    @Override
    public void acquire(int permits) throws InterruptedException {
        Preconditions.checkNotNegative(permits, "permits can't be negative");
        try {
            Operation operation = new AcquireOperation(this.name, permits, -1L).setPartitionId(this.partitionId);
            InternalCompletableFuture future = this.invokeOnPartition(operation);
            future.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrowAllowInterrupted(t);
        }
    }

    @Override
    public int availablePermits() {
        Operation operation = new AvailableOperation(this.name).setPartitionId(this.partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(operation);
        return (Integer)future.join();
    }

    @Override
    public int drainPermits() {
        Operation operation = new DrainOperation(this.name).setPartitionId(this.partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(operation);
        return (Integer)future.join();
    }

    @Override
    public void reducePermits(int reduction) {
        Preconditions.checkNotNegative(reduction, "reduction can't be negative");
        Operation operation = new ReduceOperation(this.name, reduction).setPartitionId(this.partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(operation);
        future.join();
    }

    @Override
    public void increasePermits(int increase) {
        if (this.getNodeEngine().getClusterService().getClusterVersion().isLessThan(Versions.V3_10)) {
            throw new UnsupportedOperationException("Increasing permits is available when cluster version is 3.10 or higher");
        }
        Preconditions.checkNotNegative(increase, "increase can't be negative");
        Operation operation = new IncreaseOperation(this.name, increase).setPartitionId(this.partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(operation);
        future.join();
    }

    @Override
    public void release() {
        this.release(1);
    }

    @Override
    public void release(int permits) {
        Preconditions.checkNotNegative(permits, "permits can't be negative");
        Operation operation = new ReleaseOperation(this.name, permits).setPartitionId(this.partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(operation);
        future.join();
    }

    @Override
    public boolean tryAcquire() {
        try {
            return this.tryAcquire(1, 0L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean tryAcquire(int permits) {
        try {
            return this.tryAcquire(permits, 0L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        return this.tryAcquire(1, timeout, unit);
    }

    @Override
    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNegative(permits, "permits can't be negative");
        try {
            Operation operation = new AcquireOperation(this.name, permits, unit.toMillis(timeout)).setPartitionId(this.partitionId);
            InternalCompletableFuture future = this.invokeOnPartition(operation);
            return (Boolean)future.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrowAllowInterrupted(t);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public String toString() {
        return "ISemaphore{name='" + this.name + '\'' + '}';
    }
}

