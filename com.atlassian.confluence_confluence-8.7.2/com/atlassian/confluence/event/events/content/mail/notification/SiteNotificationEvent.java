/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.NotificationEvent;
import com.atlassian.confluence.mail.notification.Notification;

public abstract class SiteNotificationEvent
extends NotificationEvent {
    private static final long serialVersionUID = -7319870362352256421L;

    public SiteNotificationEvent(Object src, Notification notification) {
        super(src, notification);
    }
}

