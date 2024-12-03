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

@EventName(value="mywork.notificationupdated")
public class NotificationUpdatedEvent
extends AbstractNotificationEvent {
    private final Notification oldNotification;

    public NotificationUpdatedEvent(Notification oldNotification, Notification notification) {
        super(notification);
        this.oldNotification = oldNotification;
    }

    public Notification getOldNotification() {
        return this.oldNotification;
    }
}

