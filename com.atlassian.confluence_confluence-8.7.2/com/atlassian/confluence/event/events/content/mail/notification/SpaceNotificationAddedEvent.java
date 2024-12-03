/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.SpaceNotificationEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.mail.notification.Notification;

public class SpaceNotificationAddedEvent
extends SpaceNotificationEvent
implements Created {
    private static final long serialVersionUID = 5723571046824381666L;

    public SpaceNotificationAddedEvent(Object src, Notification notification) {
        super(src, notification);
    }
}

