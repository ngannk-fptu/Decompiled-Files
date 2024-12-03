/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import javax.annotation.Nonnull;

public interface TaskMonitorListener<T> {
    public void onFinished(@Nonnull TaskMonitor<T> var1);

    public void onUpdated(@Nonnull TaskMonitor<T> var1);
}

