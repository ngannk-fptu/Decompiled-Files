/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.event.notification;

import com.atlassian.mywork.event.MyWorkEvent;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.Status;

public abstract class AbstractNotificationEvent
extends MyWorkEvent {
    private final Notification notification;

    public AbstractNotificationEvent(Notification notification) {
        super(notification.getUser());
        this.notification = notification;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public long getNotificationId() {
        return this.notification.getId();
    }

    public String getGlobalId() {
        return this.notification.getGlobalId();
    }

    public String getApplication() {
        return this.notification.getApplication();
    }

    public String getEntity() {
        return this.notification.getEntity();
    }

    public String getAction() {
        return this.notification.getAction();
    }

    public Status getStatus() {
        return this.notification.getStatus();
    }
}

