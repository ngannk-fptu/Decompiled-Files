/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.task;

import com.atlassian.core.task.Task;
import com.atlassian.core.task.TaskManager;
import com.atlassian.core.task.TaskQueue;

public class DefaultTaskManager
implements TaskManager {
    private TaskQueue taskQueue;

    public DefaultTaskManager(TaskQueue queue) {
        this.setTaskQueue(queue);
    }

    @Override
    public TaskQueue getTaskQueue() {
        return this.taskQueue;
    }

    @Override
    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        this.taskQueue.addTask(task);
    }

    @Override
    public void flush() {
        if (this.taskQueue != null) {
            this.taskQueue.flush();
        }
    }
}

