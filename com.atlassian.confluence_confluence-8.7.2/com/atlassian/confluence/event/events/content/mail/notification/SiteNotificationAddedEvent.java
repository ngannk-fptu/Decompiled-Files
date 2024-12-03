/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.SiteNotificationEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.mail.notification.Notification;

public class SiteNotificationAddedEvent
extends SiteNotificationEvent
implements Created {
    private static final long serialVersionUID = 1914196891697136374L;

    public SiteNotificationAddedEvent(Object src, Notification notification) {
        super(src, notification);
    }
}

