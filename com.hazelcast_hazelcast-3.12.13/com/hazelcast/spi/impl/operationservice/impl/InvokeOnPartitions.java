/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationexecutor.impl.PartitionOperationThread;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareFactoryAccessor;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionIteratingOperation;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.MapUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

final class InvokeOnPartitions {
    private static final int TRY_COUNT = 10;
    private static final int TRY_PAUSE_MILLIS = 300;
    private static final Object NULL_RESULT = new Object(){

        public String toString() {
            return "NULL_RESULT";
        }
    };
    private final OperationServiceImpl operationService;
    private final String serviceName;
    private final OperationFactory operationFactory;
    private final Map<Address, List<Integer>> memberPartitions;
    private final ILogger logger;
    private final AtomicReferenceArray<Object> partitionResults;
    private final AtomicInteger latch;
    private final SimpleCompletableFuture future;
    private boolean invoked;

    InvokeOnPartitions(OperationServiceImpl operationService, String serviceName, OperationFactory operationFactory, Map<Address, List<Integer>> memberPartitions) {
        this.operationService = operationService;
        this.serviceName = serviceName;
        this.operationFactory = operationFactory;
        this.memberPartitions = memberPartitions;
        this.logger = operationService.node.loggingService.getLogger(this.getClass());
        int partitionCount = operationService.nodeEngine.getPartitionService().getPartitionCount();
        int actualPartitionCount = 0;
        for (List<Integer> mp : memberPartitions.values()) {
            actualPartitionCount += mp.size();
        }
        this.partitionResults = new AtomicReferenceArray(partitionCount);
        this.latch = new AtomicInteger(actualPartitionCount);
        this.future = new SimpleCompletableFuture(operationService.nodeEngine);
    }

    <T> Map<Integer, T> invoke() throws Exception {
        return (Map)this.invokeAsync().get();
    }

    <T> ICompletableFuture<Map<Integer, T>> invokeAsync() {
        assert (!this.invoked) : "already invoked";
        this.invoked = true;
        this.ensureNotCallingFromPartitionOperationThread();
        this.invokeOnAllPartitions();
        return this.future;
    }

    private void ensureNotCallingFromPartitionOperationThread() {
        if (Thread.currentThread() instanceof PartitionOperationThread) {
            throw new IllegalThreadStateException(Thread.currentThread() + " cannot make invocation on multiple partitions!");
        }
    }

    private void invokeOnAllPartitions() {
        if (this.memberPartitions.isEmpty()) {
            this.future.setResult(Collections.EMPTY_MAP);
            return;
        }
        for (Map.Entry<Address, List<Integer>> mp : this.memberPartitions.entrySet()) {
            Address address = mp.getKey();
            List<Integer> partitions = mp.getValue();
            PartitionIteratingOperation op = new PartitionIteratingOperation(this.operationFactory, CollectionUtil.toIntArray(partitions));
            this.operationService.createInvocationBuilder(this.serviceName, (Operation)op, address).setTryCount(10).setTryPauseMillis(300L).invoke().andThen(new FirstAttemptExecutionCallback(partitions));
        }
    }

    private void retryPartition(final int partitionId) {
        PartitionAwareOperationFactory partitionAwareFactory = PartitionAwareFactoryAccessor.extractPartitionAware(this.operationFactory);
        Operation operation = partitionAwareFactory != null ? partitionAwareFactory.createPartitionOperation(partitionId) : this.operationFactory.createOperation();
        this.operationService.createInvocationBuilder(this.serviceName, operation, partitionId).invoke().andThen(new ExecutionCallback<Object>(){

            @Override
            public void onResponse(Object response) {
                InvokeOnPartitions.this.setPartitionResult(partitionId, response);
                InvokeOnPartitions.this.decrementLatchAndHandle(1);
            }

            @Override
            public void onFailure(Throwable t) {
                InvokeOnPartitions.this.setPartitionResult(partitionId, t);
                InvokeOnPartitions.this.decrementLatchAndHandle(1);
            }
        });
    }

    private void decrementLatchAndHandle(int count) {
        if (this.latch.addAndGet(-count) > 0) {
            return;
        }
        Map<Integer, Object> result = MapUtil.createHashMap(this.partitionResults.length());
        for (int partitionId = 0; partitionId < this.partitionResults.length(); ++partitionId) {
            Object partitionResult = this.partitionResults.get(partitionId);
            if (partitionResult instanceof Throwable) {
                this.future.setResult(partitionResult);
                return;
            }
            if (partitionResult == null) continue;
            result.put(partitionId, partitionResult == NULL_RESULT ? null : partitionResult);
        }
        this.future.setResult(result);
    }

    private void setPartitionResult(int partition, Object result) {
        if (result == null) {
            result = NULL_RESULT;
        }
        boolean success = this.partitionResults.compareAndSet(partition, null, result);
        assert (success) : "two results for same partition: old=" + this.partitionResults.get(partition) + ", new=" + result;
    }

    private class FirstAttemptExecutionCallback
    implements ExecutionCallback<Object> {
        private final List<Integer> requestedPartitions;

        FirstAttemptExecutionCallback(List<Integer> partitions) {
            this.requestedPartitions = partitions;
        }

        @Override
        public void onResponse(Object response) {
            PartitionIteratingOperation.PartitionResponse result = (PartitionIteratingOperation.PartitionResponse)((InvokeOnPartitions)InvokeOnPartitions.this).operationService.nodeEngine.toObject(response);
            Object[] results = result.getResults();
            int[] responsePartitions = result.getPartitions();
            assert (results.length == responsePartitions.length) : "results.length=" + results.length + ", responsePartitions.length=" + responsePartitions.length;
            assert (results.length <= this.requestedPartitions.size()) : "results.length=" + results.length + ", but was sent to just " + this.requestedPartitions.size() + " partitions";
            if (results.length != this.requestedPartitions.size()) {
                InvokeOnPartitions.this.logger.fine("Responses received for " + responsePartitions.length + " partitions, but " + this.requestedPartitions.size() + " partitions were requested");
            }
            int failedPartitionsCnt = 0;
            for (int i = 0; i < responsePartitions.length; ++i) {
                assert (this.requestedPartitions.contains(responsePartitions[i])) : "Response received for partition " + responsePartitions[i] + ", but that partition wasn't requested";
                if (results[i] instanceof Throwable) {
                    InvokeOnPartitions.this.retryPartition(responsePartitions[i]);
                    ++failedPartitionsCnt;
                    continue;
                }
                InvokeOnPartitions.this.setPartitionResult(responsePartitions[i], results[i]);
            }
            InvokeOnPartitions.this.decrementLatchAndHandle(this.requestedPartitions.size() - failedPartitionsCnt);
        }

        @Override
        public void onFailure(Throwable t) {
            if (((InvokeOnPartitions)InvokeOnPartitions.this).operationService.logger.isFinestEnabled()) {
                ((InvokeOnPartitions)InvokeOnPartitions.this).operationService.logger.finest(t);
            } else {
                ((InvokeOnPartitions)InvokeOnPartitions.this).operationService.logger.warning(t.getMessage());
            }
            for (Integer partition : this.requestedPartitions) {
                InvokeOnPartitions.this.retryPartition(partition);
            }
        }
    }
}

