/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 */
package com.atlassian.confluence.plugins.tasklist.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.tasklist.Task;

@EventName(value="confluence-spaces.tasks.completed")
public class ConfluenceTaskResolveEvent {
    private final long taskId;
    private final long contentId;
    private final String resolvedFrom;

    public ConfluenceTaskResolveEvent(Task task, PageUpdateTrigger updateTrigger) {
        this.taskId = task.getId();
        this.contentId = task.getContentId();
        this.resolvedFrom = updateTrigger != null ? updateTrigger.lowerCase() : PageUpdateTrigger.UNKNOWN.lowerCase();
    }

    public long getTaskId() {
        return this.taskId;
    }

    public long getContentId() {
        return this.contentId;
    }

    public String getResolvedFrom() {
        return this.resolvedFrom;
    }
}

