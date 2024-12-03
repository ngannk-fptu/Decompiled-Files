/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.core.task;

import com.atlassian.core.task.AbstractErrorQueuedTaskQueue;
import com.atlassian.core.task.DefaultTaskQueue;
import com.atlassian.core.task.LocalFifoBuffer;
import com.atlassian.core.task.Task;
import javax.annotation.Nullable;

public class ErrorQueuedTaskQueue
extends AbstractErrorQueuedTaskQueue {
    private static final String DEFAULT_QUEUE_NAME = ErrorQueuedTaskQueue.class.getSimpleName();

    public ErrorQueuedTaskQueue() {
        this(DEFAULT_QUEUE_NAME);
    }

    public ErrorQueuedTaskQueue(@Nullable String queueName) {
        super(new DefaultTaskQueue(queueName), new LocalFifoBuffer<Task>(), queueName);
    }
}

