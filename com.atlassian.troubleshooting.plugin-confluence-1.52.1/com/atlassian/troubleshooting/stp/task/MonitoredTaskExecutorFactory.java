/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutor;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskType;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MonitoredTaskExecutorFactory {
    @Nonnull
    public <V, M extends TaskMonitor<V>> MonitoredTaskExecutor create(TaskType var1, int var2);
}

