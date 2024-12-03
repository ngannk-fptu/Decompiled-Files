/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.system;

import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;

public interface SystemMaintenanceTaskQueue {
    public void enqueue(SystemMaintenanceTask var1);

    public void processEntries();
}

