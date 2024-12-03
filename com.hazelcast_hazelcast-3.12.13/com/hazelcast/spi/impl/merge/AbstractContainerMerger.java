/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.merge.AbstractContainerCollector;
import com.hazelcast.spi.merge.MergingValue;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergePolicyProvider;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class AbstractContainerMerger<C, V, T extends MergingValue<V>>
implements Runnable {
    private static final long TIMEOUT_FACTOR = 500L;
    private static final long MINIMAL_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(5L);
    protected final AbstractContainerCollector<C> collector;
    private final Semaphore semaphore = new Semaphore(0);
    private final ExecutionCallback<Object> mergeCallback = new ExecutionCallback<Object>(){

        @Override
        public void onResponse(Object response) {
            AbstractContainerMerger.this.semaphore.release(1);
        }

        @Override
        public void onFailure(Throwable t) {
            AbstractContainerMerger.this.logger.warning("Error while running " + AbstractContainerMerger.this.getLabel() + " merge operation: " + t.getMessage());
            AbstractContainerMerger.this.semaphore.release(1);
        }
    };
    private final ILogger logger;
    private final OperationService operationService;
    private final SplitBrainMergePolicyProvider splitBrainMergePolicyProvider;
    private int operationCount;

    protected AbstractContainerMerger(AbstractContainerCollector<C> collector, NodeEngine nodeEngine) {
        this.collector = collector;
        this.logger = nodeEngine.getLogger(AbstractContainerMerger.class);
        this.operationService = nodeEngine.getOperationService();
        this.splitBrainMergePolicyProvider = nodeEngine.getSplitBrainMergePolicyProvider();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void run() {
        int valueCount = this.collector.getMergingValueCount();
        if (valueCount == 0) {
            return;
        }
        this.runInternal();
        assert (this.operationCount > 0) : "No merge operations have been invoked in AbstractContainerMerger";
        try {
            long timeoutMillis = Math.max((long)valueCount * 500L, MINIMAL_TIMEOUT_MILLIS);
            if (!this.semaphore.tryAcquire(this.operationCount, timeoutMillis, TimeUnit.MILLISECONDS)) {
                this.logger.warning("Split-brain healing for " + this.getLabel() + " didn't finish within the timeout...");
            }
        }
        catch (InterruptedException e) {
            this.logger.finest("Interrupted while waiting for split-brain healing of " + this.getLabel() + "...");
            Thread.currentThread().interrupt();
        }
        finally {
            this.collector.destroy();
        }
    }

    protected abstract String getLabel();

    protected abstract void runInternal();

    protected SplitBrainMergePolicy<V, T> getMergePolicy(MergePolicyConfig mergePolicyConfig) {
        String mergePolicyName = mergePolicyConfig.getPolicy();
        return this.splitBrainMergePolicyProvider.getMergePolicy(mergePolicyName);
    }

    protected void invoke(String serviceName, Operation operation, int partitionId) {
        try {
            ++this.operationCount;
            this.operationService.invokeOnPartition(serviceName, operation, partitionId).andThen(this.mergeCallback);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }
}

