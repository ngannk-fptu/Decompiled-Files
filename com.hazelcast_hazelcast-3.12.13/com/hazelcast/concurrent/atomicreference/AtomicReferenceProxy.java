/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceService;
import com.hazelcast.concurrent.atomicreference.operations.AlterAndGetOperation;
import com.hazelcast.concurrent.atomicreference.operations.AlterOperation;
import com.hazelcast.concurrent.atomicreference.operations.ApplyOperation;
import com.hazelcast.concurrent.atomicreference.operations.CompareAndSetOperation;
import com.hazelcast.concurrent.atomicreference.operations.ContainsOperation;
import com.hazelcast.concurrent.atomicreference.operations.GetAndAlterOperation;
import com.hazelcast.concurrent.atomicreference.operations.GetAndSetOperation;
import com.hazelcast.concurrent.atomicreference.operations.GetOperation;
import com.hazelcast.concurrent.atomicreference.operations.IsNullOperation;
import com.hazelcast.concurrent.atomicreference.operations.SetAndGetOperation;
import com.hazelcast.concurrent.atomicreference.operations.SetOperation;
import com.hazelcast.core.AsyncAtomicReference;
import com.hazelcast.core.IFunction;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.Preconditions;

public class AtomicReferenceProxy<E>
extends AbstractDistributedObject<AtomicReferenceService>
implements AsyncAtomicReference<E> {
    private final String name;
    private final int partitionId;

    public AtomicReferenceProxy(String name, NodeEngine nodeEngine, AtomicReferenceService service) {
        super(nodeEngine, service);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
    }

    @Override
    public void alter(IFunction<E, E> function) {
        this.alterAsync((IFunction)function).join();
    }

    public InternalCompletableFuture<Void> alterAsync(IFunction<E, E> function) {
        Preconditions.isNotNull(function, "function");
        Operation operation = new AlterOperation(this.name, this.toData(function)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Void> asyncAlter(IFunction<E, E> function) {
        return this.alterAsync((IFunction)function);
    }

    @Override
    public E alterAndGet(IFunction<E, E> function) {
        return this.alterAndGetAsync((IFunction)function).join();
    }

    @Override
    public InternalCompletableFuture<E> alterAndGetAsync(IFunction<E, E> function) {
        Preconditions.isNotNull(function, "function");
        Operation operation = new AlterAndGetOperation(this.name, this.toData(function)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    @Override
    public InternalCompletableFuture<E> asyncAlterAndGet(IFunction<E, E> function) {
        return this.alterAndGetAsync((IFunction)function);
    }

    @Override
    public E getAndAlter(IFunction<E, E> function) {
        return this.getAndAlterAsync((IFunction)function).join();
    }

    @Override
    public InternalCompletableFuture<E> getAndAlterAsync(IFunction<E, E> function) {
        Preconditions.isNotNull(function, "function");
        Operation operation = new GetAndAlterOperation(this.name, this.toData(function)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    @Override
    public InternalCompletableFuture<E> asyncGetAndAlter(IFunction<E, E> function) {
        return this.getAndAlterAsync((IFunction)function);
    }

    @Override
    public <R> R apply(IFunction<E, R> function) {
        return (R)this.applyAsync((IFunction)function).join();
    }

    @Override
    public <R> InternalCompletableFuture<R> applyAsync(IFunction<E, R> function) {
        Preconditions.isNotNull(function, "function");
        Operation operation = new ApplyOperation(this.name, this.toData(function)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    @Override
    public <R> InternalCompletableFuture<R> asyncApply(IFunction<E, R> function) {
        return this.applyAsync((IFunction)function);
    }

    @Override
    public void clear() {
        this.clearAsync().join();
    }

    public InternalCompletableFuture<Void> clearAsync() {
        return this.setAsync((Object)null);
    }

    public InternalCompletableFuture<Void> asyncClear() {
        return this.clearAsync();
    }

    @Override
    public boolean compareAndSet(E expect, E update) {
        return (Boolean)this.compareAndSetAsync((Object)expect, (Object)update).join();
    }

    public InternalCompletableFuture<Boolean> compareAndSetAsync(E expect, E update) {
        Operation operation = new CompareAndSetOperation(this.name, this.toData(expect), this.toData(update)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Boolean> asyncCompareAndSet(E expect, E update) {
        return this.compareAndSetAsync((Object)expect, (Object)update);
    }

    @Override
    public E get() {
        return this.getAsync().join();
    }

    @Override
    public InternalCompletableFuture<E> getAsync() {
        Operation operation = new GetOperation(this.name).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    @Override
    public InternalCompletableFuture<E> asyncGet() {
        return this.getAsync();
    }

    @Override
    public boolean contains(E expected) {
        return (Boolean)this.containsAsync((Object)expected).join();
    }

    public InternalCompletableFuture<Boolean> containsAsync(E expected) {
        Operation operation = new ContainsOperation(this.name, this.toData(expected)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Boolean> asyncContains(E value) {
        return this.containsAsync((Object)value);
    }

    @Override
    public void set(E newValue) {
        this.setAsync((Object)newValue).join();
    }

    public InternalCompletableFuture<Void> setAsync(E newValue) {
        Operation operation = new SetOperation(this.name, this.toData(newValue)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Void> asyncSet(E newValue) {
        Operation operation = new SetOperation(this.name, this.toData(newValue)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    @Override
    public E getAndSet(E newValue) {
        return this.getAndSetAsync((Object)newValue).join();
    }

    @Override
    public InternalCompletableFuture<E> getAndSetAsync(E newValue) {
        Operation operation = new GetAndSetOperation(this.name, this.toData(newValue)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    @Override
    public InternalCompletableFuture<E> asyncGetAndSet(E newValue) {
        return this.getAndSetAsync((Object)newValue);
    }

    @Override
    public E setAndGet(E update) {
        return this.asyncSetAndGet((Object)update).join();
    }

    @Override
    public InternalCompletableFuture<E> asyncSetAndGet(E update) {
        Operation operation = new SetAndGetOperation(this.name, this.toData(update)).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    @Override
    public boolean isNull() {
        return (Boolean)this.isNullAsync().join();
    }

    public InternalCompletableFuture<Boolean> isNullAsync() {
        Operation operation = new IsNullOperation(this.name).setPartitionId(this.partitionId);
        return this.invokeOnPartition(operation);
    }

    public InternalCompletableFuture<Boolean> asyncIsNull() {
        return this.isNullAsync();
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
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public String toString() {
        return "IAtomicReference{name='" + this.name + '\'' + '}';
    }
}

