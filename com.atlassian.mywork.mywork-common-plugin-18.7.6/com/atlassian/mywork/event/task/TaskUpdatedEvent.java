/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.mywork.event.task;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.mywork.event.task.AbstractTaskEvent;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;

@EventName(value="mywork.taskupdated")
public class TaskUpdatedEvent
extends AbstractTaskEvent {
    private final Task oldTask;

    public TaskUpdatedEvent(Task oldTask, Task task) {
        super(task);
        this.oldTask = oldTask;
    }

    public Task getOldTask() {
        return this.oldTask;
    }

    public long getOldTaskId() {
        return this.oldTask.getId();
    }

    public String getOldGlobalId() {
        return this.oldTask.getGlobalId();
    }

    public String getOldApplication() {
        return this.oldTask.getApplication();
    }

    public String getOldEntity() {
        return this.oldTask.getEntity();
    }

    public Status getOldStatus() {
        return this.oldTask.getStatus();
    }
}

