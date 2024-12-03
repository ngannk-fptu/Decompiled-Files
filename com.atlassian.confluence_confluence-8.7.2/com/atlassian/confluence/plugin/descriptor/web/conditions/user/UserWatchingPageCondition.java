/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;

public class UserWatchingPageCondition
extends BaseConfluenceCondition {
    private NotificationManager notificationManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        if (context.getCurrentUser() == null || context.getPage() == null) {
            return false;
        }
        return this.notificationManager.isWatchingContent(context.getCurrentUser(), context.getPage());
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
}

