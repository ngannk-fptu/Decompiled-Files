/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.comment;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.pages.Comment;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CommentUpdateEvent
extends CommentEvent
implements Updated,
NotificationEnabledEvent {
    private static final long serialVersionUID = 433335935133736565L;
    private final Comment originalComment;

    @Deprecated
    public CommentUpdateEvent(Object src, Comment comment) {
        this(src, comment, (Comment)null);
    }

    @Deprecated
    public CommentUpdateEvent(Object src, Comment comment, Comment originalComment) {
        super(src, comment);
        this.originalComment = originalComment;
    }

    public CommentUpdateEvent(Object source, Comment comment, @Nullable Comment originalComment, @Nullable OperationContext<?> operationContext) {
        super(source, comment, operationContext);
        this.originalComment = originalComment;
    }

    public Comment getOriginalComment() {
        return this.originalComment;
    }
}

