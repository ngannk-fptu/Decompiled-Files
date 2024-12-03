/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.confluence.core.OperationContext
 *  com.atlassian.confluence.pages.Comment
 */
package com.atlassian.confluence.plugins.inlinecomments.events;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.inlinecomments.events.InlineCommentEvent;

public class InlineCommentResolveEvent
extends InlineCommentEvent
implements NotificationEnabledEvent {
    @Deprecated
    public InlineCommentResolveEvent(Object source, Comment comment) {
        super(source, comment);
    }

    public InlineCommentResolveEvent(Object source, Comment comment, OperationContext<?> context) {
        super(source, comment, context);
    }

    @Override
    public String getUserKey() {
        if (this.comment.getProperties().getStringProperty("resolved") != null) {
            return this.comment.getProperties().getStringProperty("resolved-user");
        }
        return this.comment.getProperties().getStringProperty("status-lastmodifier");
    }
}

