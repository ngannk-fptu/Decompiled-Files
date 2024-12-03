/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.like;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.like.AbstractLikeEvent;
import com.atlassian.user.User;

public class LikeRemovedEvent
extends AbstractLikeEvent {
    private static final long serialVersionUID = -3570712349298098409L;

    public LikeRemovedEvent(Object src, User user, ContentEntityObject contentEntity) {
        super(src, user, contentEntity);
    }
}

