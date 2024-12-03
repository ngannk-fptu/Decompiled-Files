/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.task;

import com.atlassian.core.task.Task;
import com.atlassian.core.task.TaskQueue;
import java.util.Map;

public interface MultiQueueTaskManager {
    public TaskQueue getTaskQueue(String var1);

    public void addTaskQueue(String var1, TaskQueue var2);

    public TaskQueue removeTaskQueue(String var1, TaskQueue var2, boolean var3);

    public void setTaskQueues(Map<String, TaskQueue> var1);

    public void addTask(String var1, Task var2);

    public void flush(String var1);

    public void flush();
}

