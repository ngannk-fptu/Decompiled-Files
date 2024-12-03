/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.NotificationEvent;
import com.atlassian.confluence.mail.notification.Notification;

public abstract class ContentNotificationEvent
extends NotificationEvent {
    private static final long serialVersionUID = -5330350285849170705L;

    public ContentNotificationEvent(Object src, Notification notification) {
        super(src, notification);
        if (!notification.isContentNotification()) {
            throw new IllegalArgumentException("THis event is for content notifications. Found: " + notification);
        }
    }
}

