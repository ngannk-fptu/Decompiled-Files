/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.NotificationEvent;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.spaces.Space;

public abstract class SpaceNotificationEvent
extends NotificationEvent {
    public SpaceNotificationEvent(Object src, Notification notification) {
        super(src, notification);
        if (!notification.isSpaceNotification()) {
            throw new IllegalArgumentException("Notification represents a page/blogpost watch.");
        }
    }

    public Space getSpace() {
        return this.getNotification().getSpace();
    }
}

