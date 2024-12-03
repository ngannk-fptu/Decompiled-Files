/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.AbstractErrorQueuedTaskQueue
 */
package com.atlassian.confluence.jmx;

import com.atlassian.core.task.AbstractErrorQueuedTaskQueue;
import java.sql.Timestamp;

public class TaskQueueWrapper {
    private AbstractErrorQueuedTaskQueue queue;

    public int getRetryCount() {
        return this.queue.getRetryCount();
    }

    public int getErrorQueueSize() {
        return this.queue.getErrorQueue().size();
    }

    public int size() {
        return this.queue.size();
    }

    public boolean isFlushing() {
        return this.queue.isFlushing();
    }

    public Timestamp getFlushStarted() {
        return this.queue.getFlushStarted();
    }

    public int getTasksSize() {
        return this.queue.getTasks().size();
    }

    public TaskQueueWrapper(AbstractErrorQueuedTaskQueue queue) {
        this.queue = queue;
    }
}

