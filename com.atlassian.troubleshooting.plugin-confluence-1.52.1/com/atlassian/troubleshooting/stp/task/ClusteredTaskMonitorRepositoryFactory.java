/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.stp.persistence.ActiveObjectsTaskMonitorRepository;
import com.atlassian.troubleshooting.stp.persistence.TaskMonitorRepository;
import com.atlassian.troubleshooting.stp.task.AbstractTaskMonitorRepositoryFactory;
import com.atlassian.troubleshooting.stp.task.DefaultMonitoredTaskExecutorFactory;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorFactory;
import com.atlassian.troubleshooting.stp.task.TaskMonitorRepositoryFactory;
import com.atlassian.troubleshooting.stp.task.TaskType;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class ClusteredTaskMonitorRepositoryFactory
extends AbstractTaskMonitorRepositoryFactory
implements TaskMonitorRepositoryFactory {
    private final ActiveObjects activeObjects;
    private final ClusterService clusterService;
    private final PluginInfo pluginInfo;
    private final TaskMonitorFactory taskMonitorFactory;

    public ClusteredTaskMonitorRepositoryFactory(ActiveObjects activeObjects, ClusterService clusterService, PluginInfo pluginInfo, TaskMonitorFactory taskMonitorFactory) {
        this.activeObjects = Objects.requireNonNull(activeObjects);
        this.clusterService = Objects.requireNonNull(clusterService);
        this.pluginInfo = Objects.requireNonNull(pluginInfo);
        this.taskMonitorFactory = Objects.requireNonNull(taskMonitorFactory);
    }

    @Override
    public <V extends TaskMonitor<?>> TaskMonitorRepository<V> getRepository(TaskType taskType) {
        TaskMonitorRepository defaultRepository = super.getRepository(taskType);
        return !this.clusterService.isClustered() ? defaultRepository : this.newAOBasedRepository(taskType, defaultRepository);
    }

    private <V extends TaskMonitor<?>> ActiveObjectsTaskMonitorRepository<V> newAOBasedRepository(TaskType taskType, TaskMonitorRepository<V> defaultRepository) {
        return new ActiveObjectsTaskMonitorRepository<V>(this.activeObjects, defaultRepository, taskType, DefaultMonitoredTaskExecutorFactory.DEFAULT_TASKS_TIMEOUT_MS, true, this.pluginInfo, this.taskMonitorFactory);
    }
}

