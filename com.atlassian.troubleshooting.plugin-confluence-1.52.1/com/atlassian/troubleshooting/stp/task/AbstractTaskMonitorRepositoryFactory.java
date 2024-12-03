/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.persistence.InMemoryTaskMonitorRepository;
import com.atlassian.troubleshooting.stp.persistence.TaskMonitorRepository;
import com.atlassian.troubleshooting.stp.task.DefaultMonitoredTaskExecutorFactory;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorRepositoryFactory;
import com.atlassian.troubleshooting.stp.task.TaskType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractTaskMonitorRepositoryFactory
implements TaskMonitorRepositoryFactory {
    private final Map<TaskType, TaskMonitorRepository<?>> repositories = new ConcurrentHashMap();

    @Override
    public <V extends TaskMonitor<?>> TaskMonitorRepository<V> getRepository(TaskType taskType) {
        return this.repositories.computeIfAbsent(taskType, key -> new InMemoryTaskMonitorRepository(taskType, DefaultMonitoredTaskExecutorFactory.DEFAULT_TASKS_TIMEOUT_MS));
    }
}

