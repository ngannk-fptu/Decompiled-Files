/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling;

import org.springframework.core.task.AsyncTaskExecutor;

public interface SchedulingTaskExecutor
extends AsyncTaskExecutor {
    public boolean prefersShortLivedTasks();
}

