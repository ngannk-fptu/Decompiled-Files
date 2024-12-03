/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.config;

import java.util.Set;
import org.springframework.scheduling.config.ScheduledTask;

public interface ScheduledTaskHolder {
    public Set<ScheduledTask> getScheduledTasks();
}

