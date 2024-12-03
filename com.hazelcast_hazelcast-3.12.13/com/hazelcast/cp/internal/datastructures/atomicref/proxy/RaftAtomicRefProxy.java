/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.proxy;

import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IFunction;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.ApplyOp;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.CompareAndSetOp;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.ContainsOp;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.GetOp;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.SetOp;
import com.hazelcast.cp.internal.datastructures.spi.operation.DestroyRaftObjectOp;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Preconditions;

public class RaftAtomicRefProxy<T>
implements IAtomicReference<T> {
    private final RaftInvocationManager invocationManager;
    private final SerializationService serializationService;
    private final RaftGroupId groupId;
    private final String proxyName;
    private final String objectName;

    public RaftAtomicRefProxy(NodeEngine nodeEngine, RaftGroupId groupId, String proxyName, String objectName) {
        RaftService service = (RaftService)nodeEngine.getService("hz:core:raft");
        this.invocationManager = service.getInvocationManager();
        this.serializationService = nodeEngine.getSerializationService();
        this.groupId = groupId;
        this.proxyName = proxyName;
        this.objectName = objectName;
    }

    @Override
    public boolean compareAndSet(T expect, T update) {
        return (Boolean)this.compareAndSetAsync((Object)expect, (Object)update).join();
    }

    @Override
    public T get() {
        return (T)this.getAsync().join();
    }

    @Override
    public void set(T newValue) {
        this.setAsync((Object)newValue).join();
    }

    @Override
    public T getAndSet(T newValue) {
        return (T)this.getAndSetAsync((Object)newValue).join();
    }

    @Override
    public T setAndGet(T update) {
        this.set(update);
        return update;
    }

    @Override
    public boolean isNull() {
        return (Boolean)this.isNullAsync().join();
    }

    @Override
    public void clear() {
        this.clearAsync().join();
    }

    @Override
    public boolean contains(T value) {
        return (Boolean)this.containsAsync((Object)value).join();
    }

    @Override
    public void alter(IFunction<T, T> function) {
        this.alterAsync((IFunction)function).join();
    }

    @Override
    public T alterAndGet(IFunction<T, T> function) {
        return (T)this.alterAndGetAsync((IFunction)function).join();
    }

    @Override
    public T getAndAlter(IFunction<T, T> function) {
        return (T)this.getAndAlterAsync((IFunction)function).join();
    }

    @Override
    public <R> R apply(IFunction<T, R> function) {
        return (R)this.applyAsync((IFunction)function).join();
    }

    public InternalCompletableFuture<Boolean> compareAndSetAsync(T expect, T update) {
        return this.invocationManager.invoke(this.groupId, new CompareAndSetOp(this.objectName, this.toData(expect), this.toData(update)));
    }

    @Override
    public InternalCompletableFuture<T> getAsync() {
        return this.invocationManager.invoke(this.groupId, new GetOp(this.objectName));
    }

    public InternalCompletableFuture<Void> setAsync(T newValue) {
        return this.invocationManager.invoke(this.groupId, new SetOp(this.objectName, this.toData(newValue), false));
    }

    @Override
    public InternalCompletableFuture<T> getAndSetAsync(T newValue) {
        return this.invocationManager.invoke(this.groupId, new SetOp(this.objectName, this.toData(newValue), true));
    }

    public InternalCompletableFuture<Boolean> isNullAsync() {
        return this.containsAsync((Object)null);
    }

    public InternalCompletableFuture<Void> clearAsync() {
        return this.setAsync((Object)null);
    }

    public InternalCompletableFuture<Boolean> containsAsync(T expected) {
        return this.invocationManager.invoke(this.groupId, new ContainsOp(this.objectName, this.toData(expected)));
    }

    public InternalCompletableFuture<Void> alterAsync(IFunction<T, T> function) {
        Preconditions.checkTrue(function != null, "Function cannot be null");
        return this.invocationManager.invoke(this.groupId, new ApplyOp(this.objectName, this.toData(function), ApplyOp.ReturnValueType.NO_RETURN_VALUE, true));
    }

    @Override
    public InternalCompletableFuture<T> alterAndGetAsync(IFunction<T, T> function) {
        Preconditions.checkTrue(function != null, "Function cannot be null");
        return this.invocationManager.invoke(this.groupId, new ApplyOp(this.objectName, this.toData(function), ApplyOp.ReturnValueType.RETURN_NEW_VALUE, true));
    }

    @Override
    public InternalCompletableFuture<T> getAndAlterAsync(IFunction<T, T> function) {
        Preconditions.checkTrue(function != null, "Function cannot be null");
        return this.invocationManager.invoke(this.groupId, new ApplyOp(this.objectName, this.toData(function), ApplyOp.ReturnValueType.RETURN_OLD_VALUE, true));
    }

    @Override
    public <R> InternalCompletableFuture<R> applyAsync(IFunction<T, R> function) {
        Preconditions.checkTrue(function != null, "Function cannot be null");
        return this.invocationManager.invoke(this.groupId, new ApplyOp(this.objectName, this.toData(function), ApplyOp.ReturnValueType.RETURN_NEW_VALUE, false));
    }

    @Override
    public String getPartitionKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return this.proxyName;
    }

    @Override
    public String getServiceName() {
        return "hz:raft:atomicRefService";
    }

    @Override
    public void destroy() {
        this.invocationManager.invoke(this.groupId, new DestroyRaftObjectOp(this.getServiceName(), this.objectName)).join();
    }

    public CPGroupId getGroupId() {
        return this.groupId;
    }

    private Data toData(Object value) {
        return this.serializationService.toData(value);
    }
}

