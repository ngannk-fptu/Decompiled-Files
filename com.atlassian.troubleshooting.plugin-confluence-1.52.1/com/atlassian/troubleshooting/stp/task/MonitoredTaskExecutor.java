/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.persistence.TaskMonitorRepository;
import com.atlassian.troubleshooting.stp.task.MonitoredCallable;
import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MonitoredTaskExecutor<V, M extends TaskMonitor<V>> {
    @Nullable
    public M getMonitor(@Nonnull String var1);

    public Collection<M> getMonitors();

    public void shutdown();

    public <MM extends MutableTaskMonitor<V>> MM submit(@Nonnull MonitoredCallable<V, MM> var1, @Nullable String var2);

    default public <MM extends MutableTaskMonitor<V>> MM submit(@Nonnull MonitoredCallable<V, MM> task) {
        return this.submit(task, null);
    }

    @Nonnull
    public TaskMonitorRepository<M> getTaskMonitorRepository();
}

