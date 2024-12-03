/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.impl.operationexecutor.impl;

import com.hazelcast.instance.NodeExtension;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.internal.util.concurrent.MPSCQueue;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.LiveOperations;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationexecutor.OperationExecutor;
import com.hazelcast.spi.impl.operationexecutor.OperationHostileThread;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationexecutor.OperationRunnerFactory;
import com.hazelcast.spi.impl.operationexecutor.impl.GenericOperationThread;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationQueue;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationQueueImpl;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationThread;
import com.hazelcast.spi.impl.operationexecutor.impl.PartitionOperationThread;
import com.hazelcast.spi.impl.operationexecutor.impl.TaskBatch;
import com.hazelcast.spi.impl.operationservice.PartitionTaskFactory;
import com.hazelcast.spi.impl.operationservice.impl.InboundResponseHandlerSupplier;
import com.hazelcast.spi.impl.operationservice.impl.operations.Backup;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.concurrent.IdleStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class OperationExecutorImpl
implements OperationExecutor,
MetricsProvider {
    public static final HazelcastProperty IDLE_STRATEGY = new HazelcastProperty("hazelcast.operation.partitionthread.idlestrategy", "block");
    private static final int TERMINATION_TIMEOUT_SECONDS = 3;
    private final ILogger logger;
    private final PartitionOperationThread[] partitionThreads;
    private final OperationRunner[] partitionOperationRunners;
    private final OperationQueue genericQueue = new OperationQueueImpl(new LinkedBlockingQueue<Object>(), new LinkedBlockingQueue<Object>());
    private final GenericOperationThread[] genericThreads;
    private final OperationRunner[] genericOperationRunners;
    private final Address thisAddress;
    private final OperationRunner adHocOperationRunner;
    private final int priorityThreadCount;

    public OperationExecutorImpl(HazelcastProperties properties, LoggingService loggerService, Address thisAddress, OperationRunnerFactory runnerFactory, NodeExtension nodeExtension, String hzName, ClassLoader configClassLoader) {
        this.thisAddress = thisAddress;
        this.logger = loggerService.getLogger(OperationExecutorImpl.class);
        this.adHocOperationRunner = runnerFactory.createAdHocRunner();
        this.partitionOperationRunners = this.initPartitionOperationRunners(properties, runnerFactory);
        this.partitionThreads = this.initPartitionThreads(properties, hzName, nodeExtension, configClassLoader);
        this.priorityThreadCount = properties.getInteger(GroupProperty.PRIORITY_GENERIC_OPERATION_THREAD_COUNT);
        this.genericOperationRunners = this.initGenericOperationRunners(properties, runnerFactory);
        this.genericThreads = this.initGenericThreads(hzName, nodeExtension, configClassLoader);
    }

    private OperationRunner[] initPartitionOperationRunners(HazelcastProperties properties, OperationRunnerFactory runnerFactory) {
        OperationRunner[] operationRunners = new OperationRunner[properties.getInteger(GroupProperty.PARTITION_COUNT)];
        for (int partitionId = 0; partitionId < operationRunners.length; ++partitionId) {
            operationRunners[partitionId] = runnerFactory.createPartitionRunner(partitionId);
        }
        return operationRunners;
    }

    private OperationRunner[] initGenericOperationRunners(HazelcastProperties properties, OperationRunnerFactory runnerFactory) {
        int threadCount = properties.getInteger(GroupProperty.GENERIC_OPERATION_THREAD_COUNT);
        if (threadCount <= 0) {
            int coreSize = RuntimeAvailableProcessors.get();
            threadCount = Math.max(2, coreSize / 2);
        }
        OperationRunner[] operationRunners = new OperationRunner[threadCount + this.priorityThreadCount];
        for (int partitionId = 0; partitionId < operationRunners.length; ++partitionId) {
            operationRunners[partitionId] = runnerFactory.createGenericRunner();
        }
        return operationRunners;
    }

    private PartitionOperationThread[] initPartitionThreads(HazelcastProperties properties, String hzName, NodeExtension nodeExtension, ClassLoader configClassLoader) {
        int threadCount = properties.getInteger(GroupProperty.PARTITION_OPERATION_THREAD_COUNT);
        if (threadCount <= 0) {
            int coreSize = RuntimeAvailableProcessors.get();
            threadCount = Math.max(2, coreSize);
        }
        IdleStrategy idleStrategy = InboundResponseHandlerSupplier.getIdleStrategy(properties, IDLE_STRATEGY);
        PartitionOperationThread[] threads = new PartitionOperationThread[threadCount];
        for (int threadId = 0; threadId < threads.length; ++threadId) {
            PartitionOperationThread partitionThread;
            String threadName = ThreadUtil.createThreadPoolName(hzName, "partition-operation") + threadId;
            MPSCQueue<Object> normalQueue = new MPSCQueue<Object>(idleStrategy);
            OperationQueueImpl operationQueue = new OperationQueueImpl(normalQueue, new ConcurrentLinkedQueue<Object>());
            threads[threadId] = partitionThread = new PartitionOperationThread(threadName, threadId, (OperationQueue)operationQueue, this.logger, nodeExtension, this.partitionOperationRunners, configClassLoader);
            normalQueue.setConsumerThread(partitionThread);
        }
        for (int partitionId = 0; partitionId < this.partitionOperationRunners.length; ++partitionId) {
            int threadId = OperationExecutorImpl.getPartitionThreadId(partitionId, threadCount);
            PartitionOperationThread thread = threads[threadId];
            OperationRunner runner = this.partitionOperationRunners[partitionId];
            runner.setCurrentThread(thread);
        }
        return threads;
    }

    static int getPartitionThreadId(int partitionId, int partitionThreadCount) {
        return partitionId % partitionThreadCount;
    }

    private GenericOperationThread[] initGenericThreads(String hzName, NodeExtension nodeExtension, ClassLoader configClassLoader) {
        int threadCount = this.genericOperationRunners.length;
        GenericOperationThread[] threads = new GenericOperationThread[threadCount];
        int threadId = 0;
        for (int threadIndex = 0; threadIndex < threads.length; ++threadIndex) {
            GenericOperationThread operationThread;
            boolean priority = threadIndex < this.priorityThreadCount;
            String baseName = priority ? "priority-generic-operation" : "generic-operation";
            String threadName = ThreadUtil.createThreadPoolName(hzName, baseName) + threadId;
            OperationRunner operationRunner = this.genericOperationRunners[threadIndex];
            threads[threadIndex] = operationThread = new GenericOperationThread(threadName, threadIndex, this.genericQueue, this.logger, nodeExtension, operationRunner, priority, configClassLoader);
            operationRunner.setCurrentThread(operationThread);
            if (threadIndex == this.priorityThreadCount - 1) {
                threadId = 0;
                continue;
            }
            ++threadId;
        }
        return threads;
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "operation");
        registry.collectMetrics(this.genericThreads);
        registry.collectMetrics(this.partitionThreads);
        registry.collectMetrics(this.adHocOperationRunner);
        registry.collectMetrics(this.genericOperationRunners);
        registry.collectMetrics(this.partitionOperationRunners);
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public OperationRunner[] getPartitionOperationRunners() {
        return this.partitionOperationRunners;
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public OperationRunner[] getGenericOperationRunners() {
        return this.genericOperationRunners;
    }

    @Override
    public void populate(LiveOperations liveOperations) {
        this.scan(this.partitionOperationRunners, liveOperations);
        this.scan(this.genericOperationRunners, liveOperations);
    }

    private void scan(OperationRunner[] runners, LiveOperations result) {
        for (OperationRunner runner : runners) {
            Object task = runner.currentTask();
            if (!(task instanceof Operation) || task.getClass() == Backup.class) continue;
            Operation operation = (Operation)task;
            result.add(operation.getCallerAddress(), operation.getCallId());
        }
    }

    @Override
    @Probe(name="runningCount")
    public int getRunningOperationCount() {
        return this.getRunningPartitionOperationCount() + this.getRunningGenericOperationCount();
    }

    @Probe(name="runningPartitionCount")
    private int getRunningPartitionOperationCount() {
        return OperationExecutorImpl.getRunningOperationCount(this.partitionOperationRunners);
    }

    @Probe(name="runningGenericCount")
    private int getRunningGenericOperationCount() {
        return OperationExecutorImpl.getRunningOperationCount(this.genericOperationRunners);
    }

    private static int getRunningOperationCount(OperationRunner[] runners) {
        int result = 0;
        for (OperationRunner runner : runners) {
            if (runner.currentTask() == null) continue;
            ++result;
        }
        return result;
    }

    @Override
    @Probe(name="queueSize", level=ProbeLevel.MANDATORY)
    public int getQueueSize() {
        int size = 0;
        for (PartitionOperationThread partitionThread : this.partitionThreads) {
            size += partitionThread.queue.normalSize();
        }
        return size += this.genericQueue.normalSize();
    }

    @Override
    @Probe(name="priorityQueueSize", level=ProbeLevel.MANDATORY)
    public int getPriorityQueueSize() {
        int size = 0;
        for (PartitionOperationThread partitionThread : this.partitionThreads) {
            size += partitionThread.queue.prioritySize();
        }
        return size += this.genericQueue.prioritySize();
    }

    @Probe
    private int getGenericQueueSize() {
        return this.genericQueue.normalSize();
    }

    @Probe
    private int getGenericPriorityQueueSize() {
        return this.genericQueue.prioritySize();
    }

    @Override
    @Probe(name="completedCount", level=ProbeLevel.MANDATORY)
    public long getExecutedOperationCount() {
        long result = this.adHocOperationRunner.executedOperationsCount();
        for (OperationRunner runner : this.genericOperationRunners) {
            result += runner.executedOperationsCount();
        }
        for (OperationRunner runner : this.partitionOperationRunners) {
            result += runner.executedOperationsCount();
        }
        return result;
    }

    @Override
    @Probe
    public int getPartitionThreadCount() {
        return this.partitionThreads.length;
    }

    @Override
    @Probe
    public int getGenericThreadCount() {
        return this.genericThreads.length;
    }

    @Override
    public int getPartitionThreadId(int partitionId) {
        return OperationExecutorImpl.getPartitionThreadId(partitionId, this.partitionThreads.length);
    }

    @Override
    public void execute(Operation op) {
        Preconditions.checkNotNull(op, "op can't be null");
        this.execute(op, op.getPartitionId(), op.isUrgent());
    }

    @Override
    public void executeOnPartitions(PartitionTaskFactory taskFactory, BitSet partitions) {
        Preconditions.checkNotNull(taskFactory, "taskFactory can't be null");
        Preconditions.checkNotNull(partitions, "partitions can't be null");
        for (PartitionOperationThread partitionThread : this.partitionThreads) {
            TaskBatch batch = new TaskBatch(taskFactory, partitions, partitionThread.threadId, this.partitionThreads.length);
            partitionThread.queue.add(batch, false);
        }
    }

    @Override
    public void execute(PartitionSpecificRunnable task) {
        Preconditions.checkNotNull(task, "task can't be null");
        this.execute(task, task.getPartitionId(), task instanceof UrgentSystemOperation);
    }

    @Override
    public void accept(Packet packet) {
        this.execute(packet, packet.getPartitionId(), packet.isUrgent());
    }

    private void execute(Object task, int partitionId, boolean priority) {
        if (partitionId < 0) {
            this.genericQueue.add(task, priority);
        } else {
            PartitionOperationThread partitionThread = this.partitionThreads[this.toPartitionThreadIndex(partitionId)];
            partitionThread.queue.add(task, priority);
        }
    }

    @Override
    public void executeOnPartitionThreads(Runnable task) {
        Preconditions.checkNotNull(task, "task can't be null");
        boolean priority = task instanceof UrgentSystemOperation;
        for (PartitionOperationThread partitionThread : this.partitionThreads) {
            partitionThread.queue.add(task, priority);
        }
    }

    @Override
    public void run(Operation operation) {
        Preconditions.checkNotNull(operation, "operation can't be null");
        if (!this.isRunAllowed(operation)) {
            throw new IllegalThreadStateException("Operation '" + operation + "' cannot be run in current thread: " + Thread.currentThread());
        }
        OperationRunner operationRunner = this.getOperationRunner(operation);
        operationRunner.run(operation);
    }

    OperationRunner getOperationRunner(Operation operation) {
        Preconditions.checkNotNull(operation, "operation can't be null");
        if (operation.getPartitionId() >= 0) {
            return this.partitionOperationRunners[operation.getPartitionId()];
        }
        Thread currentThread = Thread.currentThread();
        if (!(currentThread instanceof OperationThread)) {
            return this.adHocOperationRunner;
        }
        OperationThread operationThread = (OperationThread)currentThread;
        return operationThread.currentRunner;
    }

    @Override
    public void runOrExecute(Operation op) {
        if (this.isRunAllowed(op)) {
            this.run(op);
        } else {
            this.execute(op);
        }
    }

    @Override
    public boolean isRunAllowed(Operation op) {
        Preconditions.checkNotNull(op, "op can't be null");
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof OperationHostileThread) {
            return false;
        }
        int partitionId = op.getPartitionId();
        if (partitionId < 0) {
            return true;
        }
        if (currentThread.getClass() != PartitionOperationThread.class) {
            return false;
        }
        PartitionOperationThread partitionThread = (PartitionOperationThread)currentThread;
        return this.toPartitionThreadIndex(partitionId) == partitionThread.threadId;
    }

    @Override
    public boolean isInvocationAllowed(Operation op, boolean isAsync) {
        Preconditions.checkNotNull(op, "op can't be null");
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof OperationHostileThread) {
            return false;
        }
        if (isAsync) {
            return true;
        }
        if (op.getPartitionId() < 0) {
            return true;
        }
        if (currentThread.getClass() != PartitionOperationThread.class) {
            return true;
        }
        PartitionOperationThread partitionThread = (PartitionOperationThread)currentThread;
        OperationRunner runner = partitionThread.currentRunner;
        if (runner != null) {
            return runner.getPartitionId() == op.getPartitionId();
        }
        return this.toPartitionThreadIndex(op.getPartitionId()) == partitionThread.threadId;
    }

    public int toPartitionThreadIndex(int partitionId) {
        return partitionId % this.partitionThreads.length;
    }

    @Override
    public void start() {
        this.logger.info("Starting " + this.partitionThreads.length + " partition threads and " + this.genericThreads.length + " generic threads (" + this.priorityThreadCount + " dedicated for priority tasks)");
        OperationExecutorImpl.startAll(this.partitionThreads);
        OperationExecutorImpl.startAll(this.genericThreads);
    }

    private static void startAll(OperationThread[] operationThreads) {
        for (OperationThread thread : operationThreads) {
            thread.start();
        }
    }

    @Override
    public void shutdown() {
        OperationExecutorImpl.shutdownAll(this.partitionThreads);
        OperationExecutorImpl.shutdownAll(this.genericThreads);
        OperationExecutorImpl.awaitTermination(this.partitionThreads);
        OperationExecutorImpl.awaitTermination(this.genericThreads);
    }

    private static void shutdownAll(OperationThread[] operationThreads) {
        for (OperationThread thread : operationThreads) {
            thread.shutdown();
        }
    }

    private static void awaitTermination(OperationThread[] operationThreads) {
        for (OperationThread thread : operationThreads) {
            try {
                thread.awaitTermination(3, TimeUnit.SECONDS);
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public String toString() {
        return "OperationExecutorImpl{node=" + this.thisAddress + '}';
    }
}

