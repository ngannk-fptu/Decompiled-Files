/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.operations.UnlockIfLeaseExpiredOperation;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.exception.RetryableException;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.ScheduledEntry;
import com.hazelcast.util.scheduler.ScheduledEntryProcessor;
import java.util.Collection;

public final class LockEvictionProcessor
implements ScheduledEntryProcessor<Data, Integer> {
    private final NodeEngine nodeEngine;
    private final ObjectNamespace namespace;
    private final ILogger logger;
    private final OperationResponseHandler unlockResponseHandler;

    public LockEvictionProcessor(NodeEngine nodeEngine, ObjectNamespace namespace) {
        this.nodeEngine = nodeEngine;
        this.namespace = namespace;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.unlockResponseHandler = new UnlockResponseHandler();
    }

    @Override
    public void process(EntryTaskScheduler<Data, Integer> scheduler, Collection<ScheduledEntry<Data, Integer>> entries) {
        for (ScheduledEntry<Data, Integer> entry : entries) {
            Data key = entry.getKey();
            int version = entry.getValue();
            this.sendUnlockOperation(key, version);
        }
    }

    private void sendUnlockOperation(Data key, int version) {
        UnlockIfLeaseExpiredOperation operation = new UnlockIfLeaseExpiredOperation(this.namespace, key, version);
        try {
            this.submit(operation, key);
        }
        catch (Throwable t) {
            this.logger.warning(t);
        }
    }

    private void submit(UnlockOperation operation, Data key) {
        int partitionId = this.nodeEngine.getPartitionService().getPartitionId(key);
        OperationService operationService = this.nodeEngine.getOperationService();
        operation.setPartitionId(partitionId);
        operation.setOperationResponseHandler(this.unlockResponseHandler);
        operation.setValidateTarget(false);
        operation.setAsyncBackup(true);
        operationService.invokeOnTarget("hz:impl:lockService", operation, this.nodeEngine.getThisAddress());
    }

    private class UnlockResponseHandler
    implements OperationResponseHandler {
        private UnlockResponseHandler() {
        }

        public void sendResponse(Operation op, Object obj) {
            if (obj instanceof Throwable) {
                Throwable t = (Throwable)obj;
                if (t instanceof RetryableException) {
                    LockEvictionProcessor.this.logger.finest("While unlocking... " + t.getMessage());
                } else {
                    LockEvictionProcessor.this.logger.warning(t);
                }
            }
        }
    }
}

