/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.mail.notification.Notification;

public abstract class NotificationEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -4855297582950687996L;
    protected Notification notification;

    public NotificationEvent(Object src, Notification notification) {
        super(src);
        this.notification = notification;
    }

    public Notification getNotification() {
        return this.notification;
    }
}

