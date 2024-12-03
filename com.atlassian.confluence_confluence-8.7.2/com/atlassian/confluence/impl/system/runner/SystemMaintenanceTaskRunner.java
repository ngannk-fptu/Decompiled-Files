/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.system.runner;

import com.atlassian.confluence.impl.system.MaintenanceTaskExecutionException;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;

public interface SystemMaintenanceTaskRunner<T extends SystemMaintenanceTask> {
    public void execute(T var1) throws MaintenanceTaskExecutionException;
}

