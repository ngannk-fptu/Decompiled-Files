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

@EventName(value="mywork.taskdeleted")
public class TaskDeletedEvent
extends AbstractTaskEvent {
    public TaskDeletedEvent(Task task) {
        super(task);
    }
}

