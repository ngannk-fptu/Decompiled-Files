/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.Edited
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 */
package com.atlassian.confluence.plugins.email.conditions;

import com.atlassian.confluence.event.events.content.Edited;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;

public class ShowViewChangesEmailLinkCondition
extends AbstractNotificationCondition {
    protected boolean shouldDisplay(NotificationContext context) {
        if (!(context.getEvent() instanceof Edited)) {
            return false;
        }
        return context.getContent() instanceof AbstractPage;
    }
}

