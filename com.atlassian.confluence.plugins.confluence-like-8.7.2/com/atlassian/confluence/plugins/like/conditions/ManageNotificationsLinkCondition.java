/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 */
package com.atlassian.confluence.plugins.like.conditions;

import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;

public class ManageNotificationsLinkCondition
extends AbstractNotificationCondition {
    public static final String MANAGE_LIKE_NOTIFICATIONS = "manageLikeNotifications";

    protected boolean shouldDisplay(NotificationContext context) {
        return Boolean.TRUE.equals(context.get(MANAGE_LIKE_NOTIFICATIONS));
    }
}

