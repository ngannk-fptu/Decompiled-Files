/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationparker.impl;

import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.LiveOperations;
import com.hazelcast.spi.LiveOperationsTracker;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.exception.TargetDisconnectedException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationparker.OperationParker;
import com.hazelcast.spi.impl.operationparker.impl.WaitSet;
import com.hazelcast.spi.impl.operationparker.impl.WaitSetEntry;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.executor.SingleExecutorThreadFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OperationParkerImpl
implements OperationParker,
LiveOperationsTracker,
MetricsProvider {
    private static final long FIRST_WAIT_TIME = 1000L;
    private final ConcurrentMap<WaitNotifyKey, WaitSet> waitSetMap = new ConcurrentHashMap<WaitNotifyKey, WaitSet>(100);
    private final DelayQueue delayQueue = new DelayQueue();
    private final ExecutorService expirationExecutor;
    private final Future expirationTaskFuture;
    private final NodeEngineImpl nodeEngine;
    private final ILogger logger;
    private final ConstructorFunction<WaitNotifyKey, WaitSet> waitSetConstructor = new ConstructorFunction<WaitNotifyKey, WaitSet>(){

        @Override
        public WaitSet createNew(WaitNotifyKey key) {
            return new WaitSet(OperationParkerImpl.this.logger, OperationParkerImpl.this.nodeEngine, OperationParkerImpl.this.waitSetMap, OperationParkerImpl.this.delayQueue);
        }
    };

    public OperationParkerImpl(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        Node node = nodeEngine.getNode();
        this.logger = node.getLogger(OperationParker.class);
        this.expirationExecutor = Executors.newSingleThreadExecutor(new SingleExecutorThreadFactory(node.getConfigClassLoader(), ThreadUtil.createThreadName(nodeEngine.getHazelcastInstance().getName(), "operation-parker")));
        this.expirationTaskFuture = this.expirationExecutor.submit(new ExpirationTask());
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "operation-parker");
    }

    @Override
    public void populate(LiveOperations liveOperations) {
        for (WaitSet waitSet : this.waitSetMap.values()) {
            waitSet.populate(liveOperations);
        }
    }

    @Override
    public void park(BlockingOperation op) {
        WaitSet waitSet = ConcurrencyUtil.getOrPutIfAbsent(this.waitSetMap, op.getWaitKey(), this.waitSetConstructor);
        waitSet.park(op);
    }

    @Override
    public void unpark(Notifier notifier) {
        WaitNotifyKey waitNotifyKey = notifier.getNotifiedKey();
        WaitSet waitSet = (WaitSet)this.waitSetMap.get(waitNotifyKey);
        if (waitSet != null) {
            waitSet.unpark(notifier, waitNotifyKey);
        }
    }

    @Probe
    public int getParkQueueCount() {
        return this.waitSetMap.size();
    }

    @Probe
    public int getTotalParkedOperationCount() {
        int count = 0;
        for (WaitSet waitSet : this.waitSetMap.values()) {
            count += waitSet.size();
        }
        return count;
    }

    public int getTotalValidWaitingOperationCount() {
        int count = 0;
        for (WaitSet waitSet : this.waitSetMap.values()) {
            count += waitSet.totalValidWaitingOperationCount();
        }
        return count;
    }

    public void onMemberLeft(MemberImpl leftMember) {
        for (WaitSet waitSet : this.waitSetMap.values()) {
            waitSet.invalidateAll(leftMember.getUuid());
        }
    }

    public void onClientDisconnected(String clientUuid) {
        for (WaitSet waitSet : this.waitSetMap.values()) {
            waitSet.cancelAll(clientUuid, new TargetDisconnectedException("Client disconnected: " + clientUuid));
        }
    }

    public void onPartitionMigrate(MigrationInfo migrationInfo) {
        if (migrationInfo.getSource() == null || !migrationInfo.getSource().isIdentical(this.nodeEngine.getLocalMember())) {
            return;
        }
        for (WaitSet waitSet : this.waitSetMap.values()) {
            waitSet.onPartitionMigrate(migrationInfo);
        }
    }

    @Override
    public void cancelParkedOperations(String serviceName, Object objectId, Throwable cause) {
        for (WaitSet waitSet : this.waitSetMap.values()) {
            waitSet.cancelAll(serviceName, objectId, cause);
        }
    }

    public void reset() {
        this.delayQueue.clear();
        this.waitSetMap.clear();
    }

    public void shutdown() {
        this.logger.finest("Stopping tasks...");
        this.expirationTaskFuture.cancel(true);
        this.expirationExecutor.shutdown();
        for (WaitSet waitSet : this.waitSetMap.values()) {
            waitSet.onShutdown();
        }
        this.waitSetMap.clear();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("OperationParker{");
        sb.append("delayQueue=");
        sb.append(this.delayQueue.size());
        sb.append(" \n[");
        for (WaitSet waitSet : this.waitSetMap.values()) {
            sb.append("\t");
            sb.append(waitSet.size());
            sb.append(", ");
        }
        sb.append("]\n}");
        return sb.toString();
    }

    private class ExpirationTask
    implements Runnable {
        private ExpirationTask() {
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (!this.doRun()) continue;
                    return;
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                catch (Throwable t) {
                    OperationParkerImpl.this.logger.warning(t);
                    continue;
                }
                break;
            }
            return;
        }

        private boolean doRun() throws Exception {
            long waitTime = 1000L;
            while (waitTime > 0L) {
                long end;
                long begin = System.currentTimeMillis();
                WaitSetEntry entry = (WaitSetEntry)OperationParkerImpl.this.delayQueue.poll(waitTime, TimeUnit.MILLISECONDS);
                if (entry != null && entry.isValid()) {
                    this.invalidate(entry);
                }
                if ((waitTime -= (end = System.currentTimeMillis()) - begin) <= 1000L) continue;
                waitTime = 1000L;
            }
            for (WaitSet waitSet : OperationParkerImpl.this.waitSetMap.values()) {
                if (Thread.interrupted()) {
                    return true;
                }
                for (WaitSetEntry entry : waitSet) {
                    if (!entry.isValid() || !entry.needsInvalidation()) continue;
                    this.invalidate(entry);
                }
            }
            return false;
        }

        private void invalidate(WaitSetEntry entry) {
            OperationParkerImpl.this.nodeEngine.getOperationService().execute(entry);
        }
    }
}

