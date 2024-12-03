/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.util.LocalRetryableExecution;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.internal.util.futures.ChainingFuture;
import com.hazelcast.internal.util.iterator.RestartingMemberIterator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.IterableUtil;
import com.hazelcast.util.executor.CompletedFuture;
import com.hazelcast.util.executor.ManagedExecutorService;
import com.hazelcast.util.function.Supplier;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public final class InvocationUtil {
    private InvocationUtil() {
    }

    public static ICompletableFuture<Object> invokeOnStableClusterSerial(NodeEngine nodeEngine, Supplier<? extends Operation> operationSupplier, int maxRetries) {
        ClusterService clusterService = nodeEngine.getClusterService();
        if (!clusterService.isJoined()) {
            return new CompletedFuture<Object>(null, null, new CallerRunsExecutor());
        }
        RestartingMemberIterator memberIterator = new RestartingMemberIterator(clusterService, maxRetries);
        InvokeOnMemberFunction invokeOnMemberFunction = new InvokeOnMemberFunction(operationSupplier, nodeEngine, memberIterator);
        Iterator invocationIterator = IterableUtil.map(memberIterator, invokeOnMemberFunction);
        ILogger logger = nodeEngine.getLogger(ChainingFuture.class);
        ExecutionService executionService = nodeEngine.getExecutionService();
        ManagedExecutorService executor = executionService.getExecutor("hz:async");
        return new ChainingFuture<Object>(invocationIterator, executor, memberIterator, logger);
    }

    public static LocalRetryableExecution executeLocallyWithRetry(NodeEngine nodeEngine, Operation operation) {
        if (operation.getOperationResponseHandler() != null) {
            throw new IllegalArgumentException("Operation must not have a response handler set");
        }
        if (!operation.returnsResponse()) {
            throw new IllegalArgumentException("Operation must return a response");
        }
        if (operation.validatesTarget()) {
            throw new IllegalArgumentException("Operation must not validate the target");
        }
        LocalRetryableExecution execution = new LocalRetryableExecution(nodeEngine, operation);
        execution.run();
        return execution;
    }

    private static class CallerRunsExecutor
    implements Executor {
        private CallerRunsExecutor() {
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    @SerializableByConvention
    private static class InvokeOnMemberFunction
    implements IFunction<Member, ICompletableFuture<Object>> {
        private static final long serialVersionUID = 2903680336421872278L;
        private final transient Supplier<? extends Operation> operationSupplier;
        private final transient NodeEngine nodeEngine;
        private final transient RestartingMemberIterator memberIterator;
        private final long retryDelayMillis;
        private volatile int lastRetryCount;

        InvokeOnMemberFunction(Supplier<? extends Operation> operationSupplier, NodeEngine nodeEngine, RestartingMemberIterator memberIterator) {
            this.operationSupplier = operationSupplier;
            this.nodeEngine = nodeEngine;
            this.memberIterator = memberIterator;
            this.retryDelayMillis = nodeEngine.getProperties().getMillis(GroupProperty.INVOCATION_RETRY_PAUSE);
        }

        @Override
        public ICompletableFuture<Object> apply(Member member) {
            if (this.isRetry()) {
                return this.invokeOnMemberWithDelay(member);
            }
            return this.invokeOnMember(member);
        }

        private boolean isRetry() {
            int currentRetryCount = this.memberIterator.getRetryCount();
            if (this.lastRetryCount == currentRetryCount) {
                return false;
            }
            this.lastRetryCount = currentRetryCount;
            return true;
        }

        private ICompletableFuture<Object> invokeOnMemberWithDelay(Member member) {
            SimpleCompletableFuture<Object> future = new SimpleCompletableFuture<Object>(this.nodeEngine);
            InvokeOnMemberTask task = new InvokeOnMemberTask(member, future);
            this.nodeEngine.getExecutionService().schedule(task, this.retryDelayMillis, TimeUnit.MILLISECONDS);
            return future;
        }

        private ICompletableFuture<Object> invokeOnMember(Member member) {
            Address address = member.getAddress();
            Operation operation = this.operationSupplier.get();
            String serviceName = operation.getServiceName();
            return this.nodeEngine.getOperationService().invokeOnTarget(serviceName, operation, address);
        }

        private class InvokeOnMemberTask
        implements Runnable {
            private final Member member;
            private final SimpleCompletableFuture<Object> future;

            InvokeOnMemberTask(Member member, SimpleCompletableFuture<Object> future) {
                this.member = member;
                this.future = future;
            }

            @Override
            public void run() {
                InvokeOnMemberFunction.this.invokeOnMember(this.member).andThen(new ExecutionCallback<Object>(){

                    @Override
                    public void onResponse(Object response) {
                        InvokeOnMemberTask.this.future.setResult(response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        InvokeOnMemberTask.this.future.setResult(t);
                    }
                });
            }
        }
    }
}

