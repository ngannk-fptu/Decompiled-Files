/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.mywork.event.notification;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.mywork.event.notification.AbstractNotificationEvent;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;

@EventName(value="mywork.statuschanged")
public class NotificationStatusChangedEvent
extends AbstractNotificationEvent {
    private final Task task;
    private final Status oldStatus;

    public NotificationStatusChangedEvent(Notification notification, Task task, Status oldStatus) {
        super(notification);
        this.task = task;
        this.oldStatus = oldStatus;
    }

    public Task getTask() {
        return this.task;
    }

    public Status getOldStatus() {
        return this.oldStatus;
    }
}

