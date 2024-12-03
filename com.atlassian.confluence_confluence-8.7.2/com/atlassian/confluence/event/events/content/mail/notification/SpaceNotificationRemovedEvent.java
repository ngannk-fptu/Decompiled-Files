/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.mail.notification;

import com.atlassian.confluence.event.events.content.mail.notification.SpaceNotificationEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.mail.notification.Notification;

public class SpaceNotificationRemovedEvent
extends SpaceNotificationEvent
implements Removed {
    private static final long serialVersionUID = -8356436751248246737L;

    public SpaceNotificationRemovedEvent(Object src, Notification notification) {
        super(src, notification);
    }
}

