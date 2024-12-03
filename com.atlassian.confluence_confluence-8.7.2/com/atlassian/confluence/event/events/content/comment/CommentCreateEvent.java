/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 */
package com.atlassian.confluence.event.events.content.comment;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.pages.Comment;

public class CommentCreateEvent
extends CommentEvent
implements Created,
NotificationEnabledEvent {
    private static final long serialVersionUID = 6755944289733939009L;

    @Deprecated
    public CommentCreateEvent(Object src, Comment comment) {
        super(src, comment);
    }

    public CommentCreateEvent(Object source, Comment comment, OperationContext<?> context) {
        super(source, comment, context);
    }
}

