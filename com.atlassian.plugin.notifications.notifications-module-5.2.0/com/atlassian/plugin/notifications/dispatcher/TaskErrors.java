/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.notifications.api.queue.TaskStatus;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.util.DiscardingDeque;
import com.google.common.collect.Lists;
import java.util.Deque;
import java.util.List;

public class TaskErrors {
    private String taskId;
    private TaskStatus status;
    private Deque<NotificationError> errors = new DiscardingDeque<NotificationError>(20);

    public TaskErrors(String taskId, TaskStatus status) {
        this.taskId = taskId;
        this.status = status;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void addError(NotificationError error) {
        this.errors.offerFirst(error);
    }

    public String getTaskId() {
        return this.taskId;
    }

    public List<NotificationError> getErrors() {
        return Lists.newArrayList(this.errors);
    }
}

