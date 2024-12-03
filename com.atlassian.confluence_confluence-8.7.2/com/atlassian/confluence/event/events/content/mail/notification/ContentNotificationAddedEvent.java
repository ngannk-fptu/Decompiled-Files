/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.mail.notification.Notification;

public class ContentNotificationAddedEvent
extends ContentNotificationEvent
implements Created {
    private static final long serialVersionUID = 5495226088233907623L;

    public ContentNotificationAddedEvent(Object src, Notification notification) {
        super(src, notification);
    }
}

