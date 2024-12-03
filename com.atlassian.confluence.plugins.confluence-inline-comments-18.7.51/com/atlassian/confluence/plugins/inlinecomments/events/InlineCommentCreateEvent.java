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

public class InlineCommentCreateEvent
extends InlineCommentEvent
implements NotificationEnabledEvent {
    @Deprecated
    public InlineCommentCreateEvent(Object source, Comment comment) {
        super(source, comment);
    }

    public InlineCommentCreateEvent(Object source, Comment comment, OperationContext<?> context) {
        super(source, comment, context);
    }
}

