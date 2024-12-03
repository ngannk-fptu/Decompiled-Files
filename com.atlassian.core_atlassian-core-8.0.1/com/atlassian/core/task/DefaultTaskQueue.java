/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.core.task;

import com.atlassian.core.task.AbstractTaskQueue;
import com.atlassian.core.task.LocalFifoBuffer;
import com.atlassian.core.task.Task;
import javax.annotation.Nullable;

public class DefaultTaskQueue
extends AbstractTaskQueue {
    private static final String DEFAULT_QUEUE_NAME = DefaultTaskQueue.class.getSimpleName();

    public DefaultTaskQueue() {
        this(DEFAULT_QUEUE_NAME);
    }

    public DefaultTaskQueue(@Nullable String queueName) {
        super(new LocalFifoBuffer<Task>(), queueName);
    }
}

