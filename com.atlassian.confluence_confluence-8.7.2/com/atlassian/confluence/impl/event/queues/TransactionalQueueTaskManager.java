/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.core.task.TaskQueue
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.event.queues;

import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.core.task.TaskQueue;
import java.util.Map;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransactionalQueueTaskManager
implements MultiQueueTaskManager {
    private final MultiQueueTaskManager delegate;

    public TransactionalQueueTaskManager(MultiQueueTaskManager delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Transactional(readOnly=true)
    public TaskQueue getTaskQueue(String queueName) {
        return this.delegate.getTaskQueue(queueName);
    }

    public void addTaskQueue(String queueName, TaskQueue taskQueue) {
        this.delegate.addTaskQueue(queueName, taskQueue);
    }

    public TaskQueue removeTaskQueue(String queueName, TaskQueue taskQueue, boolean flush) {
        return this.delegate.removeTaskQueue(queueName, taskQueue, flush);
    }

    public void setTaskQueues(Map<String, TaskQueue> queues) {
        this.delegate.setTaskQueues(queues);
    }

    public void addTask(String queueName, Task task) {
        this.delegate.addTask(queueName, task);
    }

    public void flush(String queueName) {
        this.delegate.flush(queueName);
    }

    public void flush() {
        this.delegate.flush();
    }
}

