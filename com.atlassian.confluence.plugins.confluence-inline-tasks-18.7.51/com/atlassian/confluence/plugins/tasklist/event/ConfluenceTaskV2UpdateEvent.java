/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.user.User
 *  com.google.common.base.Objects
 *  org.joda.time.DateTime
 *  org.joda.time.Days
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.event.AbstractConfluenceTaskEvent;
import com.atlassian.user.User;
import com.google.common.base.Objects;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;

@EventName(value="confluence-spaces.tasks.changed")
public class ConfluenceTaskV2UpdateEvent
extends AbstractConfluenceTaskEvent {
    private final Task oldTask;

    public ConfluenceTaskV2UpdateEvent(Object src, User user, Task task, Task oldTask) {
        super(src, user, task);
        this.oldTask = oldTask;
    }

    public Task getOldTask() {
        return this.oldTask;
    }

    public boolean hasStatusChanged() {
        return this.oldTask.getStatus() != this.task.getStatus();
    }

    public boolean hasAssigneeChanged() {
        return !Objects.equal((Object)this.oldTask.getAssignee(), (Object)this.task.getAssignee());
    }

    public boolean hasBodyChanged() {
        return !Objects.equal((Object)this.oldTask.getBody(), (Object)this.task.getBody());
    }

    public boolean getTaskCompleted() {
        return this.task.getStatus() == TaskStatus.CHECKED;
    }

    public int getDeltaDueDate() {
        return Days.daysBetween((ReadableInstant)new DateTime((Object)this.oldTask.getDueDate()), (ReadableInstant)new DateTime((Object)this.task.getDueDate())).getDays();
    }

    public String getAssigneeUsername() {
        return this.task.getAssignee();
    }
}

