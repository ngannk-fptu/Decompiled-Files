/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskType;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface TaskMonitorFactory {
    @Nonnull
    public <V> MutableTaskMonitor<V> newInstance(TaskType var1);
}

