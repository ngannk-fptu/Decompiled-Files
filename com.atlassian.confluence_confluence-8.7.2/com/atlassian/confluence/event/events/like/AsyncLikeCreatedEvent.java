/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.like;

import com.atlassian.confluence.event.events.like.AsyncLikeEvent;
import com.atlassian.user.User;

public class AsyncLikeCreatedEvent
extends AsyncLikeEvent {
    private static final long serialVersionUID = 4247000600541405860L;

    public AsyncLikeCreatedEvent(Object src, User user, long contentId) {
        super(src, user, contentId);
    }
}

