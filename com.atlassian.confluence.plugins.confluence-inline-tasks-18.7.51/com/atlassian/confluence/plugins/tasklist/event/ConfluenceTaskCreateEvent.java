/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.Analytics
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.analytics.api.annotations.Analytics;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.event.AbstractConfluenceTaskEvent;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

@Analytics(value="inlinetask.created")
public class ConfluenceTaskCreateEvent
extends AbstractConfluenceTaskEvent {
    public ConfluenceTaskCreateEvent(Object src, User user, Task task) {
        super(src, user, task);
    }

    public long getTaskId() {
        return this.task.getId();
    }

    public long getContentId() {
        return this.task.getContentId();
    }

    public boolean isAssigned() {
        return StringUtils.isNotEmpty((CharSequence)this.task.getAssignee());
    }

    public int getTitleLength() {
        return StringUtils.isEmpty((CharSequence)this.task.getTitle()) ? 0 : this.task.getTitle().length();
    }
}

