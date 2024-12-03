/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.system;

import com.atlassian.confluence.impl.system.SystemMaintenanceTaskRegistry;
import com.atlassian.confluence.impl.system.runner.SystemMaintenanceTaskRunner;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTaskType;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultSystemMaintenanceTaskRegistry
implements SystemMaintenanceTaskRegistry {
    private final ConcurrentMap<SystemMaintenanceTaskType, SystemMaintenanceTaskRunner> taskRunnerMaps = new ConcurrentHashMap<SystemMaintenanceTaskType, SystemMaintenanceTaskRunner>();

    @Override
    public <T extends SystemMaintenanceTask> void register(SystemMaintenanceTaskType taskType, SystemMaintenanceTaskRunner<T> taskRunner) {
        this.taskRunnerMaps.put(taskType, taskRunner);
    }

    @Override
    public void unregister(SystemMaintenanceTaskType taskType) {
        this.taskRunnerMaps.remove((Object)taskType);
    }

    @Override
    public Optional<SystemMaintenanceTaskRunner> findTaskRunner(SystemMaintenanceTaskType taskType) {
        return Optional.ofNullable((SystemMaintenanceTaskRunner)this.taskRunnerMaps.get((Object)taskType));
    }
}

