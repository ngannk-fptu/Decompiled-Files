/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.scheduledexecutor.DuplicateTaskException;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.ScheduledTaskStatistics;
import com.hazelcast.scheduledexecutor.StaleTaskException;
import com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskStatisticsImpl;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.scheduledexecutor.impl.TaskRunner;
import com.hazelcast.scheduledexecutor.impl.operations.SyncStateOperation;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ScheduledExecutorContainer {
    protected final ConcurrentMap<String, ScheduledTaskDescriptor> tasks;
    private final ILogger logger;
    private final String name;
    private final NodeEngine nodeEngine;
    private final InternalExecutionService executionService;
    private final int partitionId;
    private final int durability;
    private final int capacity;

    ScheduledExecutorContainer(String name, int partitionId, NodeEngine nodeEngine, int durability, int capacity) {
        this(name, partitionId, nodeEngine, durability, capacity, new ConcurrentHashMap<String, ScheduledTaskDescriptor>());
    }

    ScheduledExecutorContainer(String name, int partitionId, NodeEngine nodeEngine, int durability, int capacity, ConcurrentMap<String, ScheduledTaskDescriptor> tasks) {
        this.logger = nodeEngine.getLogger(this.getClass());
        this.name = name;
        this.nodeEngine = nodeEngine;
        this.executionService = (InternalExecutionService)nodeEngine.getExecutionService();
        this.partitionId = partitionId;
        this.durability = durability;
        this.capacity = capacity;
        this.tasks = tasks;
    }

    public ScheduledFuture schedule(TaskDefinition definition) {
        this.checkNotDuplicateTask(definition.getName());
        this.checkNotAtCapacity();
        return this.createContextAndSchedule(definition);
    }

    public boolean cancel(String taskName) {
        this.checkNotStaleTask(taskName);
        this.log(Level.FINEST, taskName, "Canceling");
        return ((ScheduledTaskDescriptor)this.tasks.get(taskName)).cancel(true);
    }

    public boolean has(String taskName) {
        return this.tasks.containsKey(taskName);
    }

    public Object get(String taskName) throws ExecutionException, InterruptedException {
        this.checkNotStaleTask(taskName);
        return ((ScheduledTaskDescriptor)this.tasks.get(taskName)).get();
    }

    public long getDelay(String taskName, TimeUnit unit) {
        this.checkNotStaleTask(taskName);
        return ((ScheduledTaskDescriptor)this.tasks.get(taskName)).getDelay(unit);
    }

    public ScheduledTaskStatistics getStatistics(String taskName) {
        this.checkNotStaleTask(taskName);
        ScheduledTaskDescriptor descriptor = (ScheduledTaskDescriptor)this.tasks.get(taskName);
        return descriptor.getStatsSnapshot();
    }

    public boolean isCancelled(String taskName) {
        this.checkNotStaleTask(taskName);
        return ((ScheduledTaskDescriptor)this.tasks.get(taskName)).isCancelled();
    }

    public boolean isDone(String taskName) {
        this.checkNotStaleTask(taskName);
        return ((ScheduledTaskDescriptor)this.tasks.get(taskName)).isDone();
    }

    public void destroy() {
        this.log(Level.FINEST, "Destroying container...");
        for (ScheduledTaskDescriptor descriptor : this.tasks.values()) {
            try {
                descriptor.cancel(true);
            }
            catch (Exception ex) {
                this.log(Level.WARNING, descriptor.getDefinition().getName(), "Error while destroying", ex);
            }
        }
    }

    public void dispose(String taskName) {
        this.checkNotStaleTask(taskName);
        this.log(Level.FINEST, taskName, "Disposing");
        ScheduledTaskDescriptor descriptor = (ScheduledTaskDescriptor)this.tasks.get(taskName);
        descriptor.cancel(true);
        this.tasks.remove(taskName);
    }

    public void enqueueSuspended(TaskDefinition definition) {
        this.enqueueSuspended(new ScheduledTaskDescriptor(definition), false);
    }

    public void enqueueSuspended(ScheduledTaskDescriptor descriptor, boolean force) {
        if (this.logger.isFinestEnabled()) {
            this.log(Level.FINEST, "Enqueuing suspended, i.e., backup: " + descriptor.getDefinition());
        }
        if (force || !this.tasks.containsKey(descriptor.getDefinition().getName())) {
            this.tasks.put(descriptor.getDefinition().getName(), descriptor);
        }
    }

    public Collection<ScheduledTaskDescriptor> getTasks() {
        return this.tasks.values();
    }

    public void syncState(String taskName, Map newState, ScheduledTaskStatisticsImpl stats, ScheduledTaskResult resolution) {
        ScheduledTaskDescriptor descriptor = (ScheduledTaskDescriptor)this.tasks.get(taskName);
        if (descriptor == null) {
            this.log(Level.FINEST, taskName, "Sync state attempt on a defunct descriptor");
            return;
        }
        if (this.logger.isFinestEnabled()) {
            this.log(Level.FINEST, taskName, "New state received " + newState);
        }
        descriptor.setState(newState);
        descriptor.setStats(stats);
        if (descriptor.getTaskResult() != null) {
            if (this.logger.isFineEnabled()) {
                this.log(Level.FINE, taskName, String.format("New state ignored! Current: %s New: %s ", descriptor.getTaskResult(), resolution));
            }
        } else {
            descriptor.setTaskResult(resolution);
        }
    }

    public boolean shouldParkGetResult(String taskName) {
        return this.tasks.containsKey(taskName) && (((ScheduledTaskDescriptor)this.tasks.get(taskName)).getTaskResult() == null || !this.isDone(taskName));
    }

    public int getDurability() {
        return this.durability;
    }

    public String getName() {
        return this.name;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    public ScheduledTaskHandler offprintHandler(String taskName) {
        return ScheduledTaskHandlerImpl.of(this.partitionId, this.getName(), taskName);
    }

    public void promoteSuspended() {
        for (ScheduledTaskDescriptor descriptor : this.tasks.values()) {
            try {
                this.log(Level.FINEST, descriptor.getDefinition().getName(), "Attempting promotion");
                if (!descriptor.shouldSchedule()) continue;
                this.doSchedule(descriptor);
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
    }

    public ScheduledTaskDescriptor merge(SplitBrainMergeTypes.ScheduledExecutorMergeTypes mergingEntry, SplitBrainMergePolicy<ScheduledTaskDescriptor, SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergePolicy) {
        SerializationService serializationService = this.nodeEngine.getSerializationService();
        serializationService.getManagedContext().initialize(mergingEntry);
        serializationService.getManagedContext().initialize(mergePolicy);
        ScheduledTaskDescriptor mergingTask = (ScheduledTaskDescriptor)mergingEntry.getValue();
        ScheduledTaskDescriptor existingTask = null;
        for (ScheduledTaskDescriptor task : this.tasks.values()) {
            if (!mergingTask.equals(task)) continue;
            existingTask = task;
            break;
        }
        if (existingTask == null) {
            ScheduledTaskDescriptor newTask = mergePolicy.merge(mergingEntry, null);
            if (newTask != null) {
                this.enqueueSuspended(newTask, false);
                return newTask;
            }
        } else {
            SplitBrainMergeTypes.ScheduledExecutorMergeTypes existingEntry = MergingValueFactory.createMergingEntry(serializationService, existingTask);
            ScheduledTaskDescriptor newTask = mergePolicy.merge(mergingEntry, existingEntry);
            if (newTask != null && newTask != existingTask) {
                existingTask.cancel(true);
                this.enqueueSuspended(newTask, true);
                return newTask;
            }
        }
        return null;
    }

    ScheduledFuture createContextAndSchedule(TaskDefinition definition) {
        if (this.logger.isFinestEnabled()) {
            this.log(Level.FINEST, "Creating new task context for " + definition);
        }
        ScheduledTaskDescriptor descriptor = new ScheduledTaskDescriptor(definition);
        if (this.tasks.putIfAbsent(definition.getName(), descriptor) == null) {
            this.doSchedule(descriptor);
        }
        if (this.logger.isFinestEnabled()) {
            this.log(Level.FINEST, "Queue size: " + this.tasks.size());
        }
        return descriptor.getScheduledFuture();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Map<String, ScheduledTaskDescriptor> prepareForReplication(boolean migrationMode) {
        Map<String, ScheduledTaskDescriptor> replicas = MapUtil.createHashMap(this.tasks.size());
        for (ScheduledTaskDescriptor descriptor : this.tasks.values()) {
            try {
                ScheduledTaskDescriptor replica = new ScheduledTaskDescriptor(descriptor.getDefinition(), descriptor.getState(), descriptor.getStatsSnapshot(), descriptor.getTaskResult());
                replicas.put(descriptor.getDefinition().getName(), replica);
            }
            catch (Exception ex) {
                ExceptionUtil.sneakyThrow(ex);
            }
            finally {
                if (!migrationMode) continue;
                try {
                    descriptor.suspend();
                }
                catch (Exception ex) {
                    throw ExceptionUtil.rethrow(ex);
                }
            }
        }
        return replicas;
    }

    void checkNotDuplicateTask(String taskName) {
        if (this.tasks.containsKey(taskName)) {
            throw new DuplicateTaskException("There is already a task with the same name '" + taskName + "' in '" + this.getName() + "'");
        }
    }

    void checkNotAtCapacity() {
        if (this.capacity != 0 && this.tasks.size() >= this.capacity) {
            throw new RejectedExecutionException("Maximum capacity (" + this.capacity + ") of tasks reached, for scheduled executor (" + this.name + "). Reminder that tasks must be disposed if not needed.");
        }
    }

    void publishTaskState(String taskName, Map stateSnapshot, ScheduledTaskStatisticsImpl statsSnapshot, ScheduledTaskResult result) {
        if (this.logger.isFinestEnabled()) {
            this.log(Level.FINEST, "Publishing state, to replicas. State: " + stateSnapshot);
        }
        SyncStateOperation op = new SyncStateOperation(this.getName(), taskName, stateSnapshot, statsSnapshot, result);
        this.createInvocationBuilder(op).invoke().join();
    }

    protected InvocationBuilder createInvocationBuilder(Operation op) {
        OperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder("hz:impl:scheduledExecutorService", op, this.partitionId);
    }

    protected void log(Level level, String msg) {
        this.log(level, null, msg);
    }

    protected void log(Level level, String taskName, String msg) {
        this.log(level, taskName, msg, null);
    }

    protected void log(Level level, String taskName, String msg, Throwable t) {
        if (this.logger.isLoggable(level)) {
            StringBuilder log = new StringBuilder();
            log.append("[Scheduler: " + this.name + "][Partition: " + this.partitionId + "]");
            if (taskName != null) {
                log.append("[Task: " + taskName + "] ");
            }
            log.append(msg);
            this.logger.log(level, log.toString(), t);
        }
    }

    private <V> void doSchedule(ScheduledTaskDescriptor descriptor) {
        ScheduledFuture<?> future;
        assert (descriptor.getScheduledFuture() == null);
        TaskDefinition definition = descriptor.getDefinition();
        if (this.logger.isFinestEnabled()) {
            this.log(Level.FINEST, definition.getName(), "Scheduled");
        }
        switch (definition.getType()) {
            case SINGLE_RUN: {
                TaskRunner runner = new TaskRunner(this, descriptor);
                future = new DelegatingScheduledFutureStripper(this.executionService.scheduleDurable(this.name, runner, definition.getInitialDelay(), definition.getUnit()));
                break;
            }
            case AT_FIXED_RATE: {
                TaskRunner runner = new TaskRunner(this, descriptor);
                future = this.executionService.scheduleDurableWithRepetition(this.name, runner, definition.getInitialDelay(), definition.getPeriod(), definition.getUnit());
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        descriptor.setScheduledFuture(future);
    }

    private void checkNotStaleTask(String taskName) {
        if (!this.has(taskName)) {
            throw new StaleTaskException("Task with name " + taskName + " not found. ");
        }
    }
}

