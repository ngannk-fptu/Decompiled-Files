/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 */
package com.atlassian.confluence.plugins.mentions.conditions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;

public class ShowCommentLinkCondition
extends AbstractNotificationCondition {
    protected boolean shouldDisplay(NotificationContext context) {
        return context.getContent() instanceof AbstractPage && ("page".equals(((AbstractPage)context.getContent()).getType()) || "blogpost".equals(((AbstractPage)context.getContent()).getType()));
    }
}

