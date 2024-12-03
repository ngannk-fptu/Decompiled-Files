/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.OperationContext
 *  com.atlassian.confluence.event.events.content.comment.CommentEvent
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.inlinecomments.events;

import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.annotation.Nullable;

public abstract class InlineCommentEvent
extends CommentEvent {
    @Deprecated
    public InlineCommentEvent(Object src, Comment comment) {
        super(src, comment);
    }

    public InlineCommentEvent(Object source, Comment comment, OperationContext<?> context) {
        super(source, comment, context);
    }

    @Nullable
    public String getUserKey() {
        ConfluenceUser user = this.comment.getCreator();
        return user != null ? user.getKey().getStringValue() : null;
    }
}

