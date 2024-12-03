/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.AsyncTaskExecutor
 */
package org.springframework.scheduling;

import org.springframework.core.task.AsyncTaskExecutor;

public interface SchedulingTaskExecutor
extends AsyncTaskExecutor {
    default public boolean prefersShortLivedTasks() {
        return true;
    }
}

