/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.user.User;

public class UserWatchingSpaceCondition
extends BaseConfluenceCondition {
    private NotificationManager notificationManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        if (context.getCurrentUser() == null || context.getSpace() == null) {
            return false;
        }
        return this.notificationManager.getNotificationByUserAndSpace((User)context.getCurrentUser(), context.getSpace()) != null;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
}

