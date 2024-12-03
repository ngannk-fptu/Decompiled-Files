/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.plugin.notifications.spi.salext.UserPreferencesManager
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.notifications.userpreferences;

import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.plugin.notifications.spi.salext.UserPreferencesManager;
import com.atlassian.plugin.notifications.userpreferences.DefaultUserNotificationPreferences;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;

public class DefaultUserNotificationPreferencesManager
implements UserNotificationPreferencesManager {
    private final UserPreferencesManager userPreferencesManager;

    public DefaultUserNotificationPreferencesManager(UserPreferencesManager userPreferencesManager) {
        this.userPreferencesManager = userPreferencesManager;
    }

    public UserNotificationPreferences getPreferences(UserKey userKey) {
        Preconditions.checkNotNull((Object)userKey);
        return new DefaultUserNotificationPreferences(this.userPreferencesManager.getPreferences(userKey));
    }
}

