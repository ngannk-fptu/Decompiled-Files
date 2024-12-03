/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang.ObjectUtils
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.plugin.notifications.config.UserServerManager;
import com.atlassian.plugin.notifications.dispatcher.IndividualRecipientPreferences;
import com.atlassian.sal.api.user.UserKey;
import org.apache.commons.lang.ObjectUtils;

public class UserRecipientPreferences
implements IndividualRecipientPreferences {
    private final UserServerManager userServerManager;
    private final UserNotificationPreferencesManager preferencesManager;

    public UserRecipientPreferences(UserServerManager userServerManager, UserNotificationPreferencesManager preferencesManager) {
        this.userServerManager = userServerManager;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public Iterable<ServerConfiguration> getServers(UserKey userKey) {
        return this.userServerManager.getServers(userKey);
    }

    @Override
    public boolean shouldSend(RoleRecipient recipient, UserKey eventAuthor, ServerConfiguration serverConfig) {
        UserNotificationPreferences preferences = this.preferencesManager.getPreferences(recipient.getUserKey());
        if (preferences != null) {
            if (ObjectUtils.equals((Object)recipient.getUserKey(), (Object)eventAuthor) && !this.shouldSendToSelf(recipient, serverConfig, preferences)) {
                return false;
            }
            return preferences.isNotificationEnabled(serverConfig, recipient.getRole());
        }
        return false;
    }

    private boolean shouldSendToSelf(RoleRecipient recipient, ServerConfiguration serverConfig, UserNotificationPreferences preferences) {
        return preferences.isOwnEventNotificationsEnabled(serverConfig) || recipient.shouldOverrideSendingOwnEventNotifications();
    }
}

