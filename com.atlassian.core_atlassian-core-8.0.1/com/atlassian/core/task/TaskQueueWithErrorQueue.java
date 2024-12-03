/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.task;

import com.atlassian.core.task.TaskQueue;

public interface TaskQueueWithErrorQueue
extends TaskQueue {
    public TaskQueue getErrorQueue();
}

