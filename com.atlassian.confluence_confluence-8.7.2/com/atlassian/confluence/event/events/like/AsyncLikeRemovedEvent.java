/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.like;

import com.atlassian.confluence.event.events.like.AsyncLikeEvent;
import com.atlassian.user.User;

public class AsyncLikeRemovedEvent
extends AsyncLikeEvent {
    private static final long serialVersionUID = 2525956089397455851L;

    public AsyncLikeRemovedEvent(Object src, User user, long contentId) {
        super(src, user, contentId);
    }
}

