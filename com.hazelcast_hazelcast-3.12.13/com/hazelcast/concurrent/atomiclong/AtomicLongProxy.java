/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong;

import com.hazelcast.concurrent.atomiclong.AtomicLongService;
import com.hazelcast.concurrent.atomiclong.operations.AddAndGetOperation;
import com.hazelcast.concurrent.atomiclong.operations.AlterAndGetOperation;
import com.hazelcast.concurrent.atomiclong.operations.AlterOperation;
import com.hazelcast.concurrent.atomiclong.operations.ApplyOperation;
import com.hazelcast.concurrent.atomiclong.operations.CompareAndSetOperation;
import com.hazelcast.concurrent.atomiclong.operations.GetAndAddOperation;
import com.hazelcast.concurrent.atomiclong.operations.GetAndAlterOperation;
import com.hazelcast.concurrent.atomiclong.operations.GetAndSetOperation;
import com.hazelcast.concurrent.atomiclong.operations.GetOperation;
import com.hazelcast.concurrent.atomiclong.operations.SetOperation;
import com.hazelcast.core.AsyncAtomicLong;
import com.hazelcast.core.IFunction;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.Preconditions;

public class AtomicLongProxy
extends AbstractDistributedObject<AtomicLongService>
implements AsyncAtomicLong {
    private final String name;
    private final int partitionId;

    public AtomicLongProxy(String name, NodeEngine nodeEngine, AtomicLongService service) {
        super(nodeEngine, service);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public long addAndGet(long delta) {
        return (Long)this.addAndGetAsync(delta).join();
    }

    public InternalCompletableFuture<Long> addAndGetAsync(long delta) {
        Operation operation = new AddAndGetOperation(this.name, delta).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Long> asyncAddAndGet(long delta) {
        return this.addAndGetAsync(delta);
    }

    @Override
    public boolean compareAndSet(long expect, long update) {
        return (Boolean)this.compareAndSetAsync(expect, update).join();
    }

    public InternalCompletableFuture<Boolean> compareAndSetAsync(long expect, long update) {
        Operation operation = new CompareAndSetOperation(this.name, expect, update).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Boolean> asyncCompareAndSet(long expect, long update) {
        return this.compareAndSetAsync(expect, update);
    }

    @Override
    public void set(long newValue) {
        this.setAsync(newValue).join();
    }

    public InternalCompletableFuture<Void> setAsync(long newValue) {
        Operation operation = new SetOperation(this.name, newValue).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Void> asyncSet(long newValue) {
        return this.setAsync(newValue);
    }

    @Override
    public long getAndSet(long newValue) {
        return (Long)this.getAndSetAsync(newValue).join();
    }

    public InternalCompletableFuture<Long> getAndSetAsync(long newValue) {
        Operation operation = new GetAndSetOperation(this.name, newValue).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Long> asyncGetAndSet(long newValue) {
        return this.getAndSetAsync(newValue);
    }

    @Override
    public long getAndAdd(long delta) {
        return (Long)this.getAndAddAsync(delta).join();
    }

    public InternalCompletableFuture<Long> getAndAddAsync(long delta) {
        Operation operation = new GetAndAddOperation(this.name, delta).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Long> asyncGetAndAdd(long delta) {
        return this.getAndAddAsync(delta);
    }

    @Override
    public long decrementAndGet() {
        return (Long)this.decrementAndGetAsync().join();
    }

    public InternalCompletableFuture<Long> decrementAndGetAsync() {
        return this.addAndGetAsync(-1L);
    }

    public InternalCompletableFuture<Long> asyncDecrementAndGet() {
        return this.addAndGetAsync(-1L);
    }

    @Override
    public long get() {
        return (Long)this.getAsync().join();
    }

    public InternalCompletableFuture<Long> getAsync() {
        Operation operation = new GetOperation(this.name).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Long> asyncGet() {
        return this.getAsync();
    }

    @Override
    public long incrementAndGet() {
        return (Long)this.incrementAndGetAsync().join();
    }

    public InternalCompletableFuture<Long> incrementAndGetAsync() {
        return this.addAndGetAsync(1L);
    }

    public InternalCompletableFuture<Long> asyncIncrementAndGet() {
        return this.addAndGetAsync(1L);
    }

    @Override
    public long getAndIncrement() {
        return (Long)this.getAndIncrementAsync().join();
    }

    public InternalCompletableFuture<Long> getAndIncrementAsync() {
        return this.getAndAddAsync(1L);
    }

    public InternalCompletableFuture<Long> asyncGetAndIncrement() {
        return this.getAndAddAsync(1L);
    }

    @Override
    public void alter(IFunction<Long, Long> function) {
        this.alterAsync((IFunction)function).join();
    }

    public InternalCompletableFuture<Void> alterAsync(IFunction<Long, Long> function) {
        Preconditions.isNotNull(function, "function");
        Operation operation = new AlterOperation(this.name, function).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Void> asyncAlter(IFunction<Long, Long> function) {
        return this.alterAsync((IFunction)function);
    }

    @Override
    public long alterAndGet(IFunction<Long, Long> function) {
        return (Long)this.alterAndGetAsync((IFunction)function).join();
    }

    public InternalCompletableFuture<Long> alterAndGetAsync(IFunction<Long, Long> function) {
        Preconditions.isNotNull(function, "function");
        Operation operation = new AlterAndGetOperation(this.name, function).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Long> asyncAlterAndGet(IFunction<Long, Long> function) {
        return this.alterAndGetAsync((IFunction)function);
    }

    @Override
    public long getAndAlter(IFunction<Long, Long> function) {
        return (Long)this.getAndAlterAsync((IFunction)function).join();
    }

    public InternalCompletableFuture<Long> getAndAlterAsync(IFunction<Long, Long> function) {
        Preconditions.isNotNull(function, "function");
        Operation operation = new GetAndAlterOperation(this.name, function).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Long> asyncGetAndAlter(IFunction<Long, Long> function) {
        return this.getAndAlterAsync((IFunction)function);
    }

    @Override
    public <R> R apply(IFunction<Long, R> function) {
        return (R)this.applyAsync((IFunction)function).join();
    }

    public <R> InternalCompletableFuture<R> applyAsync(IFunction<Long, R> function) {
        Preconditions.isNotNull(function, "function");
        Operation operation = new ApplyOperation<R>(this.name, function).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public <R> InternalCompletableFuture<R> asyncApply(IFunction<Long, R> function) {
        return this.applyAsync((IFunction)function);
    }

    @Override
    public String toString() {
        return "IAtomicLong{name='" + this.name + '\'' + '}';
    }
}

