/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.persistence.TaskMonitorRepository;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskType;

public interface TaskMonitorRepositoryFactory {
    public <V extends TaskMonitor<?>> TaskMonitorRepository<V> getRepository(TaskType var1);
}

