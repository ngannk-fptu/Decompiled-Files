/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentEvent
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.event.events.content.page.PageMoveEvent
 *  com.atlassian.confluence.event.events.content.page.async.PageEvent
 *  com.atlassian.confluence.event.events.like.LikeEvent
 *  com.atlassian.confluence.event.events.types.Removed
 *  com.atlassian.confluence.event.events.types.Trashed
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.plugins.like.conditions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.event.events.like.LikeEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;
import com.atlassian.event.Event;

public class ShowEmailLikeLinkCondition
extends AbstractNotificationCondition {
    private LikeManager likeManager;

    public ShowEmailLikeLinkCondition(LikeManager likeManager) {
        this.likeManager = likeManager;
    }

    protected boolean shouldDisplay(NotificationContext context) {
        Event event = context.getEvent();
        if (event instanceof Removed || event instanceof Trashed) {
            return false;
        }
        if (event instanceof PageMoveEvent || !(event instanceof PageEvent) && !(event instanceof com.atlassian.confluence.event.events.content.page.async.PageEvent) && !(event instanceof BlogPostEvent) && !(event instanceof CommentEvent) && !(event instanceof LikeEvent)) {
            return false;
        }
        if (context.getRecipient().equals(context.get("author"))) {
            return false;
        }
        return !this.likeManager.hasLike((ContentEntityObject)context.getContent(), context.getRecipient());
    }
}

