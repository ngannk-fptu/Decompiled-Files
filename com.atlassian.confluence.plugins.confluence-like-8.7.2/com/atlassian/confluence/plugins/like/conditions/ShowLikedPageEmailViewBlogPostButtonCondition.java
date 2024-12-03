/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.like.LikeCreatedEvent
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 */
package com.atlassian.confluence.plugins.like.conditions;

import com.atlassian.confluence.event.events.like.LikeCreatedEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;

public class ShowLikedPageEmailViewBlogPostButtonCondition
extends AbstractNotificationCondition {
    protected boolean shouldDisplay(NotificationContext context) {
        return context.getEvent() instanceof LikeCreatedEvent && context.getContent() instanceof AbstractPage && !context.getRecipient().getName().equals(context.getContent().getCreatorName()) && "blogpost".equals(((AbstractPage)context.getContent()).getType());
    }
}

