/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor.impl;

import com.hazelcast.instance.NodeExtension;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationQueue;
import com.hazelcast.spi.impl.operationexecutor.impl.TaskBatch;
import com.hazelcast.util.executor.HazelcastManagedThread;
import java.util.concurrent.TimeUnit;

public abstract class OperationThread
extends HazelcastManagedThread
implements MetricsProvider {
    final int threadId;
    final OperationQueue queue;
    OperationRunner currentRunner;
    @Probe
    private final SwCounter completedTotalCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter completedPacketCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter completedOperationCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter completedPartitionSpecificRunnableCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter completedRunnableCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter errorCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter completedOperationBatchCount = SwCounter.newSwCounter();
    private final boolean priority;
    private final NodeExtension nodeExtension;
    private final ILogger logger;
    private volatile boolean shutdown;

    public OperationThread(String name, int threadId, OperationQueue queue, ILogger logger, NodeExtension nodeExtension, boolean priority, ClassLoader configClassLoader) {
        super(name);
        this.setContextClassLoader(configClassLoader);
        this.queue = queue;
        this.threadId = threadId;
        this.logger = logger;
        this.nodeExtension = nodeExtension;
        this.priority = priority;
    }

    public int getThreadId() {
        return this.threadId;
    }

    public abstract OperationRunner operationRunner(int var1);

    @Override
    public final void run() {
        this.nodeExtension.onThreadStart(this);
        try {
            while (!this.shutdown) {
                Object task;
                try {
                    task = this.queue.take(this.priority);
                }
                catch (InterruptedException e) {
                    continue;
                }
                this.process(task);
            }
        }
        catch (Throwable t) {
            OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(t);
            this.logger.severe(t);
        }
        finally {
            this.nodeExtension.onThreadStop(this);
        }
    }

    private void process(Object task) {
        try {
            if (task.getClass() == Packet.class) {
                this.process((Packet)task);
            } else if (task instanceof Operation) {
                this.process((Operation)task);
            } else if (task instanceof PartitionSpecificRunnable) {
                this.process((PartitionSpecificRunnable)task);
            } else if (task instanceof Runnable) {
                this.process((Runnable)task);
            } else if (task instanceof TaskBatch) {
                this.process((TaskBatch)task);
            } else {
                throw new IllegalStateException("Unhandled task:" + task);
            }
            this.completedTotalCount.inc();
        }
        catch (Throwable t) {
            this.errorCount.inc();
            OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(t);
            this.logger.severe("Failed to process: " + task + " on: " + this.getName(), t);
        }
        finally {
            this.currentRunner = null;
        }
    }

    private void process(Operation operation) {
        this.currentRunner = this.operationRunner(operation.getPartitionId());
        this.currentRunner.run(operation);
        this.completedOperationCount.inc();
    }

    private void process(Packet packet) throws Exception {
        this.currentRunner = this.operationRunner(packet.getPartitionId());
        this.currentRunner.run(packet);
        this.completedPacketCount.inc();
    }

    private void process(PartitionSpecificRunnable runnable) {
        this.currentRunner = this.operationRunner(runnable.getPartitionId());
        this.currentRunner.run(runnable);
        this.completedPartitionSpecificRunnableCount.inc();
    }

    private void process(Runnable runnable) {
        runnable.run();
        this.completedRunnableCount.inc();
    }

    private void process(TaskBatch batch) {
        block6: {
            Object task = batch.next();
            if (task == null) {
                this.completedOperationBatchCount.inc();
                return;
            }
            try {
                if (task instanceof Operation) {
                    this.process((Operation)task);
                    break block6;
                }
                if (task instanceof Runnable) {
                    this.process((Runnable)task);
                    break block6;
                }
                throw new IllegalStateException("Unhandled task: " + task + " from " + batch.taskFactory());
            }
            finally {
                this.queue.add(batch, false);
            }
        }
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "operation.thread[" + this.getName() + "]");
    }

    public final void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }

    public final void awaitTermination(int timeout, TimeUnit unit) throws InterruptedException {
        this.join(unit.toMillis(timeout));
    }
}

