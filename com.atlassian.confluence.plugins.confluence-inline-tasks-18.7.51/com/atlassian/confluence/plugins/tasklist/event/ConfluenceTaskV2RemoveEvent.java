/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.event.AbstractConfluenceTaskEvent;
import com.atlassian.user.User;

@EventName(value="confluence-spaces.tasks.removed")
public class ConfluenceTaskV2RemoveEvent
extends AbstractConfluenceTaskEvent {
    public ConfluenceTaskV2RemoveEvent(Object src, User user, Task task) {
        super(src, user, task);
    }

    public String getAssigneeUsername() {
        return this.task.getAssignee();
    }

    public Boolean getCompleted() {
        return this.task.getStatus() == TaskStatus.CHECKED;
    }
}

