/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.like;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.user.User;

@AsynchronousPreferred
public class AsyncLikeEvent
extends ConfluenceEvent
implements UserDriven {
    private static final long serialVersionUID = -1047519887239407349L;
    private final User user;
    private final long contentId;

    public AsyncLikeEvent(Object src, User user, long contentId) {
        super(src);
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        this.user = user;
        if (contentId == 0L) {
            throw new IllegalArgumentException("contentId cannot be 0");
        }
        this.contentId = contentId;
    }

    public long getContentId() {
        return this.contentId;
    }

    @Override
    public User getOriginatingUser() {
        return this.user;
    }
}

