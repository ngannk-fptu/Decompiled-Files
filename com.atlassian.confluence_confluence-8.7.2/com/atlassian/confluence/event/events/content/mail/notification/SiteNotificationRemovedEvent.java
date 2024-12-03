/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.SiteNotificationEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.mail.notification.Notification;

public class SiteNotificationRemovedEvent
extends SiteNotificationEvent
implements Removed {
    private static final long serialVersionUID = 237489949238095451L;

    public SiteNotificationRemovedEvent(Object src, Notification notification) {
        super(src, notification);
    }
}

