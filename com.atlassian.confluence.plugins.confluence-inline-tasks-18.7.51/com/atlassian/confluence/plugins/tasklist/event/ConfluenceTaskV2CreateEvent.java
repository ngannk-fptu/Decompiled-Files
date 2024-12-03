/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.event.AbstractConfluenceTaskEvent;
import com.atlassian.user.User;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

@EventName(value="confluence-spaces.tasks.created")
public class ConfluenceTaskV2CreateEvent
extends AbstractConfluenceTaskEvent {
    public ConfluenceTaskV2CreateEvent(Object src, User user, Task task) {
        super(src, user, task);
    }

    public String getAssigneeUsername() {
        return this.task.getAssignee();
    }

    public Date getDueDate() {
        return this.task.getDueDate();
    }

    public boolean isAssigned() {
        return StringUtils.isNotEmpty((CharSequence)this.task.getAssignee());
    }

    public long getTaskId() {
        return this.task.getId();
    }

    public long getContentId() {
        return this.task.getContentId();
    }
}

