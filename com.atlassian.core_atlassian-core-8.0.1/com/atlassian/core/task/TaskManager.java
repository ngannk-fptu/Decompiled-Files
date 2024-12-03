/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.task;

import com.atlassian.core.task.Task;
import com.atlassian.core.task.TaskQueue;

public interface TaskManager {
    public TaskQueue getTaskQueue();

    public void setTaskQueue(TaskQueue var1);

    public void addTask(Task var1);

    public void flush();
}

