/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.proxy;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AddAndGetOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AlterOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.ApplyOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.CompareAndSetOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.GetAndAddOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.GetAndSetOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.LocalGetOp;
import com.hazelcast.cp.internal.datastructures.spi.operation.DestroyRaftObjectOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ExceptionUtil;

public class RaftAtomicLongProxy
implements IAtomicLong {
    private final RaftInvocationManager invocationManager;
    private final RaftGroupId groupId;
    private final String proxyName;
    private final String objectName;

    public RaftAtomicLongProxy(NodeEngine nodeEngine, RaftGroupId groupId, String proxyName, String objectName) {
        RaftService service = (RaftService)nodeEngine.getService("hz:core:raft");
        this.invocationManager = service.getInvocationManager();
        this.groupId = groupId;
        this.proxyName = proxyName;
        this.objectName = objectName;
    }

    @Override
    public long addAndGet(long delta) {
        return (Long)this.addAndGetAsync(delta).join();
    }

    @Override
    public long incrementAndGet() {
        return this.addAndGet(1L);
    }

    @Override
    public long decrementAndGet() {
        return this.addAndGet(-1L);
    }

    @Override
    public boolean compareAndSet(long expect, long update) {
        return (Boolean)this.compareAndSetAsync(expect, update).join();
    }

    @Override
    public long getAndAdd(long delta) {
        return (Long)this.getAndAddAsync(delta).join();
    }

    @Override
    public long get() {
        return this.getAndAdd(0L);
    }

    @Override
    public long getAndIncrement() {
        return this.getAndAdd(1L);
    }

    @Override
    public long getAndSet(long newValue) {
        return (Long)this.getAndSetAsync(newValue).join();
    }

    @Override
    public void set(long newValue) {
        this.getAndSet(newValue);
    }

    public InternalCompletableFuture<Long> addAndGetAsync(long delta) {
        return this.invocationManager.invoke(this.groupId, new AddAndGetOp(this.objectName, delta));
    }

    public InternalCompletableFuture<Long> incrementAndGetAsync() {
        return this.addAndGetAsync(1L);
    }

    public InternalCompletableFuture<Long> decrementAndGetAsync() {
        return this.addAndGetAsync(-1L);
    }

    public InternalCompletableFuture<Boolean> compareAndSetAsync(long expect, long update) {
        return this.invocationManager.invoke(this.groupId, new CompareAndSetOp(this.objectName, expect, update));
    }

    public InternalCompletableFuture<Long> getAndAddAsync(long delta) {
        return this.invocationManager.invoke(this.groupId, new GetAndAddOp(this.objectName, delta));
    }

    public InternalCompletableFuture<Long> getAsync() {
        return this.getAndAddAsync(0L);
    }

    public InternalCompletableFuture<Long> getAndIncrementAsync() {
        return this.getAndAddAsync(1L);
    }

    public InternalCompletableFuture<Long> getAndSetAsync(long newValue) {
        return this.invocationManager.invoke(this.groupId, new GetAndSetOp(this.objectName, newValue));
    }

    public InternalCompletableFuture<Void> setAsync(long newValue) {
        ICompletableFuture future = this.getAndSetAsync(newValue);
        return future;
    }

    @Override
    public void alter(IFunction<Long, Long> function) {
        this.doAlter(function, AlterOp.AlterResultType.NEW_VALUE);
    }

    @Override
    public long alterAndGet(IFunction<Long, Long> function) {
        return this.doAlter(function, AlterOp.AlterResultType.NEW_VALUE);
    }

    @Override
    public long getAndAlter(IFunction<Long, Long> function) {
        return this.doAlter(function, AlterOp.AlterResultType.OLD_VALUE);
    }

    private long doAlter(IFunction<Long, Long> function, AlterOp.AlterResultType alterResultType) {
        return this.doAlterAsync(function, alterResultType).join();
    }

    private InternalCompletableFuture<Long> doAlterAsync(IFunction<Long, Long> function, AlterOp.AlterResultType alterResultType) {
        return this.invocationManager.invoke(this.groupId, new AlterOp(this.objectName, function, alterResultType));
    }

    @Override
    public <R> R apply(IFunction<Long, R> function) {
        return (R)this.applyAsync((IFunction)function).join();
    }

    public InternalCompletableFuture<Void> alterAsync(IFunction<Long, Long> function) {
        InternalCompletableFuture<Long> future = this.doAlterAsync(function, AlterOp.AlterResultType.NEW_VALUE);
        return future;
    }

    public InternalCompletableFuture<Long> alterAndGetAsync(IFunction<Long, Long> function) {
        return this.doAlterAsync(function, AlterOp.AlterResultType.NEW_VALUE);
    }

    public InternalCompletableFuture<Long> getAndAlterAsync(IFunction<Long, Long> function) {
        return this.doAlterAsync(function, AlterOp.AlterResultType.OLD_VALUE);
    }

    public <R> InternalCompletableFuture<R> applyAsync(IFunction<Long, R> function) {
        return this.invocationManager.invoke(this.groupId, new ApplyOp<R>(this.objectName, function));
    }

    public long localGet(QueryPolicy queryPolicy) {
        ICompletableFuture<Long> f = this.localGetAsync(queryPolicy);
        try {
            return (Long)f.get();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    public ICompletableFuture<Long> localGetAsync(final QueryPolicy queryPolicy) {
        final SimpleCompletableFuture<Long> resultFuture = new SimpleCompletableFuture<Long>(null, null);
        InternalCompletableFuture localFuture = this.invocationManager.queryLocally(this.groupId, new LocalGetOp(this.objectName), queryPolicy);
        localFuture.andThen(new ExecutionCallback<Long>(){

            @Override
            public void onResponse(Long response) {
                resultFuture.setResult(response);
            }

            @Override
            public void onFailure(Throwable t) {
                InternalCompletableFuture future = RaftAtomicLongProxy.this.invocationManager.query(RaftAtomicLongProxy.this.groupId, new LocalGetOp(RaftAtomicLongProxy.this.objectName), queryPolicy);
                future.andThen(new ExecutionCallback<Long>(){

                    @Override
                    public void onResponse(Long response) {
                        resultFuture.setResult(response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        resultFuture.setResult(t);
                    }
                });
            }
        });
        return resultFuture;
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
        return "hz:raft:atomicLongService";
    }

    @Override
    public void destroy() {
        this.invocationManager.invoke(this.groupId, new DestroyRaftObjectOp(this.getServiceName(), this.objectName)).join();
    }

    public CPGroupId getGroupId() {
        return this.groupId;
    }
}

