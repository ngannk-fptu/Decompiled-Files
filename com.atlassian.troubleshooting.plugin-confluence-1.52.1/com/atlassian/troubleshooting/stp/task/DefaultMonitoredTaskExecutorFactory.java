/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.stp.task.DefaultMonitoredTaskExecutor;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutor;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutorFactory;
import com.atlassian.troubleshooting.stp.task.TaskMonitorRepositoryFactory;
import com.atlassian.troubleshooting.stp.task.TaskType;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class DefaultMonitoredTaskExecutorFactory
implements MonitoredTaskExecutorFactory {
    static final long DEFAULT_TASKS_TIMEOUT_MS = Long.getLong("troubleshooting.tasks.timeout.ms", TimeUnit.HOURS.toMillis(2L));
    private final ClusterService clusterService;
    private final TaskMonitorRepositoryFactory repositoryFactory;

    @Autowired
    public DefaultMonitoredTaskExecutorFactory(@Nonnull TaskMonitorRepositoryFactory repositoryFactory, @Nonnull ClusterService clusterService) {
        this.repositoryFactory = Objects.requireNonNull(repositoryFactory);
        this.clusterService = Objects.requireNonNull(clusterService);
    }

    @Override
    @Nonnull
    public MonitoredTaskExecutor create(@Nonnull TaskType taskType, int maxThreads) {
        return new DefaultMonitoredTaskExecutor(taskType, maxThreads, this.clusterService, this.repositoryFactory);
    }
}

