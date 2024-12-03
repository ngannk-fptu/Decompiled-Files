/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.email.conditions;

import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.user.User;
import java.util.Map;

public class StopWatchingSpaceEmailLinkCondition
extends AbstractNotificationCondition {
    public static final String PAGE_MOVED_NOTIFICATION = "page-moved-notification";
    private Notification.WatchType watchType;
    private NotificationManager notificationManager;

    public StopWatchingSpaceEmailLinkCondition(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.watchType = Notification.WatchType.valueOf((String)params.get("watchType"));
    }

    protected boolean shouldDisplay(NotificationContext context) {
        ModuleCompleteKey moduleCompleteKey;
        String moduleKey;
        if (this.watchType != context.getWatchType()) {
            return false;
        }
        Object notificationKey = context.get("notificationKey");
        if (notificationKey instanceof ModuleCompleteKey && PAGE_MOVED_NOTIFICATION.equals(moduleKey = (moduleCompleteKey = (ModuleCompleteKey)notificationKey).getModuleKey())) {
            User user = (User)context.get("user");
            Space space = (Space)context.get("space");
            return this.notificationManager.isUserWatchingPageOrSpace(user, space, null);
        }
        return true;
    }
}

