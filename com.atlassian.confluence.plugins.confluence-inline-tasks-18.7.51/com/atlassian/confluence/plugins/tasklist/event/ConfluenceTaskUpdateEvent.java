/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.user.User
 *  com.google.common.base.Objects
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.event.AbstractConfluenceTaskEvent;
import com.atlassian.user.User;
import com.google.common.base.Objects;

public class ConfluenceTaskUpdateEvent
extends AbstractConfluenceTaskEvent {
    private final Task oldTask;
    private final PageUpdateTrigger updateTrigger;

    public ConfluenceTaskUpdateEvent(Object src, User user, Task task, Task oldTask, PageUpdateTrigger updateTrigger) {
        super(src, user, task);
        this.oldTask = oldTask;
        this.updateTrigger = updateTrigger;
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

    public boolean hasTitleChanged() {
        return !Objects.equal((Object)this.oldTask.getTitle(), (Object)this.task.getTitle());
    }

    public PageUpdateTrigger getUpdateTrigger() {
        return this.updateTrigger;
    }
}

