/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.task;

import com.atlassian.core.task.AbstractTaskQueue;
import com.atlassian.core.task.FifoBuffer;
import com.atlassian.core.task.Task;
import com.atlassian.core.task.TaskQueue;
import com.atlassian.core.task.TaskQueueWithErrorQueue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractErrorQueuedTaskQueue
extends AbstractTaskQueue
implements TaskQueueWithErrorQueue {
    private static final String DEFAULT_QUEUE_NAME = AbstractErrorQueuedTaskQueue.class.getSimpleName();
    private static final transient Logger log = LoggerFactory.getLogger(AbstractErrorQueuedTaskQueue.class);
    private final TaskQueue errorQueue;
    private int retryCount = 5;
    private List<Task> failed;

    public AbstractErrorQueuedTaskQueue(TaskQueue errorQueue, FifoBuffer<Task> buffer) {
        this(errorQueue, buffer, DEFAULT_QUEUE_NAME);
    }

    public AbstractErrorQueuedTaskQueue(TaskQueue errorQueue, FifoBuffer<Task> buffer, String queueName) {
        super(buffer, queueName);
        this.errorQueue = errorQueue;
    }

    @Override
    public void flush() {
        this.failed = new ArrayList<Task>();
        super.flush();
        for (Task task : this.failed) {
            this.addTask(task);
        }
    }

    @Override
    protected void handleException(Task task, Exception rootException) {
        TaskDecorator theTask = (TaskDecorator)task;
        if (theTask.getExecutionCount() > this.retryCount) {
            this.errorQueue.addTask(theTask.getTask());
        } else {
            this.failed.add(task);
        }
        if (rootException instanceof MessagingException) {
            Exception e = rootException;
            while (e instanceof MessagingException) {
                MessagingException me = (MessagingException)e;
                log.error(me.getMessage(), (Throwable)me);
                e = me.getNextException();
            }
        } else {
            log.error("Failed to execute task", (Throwable)rootException);
        }
    }

    @Override
    public void addTask(Task task) {
        if (task instanceof TaskDecorator) {
            super.addTask(task);
        } else {
            super.addTask(new TaskDecorator(task));
        }
    }

    @Override
    public TaskQueue getErrorQueue() {
        return this.errorQueue;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public static class TaskDecorator
    implements Task,
    Serializable {
        private final Task task;
        private final AtomicInteger executionCount = new AtomicInteger();

        public TaskDecorator(Task task) {
            this.task = task;
        }

        @Override
        public void execute() throws Exception {
            this.executionCount.incrementAndGet();
            this.task.execute();
        }

        public int getExecutionCount() {
            return this.executionCount.get();
        }

        public Task getTask() {
            return this.task;
        }
    }
}

