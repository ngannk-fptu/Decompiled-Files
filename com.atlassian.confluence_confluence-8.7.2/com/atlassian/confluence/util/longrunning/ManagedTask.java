/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.confluence.util.longrunning.DefaultLongRunningTaskManager;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.profiling.Activity;
import com.atlassian.confluence.util.profiling.ActivityMonitor;
import com.atlassian.core.task.longrunning.LongRunningTask;
import org.checkerframework.checker.nullness.qual.NonNull;

final class ManagedTask
implements Runnable {
    private final LongRunningTaskId id;
    private final LongRunningTask task;
    private final DefaultLongRunningTaskManager manager;
    private final ActivityMonitor monitor;

    ManagedTask(@NonNull LongRunningTaskId id, @NonNull LongRunningTask task, @NonNull DefaultLongRunningTaskManager manager, @NonNull ActivityMonitor monitor) {
        this.id = id;
        this.task = task;
        this.manager = manager;
        this.monitor = monitor;
    }

    LongRunningTaskId getId() {
        return this.id;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        String oldThreadName = Thread.currentThread().getName();
        Thread.currentThread().setName(this.getThreadName());
        try (Activity activity = this.monitor.registerStart("", "long-running-task", this.task.getName());){
            this.task.run();
        }
        finally {
            this.manager.taskFinished(this.getId());
            Thread.currentThread().setName(oldThreadName);
        }
    }

    private String getThreadName() {
        return "Long running task: " + this.task.getName();
    }
}

