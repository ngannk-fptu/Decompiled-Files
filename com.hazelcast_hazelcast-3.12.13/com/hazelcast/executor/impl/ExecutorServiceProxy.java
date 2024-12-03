/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.core.MultiExecutionCallback;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.executor.impl.CancellableDelegatingFuture;
import com.hazelcast.executor.impl.DistributedExecutorService;
import com.hazelcast.executor.impl.ExecutionCallbackAdapterFactory;
import com.hazelcast.executor.impl.RunnableAdapter;
import com.hazelcast.executor.impl.operations.CallableTaskOperation;
import com.hazelcast.executor.impl.operations.MemberCallableTaskOperation;
import com.hazelcast.executor.impl.operations.ShutdownOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.executor.CompletedFuture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.logging.Level;

public class ExecutorServiceProxy
extends AbstractDistributedObject<DistributedExecutorService>
implements IExecutorService {
    public static final int SYNC_FREQUENCY = 100;
    public static final int SYNC_DELAY_MS = 10;
    private static final AtomicIntegerFieldUpdater<ExecutorServiceProxy> CONSECUTIVE_SUBMITS = AtomicIntegerFieldUpdater.newUpdater(ExecutorServiceProxy.class, "consecutiveSubmits");
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
            if (ExecutorServiceProxy.this.logger.isLoggable(Level.FINEST)) {
                ExecutorServiceProxy.this.logger.log(Level.FINEST, "Exception while ExecutorService shutdown", throwable);
            }
        }
    };
    private final String name;
    private final Random random = new Random(-System.currentTimeMillis());
    private final int partitionCount;
    private final ILogger logger;
    private volatile int consecutiveSubmits;
    private volatile long lastSubmitTime;

    public ExecutorServiceProxy(String name, NodeEngine nodeEngine, DistributedExecutorService service) {
        super(nodeEngine, service);
        this.name = name;
        this.partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.logger = nodeEngine.getLogger(ExecutorServiceProxy.class);
        this.getLocalExecutorStats();
    }

    @Override
    public void execute(Runnable command, MemberSelector memberSelector) {
        List<Member> members = this.selectMembers(memberSelector);
        int selectedMember = this.random.nextInt(members.size());
        this.executeOnMember(command, members.get(selectedMember));
    }

    @Override
    public void executeOnMembers(Runnable command, MemberSelector memberSelector) {
        List<Member> members = this.selectMembers(memberSelector);
        this.executeOnMembers(command, members);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task, MemberSelector memberSelector) {
        List<Member> members = this.selectMembers(memberSelector);
        int selectedMember = this.random.nextInt(members.size());
        return this.submitToMember(task, members.get(selectedMember));
    }

    @Override
    public <T> Map<Member, Future<T>> submitToMembers(Callable<T> task, MemberSelector memberSelector) {
        List<Member> members = this.selectMembers(memberSelector);
        return this.submitToMembers(task, members);
    }

    public void submit(Runnable task, MemberSelector memberSelector, ExecutionCallback callback) {
        List<Member> members = this.selectMembers(memberSelector);
        int selectedMember = this.random.nextInt(members.size());
        this.submitToMember(task, members.get(selectedMember), callback);
    }

    @Override
    public void submitToMembers(Runnable task, MemberSelector memberSelector, MultiExecutionCallback callback) {
        List<Member> members = this.selectMembers(memberSelector);
        this.submitToMembers(task, members, callback);
    }

    @Override
    public <T> void submit(Callable<T> task, MemberSelector memberSelector, ExecutionCallback<T> callback) {
        List<Member> members = this.selectMembers(memberSelector);
        int selectedMember = this.random.nextInt(members.size());
        this.submitToMember(task, members.get(selectedMember), callback);
    }

    @Override
    public <T> void submitToMembers(Callable<T> task, MemberSelector memberSelector, MultiExecutionCallback callback) {
        List<Member> members = this.selectMembers(memberSelector);
        this.submitToMembers(task, members, callback);
    }

    @Override
    public void execute(Runnable command) {
        RunnableAdapter callable = this.createRunnableAdapter(command);
        this.submit(callable);
    }

    private <T> RunnableAdapter<T> createRunnableAdapter(Runnable command) {
        Preconditions.checkNotNull(command, "Command can't be null");
        return new RunnableAdapter(command);
    }

    @Override
    public void executeOnKeyOwner(Runnable command, Object key) {
        RunnableAdapter callable = this.createRunnableAdapter(command);
        this.submitToKeyOwner(callable, key);
    }

    @Override
    public void executeOnMember(Runnable command, Member member) {
        RunnableAdapter callable = this.createRunnableAdapter(command);
        this.submitToMember(callable, member);
    }

    @Override
    public void executeOnMembers(Runnable command, Collection<Member> members) {
        RunnableAdapter callable = this.createRunnableAdapter(command);
        this.submitToMembers(callable, members);
    }

    @Override
    public void executeOnAllMembers(Runnable command) {
        RunnableAdapter callable = this.createRunnableAdapter(command);
        this.submitToAllMembers(callable);
    }

    @Override
    public Future<?> submit(Runnable task) {
        RunnableAdapter callable = this.createRunnableAdapter(task);
        return this.submit(callable);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        Preconditions.checkNotNull(task, "task can't be null");
        this.checkNotShutdown();
        NodeEngine nodeEngine = this.getNodeEngine();
        RunnableAdapter<T> callable = this.createRunnableAdapter(task);
        Data callableData = nodeEngine.toData(callable);
        String uuid = UuidUtil.newUnsecureUuidString();
        int partitionId = this.getTaskPartitionId(callable);
        Operation op = new CallableTaskOperation(this.name, uuid, callableData).setPartitionId(partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(op);
        boolean sync = this.checkSync();
        if (sync) {
            try {
                future.get();
            }
            catch (Exception exception) {
                this.logger.warning(exception);
            }
            return new CompletedFuture(nodeEngine.getSerializationService(), result, this.getAsyncExecutor());
        }
        return new CancellableDelegatingFuture<T>(future, result, nodeEngine, uuid, partitionId);
    }

    private void checkNotShutdown() {
        if (this.isShutdown()) {
            throw new RejectedExecutionException(this.getRejectionMessage());
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        int partitionId = this.getTaskPartitionId(task);
        return this.submitToPartitionOwner(task, partitionId, false);
    }

    private <T> Future<T> submitToPartitionOwner(Callable<T> task, int partitionId, boolean preventSync) {
        Preconditions.checkNotNull(task, "task can't be null");
        this.checkNotShutdown();
        NodeEngine nodeEngine = this.getNodeEngine();
        Data taskData = nodeEngine.toData(task);
        String uuid = UuidUtil.newUnsecureUuidString();
        boolean sync = !preventSync && this.checkSync();
        Operation op = new CallableTaskOperation(this.name, uuid, taskData).setPartitionId(partitionId);
        InternalCompletableFuture future = this.invokeOnPartition(op);
        if (sync) {
            Object response;
            try {
                response = future.get();
            }
            catch (Exception e) {
                response = e;
            }
            return new CompletedFuture(nodeEngine.getSerializationService(), response, this.getAsyncExecutor());
        }
        return new CancellableDelegatingFuture(future, nodeEngine, uuid, partitionId);
    }

    private boolean checkSync() {
        boolean sync = false;
        long last = this.lastSubmitTime;
        long now = Clock.currentTimeMillis();
        if (last + 10L < now) {
            CONSECUTIVE_SUBMITS.set(this, 0);
        } else if (CONSECUTIVE_SUBMITS.incrementAndGet(this) % 100 == 0) {
            sync = true;
        }
        this.lastSubmitTime = now;
        return sync;
    }

    private <T> int getTaskPartitionId(Callable<T> task) {
        Object partitionKey;
        if (task instanceof PartitionAware && (partitionKey = ((PartitionAware)((Object)task)).getPartitionKey()) != null) {
            return this.getNodeEngine().getPartitionService().getPartitionId(partitionKey);
        }
        return this.random.nextInt(this.partitionCount);
    }

    @Override
    public <T> Future<T> submitToKeyOwner(Callable<T> task, Object key) {
        NodeEngine nodeEngine = this.getNodeEngine();
        return this.submitToPartitionOwner(task, nodeEngine.getPartitionService().getPartitionId(key), false);
    }

    @Override
    public <T> Future<T> submitToMember(Callable<T> task, Member member) {
        Preconditions.checkNotNull(task, "task can't be null");
        this.checkNotShutdown();
        Data taskData = this.getNodeEngine().toData(task);
        return this.submitToMember(taskData, member);
    }

    private <T> Future<T> submitToMember(Data taskData, Member member) {
        NodeEngine nodeEngine = this.getNodeEngine();
        String uuid = UuidUtil.newUnsecureUuidString();
        Address target = member.getAddress();
        boolean sync = this.checkSync();
        MemberCallableTaskOperation op = new MemberCallableTaskOperation(this.name, uuid, taskData);
        InternalCompletableFuture future = nodeEngine.getOperationService().invokeOnTarget("hz:impl:executorService", op, target);
        if (sync) {
            Object response;
            try {
                response = future.get();
            }
            catch (Exception e) {
                response = e;
            }
            return new CompletedFuture(nodeEngine.getSerializationService(), response, this.getAsyncExecutor());
        }
        return new CancellableDelegatingFuture(future, nodeEngine, uuid, target);
    }

    @Override
    public <T> Map<Member, Future<T>> submitToMembers(Callable<T> task, Collection<Member> members) {
        Preconditions.checkNotNull(task, "task can't be null");
        this.checkNotShutdown();
        Data taskData = this.getNodeEngine().toData(task);
        Map<Member, Future<T>> futures = MapUtil.createHashMap(members.size());
        for (Member member : members) {
            Future<T> future = this.submitToMember(taskData, member);
            futures.put(member, future);
        }
        return futures;
    }

    @Override
    public <T> Map<Member, Future<T>> submitToAllMembers(Callable<T> task) {
        NodeEngine nodeEngine = this.getNodeEngine();
        return this.submitToMembers(task, nodeEngine.getClusterService().getMembers());
    }

    public void submit(Runnable task, ExecutionCallback callback) {
        RunnableAdapter callable = this.createRunnableAdapter(task);
        this.submit(callable, callback);
    }

    public void submitToKeyOwner(Runnable task, Object key, ExecutionCallback callback) {
        RunnableAdapter callable = this.createRunnableAdapter(task);
        this.submitToKeyOwner(callable, key, callback);
    }

    public void submitToMember(Runnable task, Member member, ExecutionCallback callback) {
        RunnableAdapter callable = this.createRunnableAdapter(task);
        this.submitToMember(callable, member, callback);
    }

    @Override
    public void submitToMembers(Runnable task, Collection<Member> members, MultiExecutionCallback callback) {
        RunnableAdapter callable = this.createRunnableAdapter(task);
        this.submitToMembers(callable, members, callback);
    }

    @Override
    public void submitToAllMembers(Runnable task, MultiExecutionCallback callback) {
        RunnableAdapter callable = this.createRunnableAdapter(task);
        this.submitToAllMembers(callable, callback);
    }

    private <T> void submitToPartitionOwner(Callable<T> task, ExecutionCallback<T> callback, int partitionId) {
        this.checkNotShutdown();
        NodeEngine nodeEngine = this.getNodeEngine();
        Data taskData = nodeEngine.toData(task);
        CallableTaskOperation op = new CallableTaskOperation(this.name, null, taskData);
        OperationService operationService = nodeEngine.getOperationService();
        operationService.createInvocationBuilder("hz:impl:executorService", (Operation)op, partitionId).setExecutionCallback(callback).invoke();
    }

    @Override
    public <T> void submit(Callable<T> task, ExecutionCallback<T> callback) {
        int partitionId = this.getTaskPartitionId(task);
        this.submitToPartitionOwner(task, callback, partitionId);
    }

    @Override
    public <T> void submitToKeyOwner(Callable<T> task, Object key, ExecutionCallback<T> callback) {
        NodeEngine nodeEngine = this.getNodeEngine();
        this.submitToPartitionOwner(task, callback, nodeEngine.getPartitionService().getPartitionId(key));
    }

    private <T> void submitToMember(Data taskData, Member member, ExecutionCallback<T> callback) {
        this.checkNotShutdown();
        NodeEngine nodeEngine = this.getNodeEngine();
        String uuid = UuidUtil.newUnsecureUuidString();
        MemberCallableTaskOperation op = new MemberCallableTaskOperation(this.name, uuid, taskData);
        OperationService operationService = nodeEngine.getOperationService();
        Address address = member.getAddress();
        operationService.createInvocationBuilder("hz:impl:executorService", (Operation)op, address).setExecutionCallback(callback).invoke();
    }

    @Override
    public <T> void submitToMember(Callable<T> task, Member member, ExecutionCallback<T> callback) {
        this.checkNotShutdown();
        Data taskData = this.getNodeEngine().toData(task);
        this.submitToMember(taskData, member, callback);
    }

    private String getRejectionMessage() {
        return "ExecutorService[" + this.name + "] is shutdown! In order to create a new ExecutorService with name '" + this.name + "', you need to destroy current ExecutorService first!";
    }

    @Override
    public <T> void submitToMembers(Callable<T> task, Collection<Member> members, MultiExecutionCallback callback) {
        NodeEngine nodeEngine = this.getNodeEngine();
        ExecutionCallbackAdapterFactory executionCallbackFactory = new ExecutionCallbackAdapterFactory(nodeEngine.getLogger(ExecutionCallbackAdapterFactory.class), members, callback);
        Data taskData = nodeEngine.toData(task);
        for (Member member : members) {
            this.submitToMember(taskData, member, executionCallbackFactory.callbackFor(member));
        }
    }

    @Override
    public <T> void submitToAllMembers(Callable<T> task, MultiExecutionCallback callback) {
        NodeEngine nodeEngine = this.getNodeEngine();
        this.submitToMembers(task, nodeEngine.getClusterService().getMembers(), callback);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        ArrayList<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
        ArrayList<Future<T>> result = new ArrayList<Future<T>>(tasks.size());
        for (Callable<T> callable : tasks) {
            futures.add(this.submit(callable));
        }
        for (Future future : futures) {
            Object value;
            try {
                value = future.get();
            }
            catch (ExecutionException e) {
                value = e;
            }
            result.add(new CompletedFuture(this.getNodeEngine().getSerializationService(), value, this.getAsyncExecutor()));
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(unit, "unit must not be null");
        Preconditions.checkNotNull(tasks, "tasks must not be null");
        long timeoutNanos = unit.toNanos(timeout);
        ArrayList<Future<T>> futures = new ArrayList<Future<T>>(tasks.size());
        boolean done = false;
        try {
            ArrayList<Future<T>> arrayList;
            for (Callable<T> task : tasks) {
                long start = System.nanoTime();
                int partitionId = this.getTaskPartitionId(task);
                futures.add(this.submitToPartitionOwner(task, partitionId, true));
                timeoutNanos -= System.nanoTime() - start;
            }
            if (timeoutNanos <= 0L) {
                arrayList = futures;
                return arrayList;
            }
            done = this.wait(timeoutNanos, futures);
            arrayList = futures;
            return arrayList;
        }
        catch (Throwable t) {
            this.logger.severe(t);
            ArrayList<Future<T>> arrayList = futures;
            return arrayList;
        }
        finally {
            if (!done) {
                ExecutorServiceProxy.cancelAll(futures);
            }
        }
    }

    private <T> boolean wait(long timeoutNanos, List<Future<T>> futures) throws InterruptedException {
        boolean done = true;
        int size = futures.size();
        for (int i = 0; i < size; ++i) {
            ExecutionException value;
            long start = System.nanoTime();
            try {
                Future<T> future = futures.get(i);
                value = future.get(timeoutNanos, TimeUnit.NANOSECONDS);
            }
            catch (ExecutionException e) {
                value = e;
            }
            catch (TimeoutException e) {
                done = false;
                for (int l = i; l < size; ++l) {
                    Object v;
                    Future<T> f = futures.get(i);
                    if (!f.isDone()) continue;
                    try {
                        v = f.get();
                    }
                    catch (ExecutionException ex) {
                        v = ex;
                    }
                    futures.set(l, new CompletedFuture(this.getNodeEngine().getSerializationService(), v, this.getAsyncExecutor()));
                }
                break;
            }
            futures.set(i, new CompletedFuture(this.getNodeEngine().getSerializationService(), value, this.getAsyncExecutor()));
            timeoutNanos -= System.nanoTime() - start;
        }
        return done;
    }

    private static <T> void cancelAll(List<Future<T>> result) {
        for (Future<T> aResult : result) {
            aResult.cancel(true);
        }
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
    protected void throwNotActiveException() {
        throw new RejectedExecutionException();
    }

    @Override
    public boolean isShutdown() {
        try {
            return ((DistributedExecutorService)this.getService()).isShutdown(this.name);
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
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void shutdown() {
        NodeEngine nodeEngine = this.getNodeEngine();
        Set<Member> members = nodeEngine.getClusterService().getMembers();
        OperationService operationService = nodeEngine.getOperationService();
        LinkedList<InternalCompletableFuture> calls = new LinkedList<InternalCompletableFuture>();
        for (Member member : members) {
            InternalCompletableFuture f = this.submitShutdownOperation(operationService, member);
            calls.add(f);
        }
        FutureUtil.waitWithDeadline(calls, 3L, TimeUnit.SECONDS, this.shutdownExceptionHandler);
    }

    private InternalCompletableFuture submitShutdownOperation(OperationService operationService, Member member) {
        ShutdownOperation op = new ShutdownOperation(this.name);
        return operationService.invokeOnTarget(this.getServiceName(), op, member.getAddress());
    }

    @Override
    public List<Runnable> shutdownNow() {
        this.shutdown();
        return Collections.emptyList();
    }

    @Override
    public LocalExecutorStats getLocalExecutorStats() {
        return ((DistributedExecutorService)this.getService()).getLocalExecutorStats(this.name);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:executorService";
    }

    @Override
    public String getName() {
        return this.name;
    }

    private ExecutorService getAsyncExecutor() {
        return this.getNodeEngine().getExecutionService().getExecutor("hz:async");
    }

    private List<Member> selectMembers(MemberSelector memberSelector) {
        if (memberSelector == null) {
            throw new IllegalArgumentException("memberSelector must not be null");
        }
        ArrayList<Member> selected = new ArrayList<Member>();
        Set<Member> members = this.getNodeEngine().getClusterService().getMembers();
        for (Member member : members) {
            if (!memberSelector.select(member)) continue;
            selected.add(member);
        }
        if (selected.isEmpty()) {
            throw new RejectedExecutionException("No member selected with memberSelector[" + memberSelector + "]");
        }
        return selected;
    }

    @Override
    public String toString() {
        return "IExecutorService{name='" + this.name + '\'' + '}';
    }
}

