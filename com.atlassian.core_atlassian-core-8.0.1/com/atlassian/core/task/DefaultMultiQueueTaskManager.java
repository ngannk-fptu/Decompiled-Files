/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.core.task;

import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.core.task.TaskQueue;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class DefaultMultiQueueTaskManager
implements MultiQueueTaskManager {
    private final Map<String, TaskQueue> queues = new HashMap<String, TaskQueue>();

    public DefaultMultiQueueTaskManager(String queueName, TaskQueue queue) {
        this.addTaskQueue(queueName, queue);
    }

    public DefaultMultiQueueTaskManager(Map<String, TaskQueue> queues) {
        this.setTaskQueues(queues);
    }

    @Override
    public void addTaskQueue(@Nonnull String name, @Nonnull TaskQueue queue) {
        if (this.queues.containsKey(name)) {
            throw new IllegalArgumentException("The queue specified already exists in the task manager");
        }
        this.queues.put(name, queue);
    }

    @Override
    public TaskQueue removeTaskQueue(String queueName, TaskQueue taskQueue, boolean flush) {
        TaskQueue queue = this.getTaskQueue(queueName);
        if (queue != null && flush) {
            queue.flush();
        }
        return queue;
    }

    @Override
    public TaskQueue getTaskQueue(String name) {
        return this.queues.get(name);
    }

    @Override
    public void setTaskQueues(Map<String, TaskQueue> queues) {
        for (Map.Entry<String, TaskQueue> entry : queues.entrySet()) {
            this.addTaskQueue(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addTask(String queueName, Task task) {
        this.getTaskQueue(queueName).addTask(task);
    }

    @Override
    public void flush(String queueName) {
        this.getTaskQueue(queueName).flush();
    }

    @Override
    public void flush() {
        for (TaskQueue taskQueue : this.queues.values()) {
            taskQueue.flush();
        }
    }
}

