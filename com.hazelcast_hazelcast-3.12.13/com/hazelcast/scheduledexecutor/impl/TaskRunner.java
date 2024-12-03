/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.scheduledexecutor.StatefulTask;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskStatisticsImpl;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.scheduledexecutor.impl.operations.ResultReadyNotifyOperation;
import com.hazelcast.util.ExceptionUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;

class TaskRunner<V>
implements Callable<V>,
Runnable {
    private final ScheduledExecutorContainer container;
    private final String taskName;
    private final Callable<V> original;
    private final ScheduledTaskDescriptor descriptor;
    private final ScheduledTaskStatisticsImpl statistics;
    private boolean initted;
    private ScheduledTaskResult resolution;

    TaskRunner(ScheduledExecutorContainer container, ScheduledTaskDescriptor descriptor) {
        this.container = container;
        this.descriptor = descriptor;
        this.original = descriptor.getDefinition().getCommand();
        this.taskName = descriptor.getDefinition().getName();
        this.statistics = descriptor.getStatsSnapshot();
        this.statistics.onInit();
    }

    @Override
    public V call() throws Exception {
        this.beforeRun();
        try {
            V result = this.original.call();
            if (TaskDefinition.Type.SINGLE_RUN.equals((Object)this.descriptor.getDefinition().getType())) {
                this.resolution = new ScheduledTaskResult(result);
            }
            V v = result;
            return v;
        }
        catch (Throwable t) {
            this.container.log(Level.WARNING, this.taskName, "Exception occurred during run", t);
            this.resolution = new ScheduledTaskResult(t);
            throw ExceptionUtil.rethrow(t);
        }
        finally {
            this.afterRun();
        }
    }

    @Override
    public void run() {
        try {
            this.call();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private void initOnce() {
        if (this.initted) {
            return;
        }
        Map<?, ?> snapshot = this.descriptor.getState();
        if (this.original instanceof StatefulTask && !snapshot.isEmpty()) {
            ((StatefulTask)((Object)this.original)).load(snapshot);
        }
        this.initted = true;
    }

    private void beforeRun() {
        this.container.log(Level.FINEST, this.taskName, "Entering running mode");
        try {
            this.initOnce();
            this.statistics.onBeforeRun();
        }
        catch (Exception ex) {
            this.container.log(Level.WARNING, this.taskName, "Unexpected exception during beforeRun occurred", ex);
        }
    }

    private void afterRun() {
        try {
            this.statistics.onAfterRun();
            HashMap state = new HashMap();
            if (this.original instanceof StatefulTask) {
                ((StatefulTask)((Object)this.original)).save(state);
            }
            this.container.publishTaskState(this.taskName, state, this.statistics.snapshot(), this.resolution);
        }
        catch (Exception ex) {
            this.container.log(Level.WARNING, this.taskName, "Unexpected exception during afterRun occurred", ex);
        }
        finally {
            this.notifyResultReady();
        }
        this.container.log(Level.FINEST, this.taskName, "Exiting running mode");
    }

    private void notifyResultReady() {
        ResultReadyNotifyOperation op = new ResultReadyNotifyOperation(this.container.offprintHandler(this.taskName));
        this.container.createInvocationBuilder(op).setCallTimeout(Long.MAX_VALUE).invoke();
    }
}

