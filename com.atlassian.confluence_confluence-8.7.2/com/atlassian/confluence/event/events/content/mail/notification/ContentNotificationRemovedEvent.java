/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.ContentNotificationEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.mail.notification.Notification;

public class ContentNotificationRemovedEvent
extends ContentNotificationEvent
implements Removed {
    private static final long serialVersionUID = -4811879120039493406L;

    public ContentNotificationRemovedEvent(Object src, Notification notification) {
        super(src, notification);
    }
}

