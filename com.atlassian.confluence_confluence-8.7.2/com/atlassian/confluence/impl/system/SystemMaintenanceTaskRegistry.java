/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.system;

import com.atlassian.confluence.impl.system.runner.SystemMaintenanceTaskRunner;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTaskType;
import java.util.Optional;

public interface SystemMaintenanceTaskRegistry {
    public <T extends SystemMaintenanceTask> void register(SystemMaintenanceTaskType var1, SystemMaintenanceTaskRunner<T> var2);

    public void unregister(SystemMaintenanceTaskType var1);

    public Optional<SystemMaintenanceTaskRunner> findTaskRunner(SystemMaintenanceTaskType var1);
}

