/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.like;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.like.AbstractLikeEvent;
import com.atlassian.user.User;

public class LikeCreatedEvent
extends AbstractLikeEvent
implements NotificationEnabledEvent {
    private static final long serialVersionUID = 379636611989172386L;

    public LikeCreatedEvent(Object src, User user, ContentEntityObject content) {
        super(src, user, content);
    }
}

