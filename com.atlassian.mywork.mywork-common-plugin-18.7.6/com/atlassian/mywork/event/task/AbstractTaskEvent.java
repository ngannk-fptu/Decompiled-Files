/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.event.task;

import com.atlassian.mywork.event.MyWorkEvent;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;

public abstract class AbstractTaskEvent
extends MyWorkEvent {
    private final Task task;

    public AbstractTaskEvent(Task task) {
        super(task.getUser());
        this.task = task;
    }

    public Task getTask() {
        return this.task;
    }

    public long getTaskId() {
        return this.task.getId();
    }

    public String getGlobalId() {
        return this.task.getGlobalId();
    }

    public String getApplication() {
        return this.task.getApplication();
    }

    public String getEntity() {
        return this.task.getEntity();
    }

    public Status getStatus() {
        return this.task.getStatus();
    }
}

