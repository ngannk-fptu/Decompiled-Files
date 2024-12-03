/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.content.comment;

import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.user.User;

public class CommentRemoveEvent
extends CommentEvent
implements Removed,
UserDriven {
    private static final long serialVersionUID = -4934404515216674750L;
    private final User remover;

    public CommentRemoveEvent(Object src, Comment comment, User remover) {
        super(src, comment);
        this.remover = remover;
    }

    @Override
    public User getOriginatingUser() {
        return this.remover;
    }
}

