/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.PartitionService;
import com.hazelcast.internal.eviction.ClearExpiredRecordsTask;
import com.hazelcast.partition.PartitionLostEvent;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ExpirationManager
implements LifecycleListener,
PartitionLostListener {
    private final int taskPeriodSeconds;
    private final String lifecycleListenerId;
    private final String partitionLostListenerId;
    private final NodeEngine nodeEngine;
    private final ClearExpiredRecordsTask task;
    private final TaskScheduler globalTaskScheduler;
    private final LifecycleService lifecycleService;
    private final PartitionService partitionService;
    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    private final AtomicBoolean scheduledOneTime = new AtomicBoolean(false);
    private volatile ScheduledFuture<?> scheduledExpirationTask;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public ExpirationManager(ClearExpiredRecordsTask task, NodeEngine nodeEngine) {
        this.task = task;
        this.nodeEngine = nodeEngine;
        this.globalTaskScheduler = nodeEngine.getExecutionService().getGlobalTaskScheduler();
        this.taskPeriodSeconds = Preconditions.checkPositive(task.getTaskPeriodSeconds(), "taskPeriodSeconds should be a positive number");
        this.lifecycleService = this.getHazelcastInstance().getLifecycleService();
        this.lifecycleListenerId = this.lifecycleService.addLifecycleListener(this);
        this.partitionService = this.getHazelcastInstance().getPartitionService();
        this.partitionLostListenerId = this.partitionService.addPartitionLostListener(this);
    }

    protected HazelcastInstance getHazelcastInstance() {
        return this.nodeEngine.getHazelcastInstance();
    }

    public void scheduleExpirationTask() {
        if (this.nodeEngine.getLocalMember().isLiteMember() || this.scheduled.get() || !this.scheduled.compareAndSet(false, true)) {
            return;
        }
        this.scheduledExpirationTask = this.globalTaskScheduler.scheduleWithRepetition(this.task, this.taskPeriodSeconds, this.taskPeriodSeconds, TimeUnit.SECONDS);
        this.scheduledOneTime.set(true);
    }

    void unscheduleExpirationTask() {
        this.scheduled.set(false);
        ScheduledFuture<?> scheduledFuture = this.scheduledExpirationTask;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    @Override
    public void stateChanged(LifecycleEvent event) {
        switch (event.getState()) {
            case SHUTTING_DOWN: 
            case MERGING: {
                this.unscheduleExpirationTask();
                this.sendQueuedExpiredKeys();
                break;
            }
            case MERGED: {
                this.rescheduleIfScheduledBefore();
                break;
            }
            default: {
                return;
            }
        }
    }

    private void sendQueuedExpiredKeys() {
        for (Object container : this.task.containers) {
            this.task.sendQueuedExpiredKeys(container);
        }
    }

    @Override
    public void partitionLost(PartitionLostEvent event) {
        this.task.partitionLost(event);
    }

    public void onClusterStateChange(ClusterState newState) {
        if (newState == ClusterState.PASSIVE) {
            this.unscheduleExpirationTask();
        } else {
            this.rescheduleIfScheduledBefore();
        }
    }

    public void onShutdown() {
        this.lifecycleService.removeLifecycleListener(this.lifecycleListenerId);
        this.partitionService.removePartitionLostListener(this.partitionLostListenerId);
    }

    public ClearExpiredRecordsTask getTask() {
        return this.task;
    }

    private void rescheduleIfScheduledBefore() {
        if (!this.scheduledOneTime.get()) {
            return;
        }
        this.scheduleExpirationTask();
    }

    int getTaskPeriodSeconds() {
        return this.taskPeriodSeconds;
    }

    int getCleanupOperationCount() {
        return this.task.getCleanupOperationCount();
    }

    int getCleanupPercentage() {
        return this.task.getCleanupPercentage();
    }

    boolean isScheduled() {
        return this.scheduled.get();
    }
}

