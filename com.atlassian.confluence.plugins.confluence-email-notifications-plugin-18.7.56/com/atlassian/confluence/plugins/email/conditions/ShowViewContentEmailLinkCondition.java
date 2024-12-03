/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentEvent
 *  com.atlassian.confluence.event.events.content.page.PageMoveEvent
 *  com.atlassian.confluence.event.events.like.LikeEvent
 *  com.atlassian.confluence.event.events.types.Removed
 *  com.atlassian.confluence.event.events.types.Trashed
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.plugins.email.conditions;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.event.events.like.LikeEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;
import com.atlassian.event.Event;

public class ShowViewContentEmailLinkCondition
extends AbstractNotificationCondition {
    protected boolean shouldDisplay(NotificationContext context) {
        Event event = context.getEvent();
        if (event instanceof Removed || event instanceof Trashed || event instanceof LikeEvent || event instanceof AttachmentEvent || event instanceof PageMoveEvent) {
            return false;
        }
        ConfluenceEntityObject entity = context.getContent();
        return entity instanceof Page || entity instanceof BlogPost || entity instanceof Comment;
    }
}

