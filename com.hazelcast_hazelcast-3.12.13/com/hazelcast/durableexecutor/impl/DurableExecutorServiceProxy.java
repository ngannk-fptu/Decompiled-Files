/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.Member;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.durableexecutor.DurableExecutorServiceFuture;
import com.hazelcast.durableexecutor.impl.DistributedDurableExecutorService;
import com.hazelcast.durableexecutor.impl.operations.DisposeResultOperation;
import com.hazelcast.durableexecutor.impl.operations.RetrieveAndDisposeResultOperation;
import com.hazelcast.durableexecutor.impl.operations.RetrieveResultOperation;
import com.hazelcast.durableexecutor.impl.operations.ShutdownOperation;
import com.hazelcast.durableexecutor.impl.operations.TaskOperation;
import com.hazelcast.executor.impl.RunnableAdapter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.executor.CompletedFuture;
import com.hazelcast.util.executor.DelegatingFuture;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class DurableExecutorServiceProxy
extends AbstractDistributedObject<DistributedDurableExecutorService>
implements DurableExecutorService {
    private final FutureUtil.ExceptionHandler shutdownExceptionHandler = new FutureUtil.ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            if (throwable != null) {
                if (throwable instanceof QuorumException) {
                    ExceptionUtil.sneakyThrow(throwable);
                }
                if (throwable.getCause() instanceof QuorumException) {
                    ExceptionUtil.sneakyThrow(throwable.getCause());
                }
            }
            if (DurableExecutorServiceProxy.this.logger.isLoggable(Level.FINEST)) {
                DurableExecutorServiceProxy.this.logger.log(Level.FINEST, "Exception while ExecutorService shutdown", throwable);
            }
        }
    };
    private final ILogger logger;
    private final Random random = new Random();
    private final int partitionCount;
    private final String name;

    DurableExecutorServiceProxy(NodeEngine nodeEngine, DistributedDurableExecutorService service, String name) {
        super(nodeEngine, service);
        this.name = name;
        this.logger = nodeEngine.getLogger(DurableExecutorServiceProxy.class);
        this.partitionCount = nodeEngine.getPartitionService().getPartitionCount();
    }

    @Override
    public <T> Future<T> retrieveResult(long uniqueId) {
        int partitionId = Bits.extractInt(uniqueId, false);
        int sequence = Bits.extractInt(uniqueId, true);
        Operation op = new RetrieveResultOperation(this.name, sequence).setPartitionId(partitionId);
        return this.invokeOnPartition(op);
    }

    @Override
    public void disposeResult(long uniqueId) {
        int partitionId = Bits.extractInt(uniqueId, false);
        int sequence = Bits.extractInt(uniqueId, true);
        Operation op = new DisposeResultOperation(this.name, sequence).setPartitionId(partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(op);
        future.join();
    }

    @Override
    public <T> Future<T> retrieveAndDisposeResult(long uniqueId) {
        int partitionId = Bits.extractInt(uniqueId, false);
        int sequence = Bits.extractInt(uniqueId, true);
        Operation op = new RetrieveAndDisposeResultOperation(this.name, sequence).setPartitionId(partitionId);
        return this.invokeOnPartition(op);
    }

    @Override
    public void execute(Runnable task) {
        RunnableAdapter runnableAdapter = this.createRunnableAdapter(task);
        int partitionId = this.getTaskPartitionId(runnableAdapter);
        this.submitToPartition(runnableAdapter, partitionId, null);
    }

    @Override
    public void executeOnKeyOwner(Runnable task, Object key) {
        RunnableAdapter runnableAdapter = this.createRunnableAdapter(task);
        int partitionId = this.getPartitionId(key);
        this.submitToPartition(runnableAdapter, partitionId, null);
    }

    @Override
    public <T> DurableExecutorServiceFuture<T> submit(Runnable task, T result) {
        RunnableAdapter<T> runnableAdapter = this.createRunnableAdapter(task);
        int partitionId = this.getTaskPartitionId(runnableAdapter);
        return this.submitToPartition(runnableAdapter, partitionId, result);
    }

    @Override
    public DurableExecutorServiceFuture<?> submit(Runnable task) {
        RunnableAdapter runnableAdapter = this.createRunnableAdapter(task);
        int partitionId = this.getTaskPartitionId(runnableAdapter);
        return this.submitToPartition(runnableAdapter, partitionId, null);
    }

    @Override
    public <T> DurableExecutorServiceFuture<T> submit(Callable<T> task) {
        int partitionId = this.getTaskPartitionId(task);
        return this.submitToPartition(task, partitionId, null);
    }

    @Override
    public <T> DurableExecutorServiceFuture<T> submitToKeyOwner(Callable<T> task, Object key) {
        int partitionId = this.getPartitionId(key);
        return this.submitToPartition(task, partitionId, null);
    }

    @Override
    public DurableExecutorServiceFuture<?> submitToKeyOwner(Runnable task, Object key) {
        RunnableAdapter runnableAdapter = this.createRunnableAdapter(task);
        int partitionId = this.getPartitionId(key);
        return this.submitToPartition(runnableAdapter, partitionId, null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void shutdown() {
        NodeEngine nodeEngine = this.getNodeEngine();
        Set<Member> members = nodeEngine.getClusterService().getMembers();
        OperationService operationService = nodeEngine.getOperationService();
        LinkedList calls = new LinkedList();
        for (Member member : members) {
            ShutdownOperation op = new ShutdownOperation(this.name);
            InternalCompletableFuture f = operationService.invokeOnTarget("hz:impl:durableExecutorService", op, member.getAddress());
            calls.add(f);
        }
        FutureUtil.waitWithDeadline(calls, 3L, TimeUnit.SECONDS, this.shutdownExceptionHandler);
    }

    @Override
    public List<Runnable> shutdownNow() {
        this.shutdown();
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        try {
            return ((DistributedDurableExecutorService)this.getService()).isShutdown(this.name);
        }
        catch (HazelcastInstanceNotActiveException e) {
            return true;
        }
    }

    @Override
    public boolean isTerminated() {
        return this.isShutdown();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:durableExecutorService";
    }

    @Override
    protected void throwNotActiveException() {
        throw new RejectedExecutionException();
    }

    private <T> DurableExecutorServiceFuture<T> submitToPartition(Callable<T> task, int partitionId, T defaultValue) {
        int sequence;
        Preconditions.checkNotNull(task, "task can't be null");
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        Object taskData = serializationService.toData(task);
        TaskOperation operation = new TaskOperation(this.name, (Data)taskData);
        operation.setPartitionId(partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(operation);
        try {
            sequence = (Integer)future.get();
        }
        catch (Throwable t) {
            CompletedFuture completedFuture = new CompletedFuture(serializationService, t, this.getAsyncExecutor());
            return new DurableExecutorServiceDelegateFuture<Object>(completedFuture, serializationService, null, -1L);
        }
        Operation op = new RetrieveResultOperation(this.name, sequence).setPartitionId(partitionId);
        InternalCompletableFuture internalCompletableFuture = this.invokeOnPartition(op);
        long taskId = Bits.combineToLong(partitionId, sequence);
        return new DurableExecutorServiceDelegateFuture<T>(internalCompletableFuture, serializationService, defaultValue, taskId);
    }

    private ExecutorService getAsyncExecutor() {
        return this.getNodeEngine().getExecutionService().getExecutor("hz:async");
    }

    private <T> RunnableAdapter<T> createRunnableAdapter(Runnable command) {
        Preconditions.checkNotNull(command, "Command can't be null");
        return new RunnableAdapter(command);
    }

    private <T> int getTaskPartitionId(Callable<T> task) {
        Object partitionKey;
        if (task instanceof PartitionAware && (partitionKey = ((PartitionAware)((Object)task)).getPartitionKey()) != null) {
            return this.getPartitionId(partitionKey);
        }
        return this.random.nextInt(this.partitionCount);
    }

    private int getPartitionId(Object key) {
        return this.getNodeEngine().getPartitionService().getPartitionId(key);
    }

    private static class DurableExecutorServiceDelegateFuture<T>
    extends DelegatingFuture<T>
    implements DurableExecutorServiceFuture<T> {
        final long taskId;

        public DurableExecutorServiceDelegateFuture(InternalCompletableFuture future, SerializationService serializationService, T defaultValue, long taskId) {
            super(future, serializationService, defaultValue);
            this.taskId = taskId;
        }

        @Override
        public long getTaskId() {
            return this.taskId;
        }
    }
}

