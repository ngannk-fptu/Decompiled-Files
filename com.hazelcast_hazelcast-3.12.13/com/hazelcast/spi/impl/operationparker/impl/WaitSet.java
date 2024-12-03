/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationparker.impl;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.LiveOperations;
import com.hazelcast.spi.LiveOperationsTracker;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.exception.PartitionMigratingException;
import com.hazelcast.spi.impl.operationparker.impl.WaitSetEntry;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WaitSet
implements LiveOperationsTracker,
Iterable<WaitSetEntry> {
    private static final long TIMEOUT_UPPER_BOUND = 1500L;
    private final Queue<WaitSetEntry> queue = new ConcurrentLinkedQueue<WaitSetEntry>();
    private final ILogger logger;
    private final NodeEngine nodeEngine;
    private final Map<WaitNotifyKey, WaitSet> waitSetMap;
    private final Queue<WaitSetEntry> delayQueue;

    public WaitSet(ILogger logger, NodeEngine nodeEngine, Map<WaitNotifyKey, WaitSet> waitSetMap, Queue<WaitSetEntry> delayQueue) {
        this.nodeEngine = nodeEngine;
        this.logger = logger;
        this.waitSetMap = waitSetMap;
        this.delayQueue = delayQueue;
    }

    @Override
    public void populate(LiveOperations liveOperations) {
        for (WaitSetEntry entry : this.queue) {
            Operation operation = entry.getOperation();
            liveOperations.add(operation.getCallerAddress(), operation.getCallId());
        }
    }

    public void park(BlockingOperation op) {
        long timeout = op.getWaitTimeout();
        WaitSetEntry entry = new WaitSetEntry(this.queue, op);
        entry.setNodeEngine(this.nodeEngine);
        this.queue.offer(entry);
        if (timeout > -1L && timeout < 1500L) {
            this.delayQueue.offer(entry);
        }
    }

    public void unpark(Notifier notifier, WaitNotifyKey key) {
        WaitSetEntry entry = this.queue.peek();
        while (entry != null) {
            Operation op = entry.getOperation();
            if (notifier == op) {
                throw new IllegalStateException("Found cyclic wait-notify! -> " + notifier);
            }
            if (entry.isValid()) {
                if (entry.isExpired()) {
                    entry.onExpire();
                } else if (entry.isCancelled()) {
                    entry.onCancel();
                } else {
                    if (entry.shouldWait()) {
                        return;
                    }
                    OperationService operationService = this.nodeEngine.getOperationService();
                    operationService.run(op);
                }
                entry.setValid(false);
            }
            this.queue.poll();
            entry = this.queue.peek();
            if (entry != null) continue;
            this.waitSetMap.remove(key);
        }
    }

    void onPartitionMigrate(MigrationInfo migrationInfo) {
        Iterator it = this.queue.iterator();
        int partitionId = migrationInfo.getPartitionId();
        while (it.hasNext()) {
            Operation op;
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            WaitSetEntry entry = (WaitSetEntry)it.next();
            if (!entry.isValid() || partitionId != (op = entry.getOperation()).getPartitionId()) continue;
            entry.setValid(false);
            PartitionMigratingException pme = new PartitionMigratingException(this.nodeEngine.getThisAddress(), partitionId, op.getClass().getName(), op.getServiceName());
            op.sendResponse(pme);
            it.remove();
        }
    }

    public void onShutdown() {
        HazelcastInstanceNotActiveException response = new HazelcastInstanceNotActiveException();
        Address thisAddress = this.nodeEngine.getThisAddress();
        for (WaitSetEntry entry : this.queue) {
            if (!entry.isValid()) continue;
            Operation op = entry.getOperation();
            if (thisAddress.equals(op.getCallerAddress())) {
                try {
                    OperationResponseHandler responseHandler = op.getOperationResponseHandler();
                    responseHandler.sendResponse(op, response);
                }
                catch (Exception e) {
                    this.logger.finest("While sending HazelcastInstanceNotActiveException response...", e);
                }
            }
            this.queue.clear();
        }
    }

    public void invalidateAll(String callerUuid) {
        for (WaitSetEntry entry : this.queue) {
            Operation op;
            if (!entry.isValid() || !callerUuid.equals((op = entry.getOperation()).getCallerUuid())) continue;
            entry.setValid(false);
        }
    }

    public void cancelAll(String callerUuid, Throwable cause) {
        for (WaitSetEntry entry : this.queue) {
            Operation op;
            if (!entry.isValid() || !callerUuid.equals((op = entry.getOperation()).getCallerUuid())) continue;
            entry.cancel(cause);
        }
    }

    public void cancelAll(String serviceName, Object objectId, Throwable cause) {
        for (WaitSetEntry entry : this.queue) {
            WaitNotifyKey wnk;
            if (!entry.isValid() || !serviceName.equals((wnk = entry.blockingOperation.getWaitKey()).getServiceName()) || !objectId.equals(wnk.getObjectName())) continue;
            entry.cancel(cause);
        }
    }

    WaitSetEntry find(Operation op) {
        for (WaitSetEntry entry : this.queue) {
            if (entry.op != op) continue;
            return entry;
        }
        return null;
    }

    public int size() {
        return this.queue.size();
    }

    public int totalValidWaitingOperationCount() {
        int count = 0;
        for (WaitSetEntry entry : this.queue) {
            if (!entry.valid) continue;
            ++count;
        }
        return count;
    }

    @Override
    public Iterator<WaitSetEntry> iterator() {
        return this.queue.iterator();
    }
}

