/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.scheduling.config;

import java.util.concurrent.ScheduledFuture;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.config.Task;

public final class ScheduledTask {
    private final Task task;
    @Nullable
    volatile ScheduledFuture<?> future;

    ScheduledTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return this.task;
    }

    public void cancel() {
        this.cancel(true);
    }

    public void cancel(boolean mayInterruptIfRunning) {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(mayInterruptIfRunning);
        }
    }

    public String toString() {
        return this.task.toString();
    }
}

