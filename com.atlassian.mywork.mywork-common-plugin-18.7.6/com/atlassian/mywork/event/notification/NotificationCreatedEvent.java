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

@EventName(value="mywork.notificationcreated")
public class NotificationCreatedEvent
extends AbstractNotificationEvent {
    public NotificationCreatedEvent(Notification notification) {
        super(notification);
    }
}

