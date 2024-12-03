/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.mywork.event.task;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.mywork.event.task.AbstractTaskEvent;
import com.atlassian.mywork.model.Task;

@EventName(value="mywork.taskmoved")
public class TaskMovedEvent
extends AbstractTaskEvent {
    private final Long targetId;

    public TaskMovedEvent(Task task, Long targetId) {
        super(task);
        this.targetId = targetId;
    }

    public Long getTargetId() {
        return this.targetId;
    }
}

