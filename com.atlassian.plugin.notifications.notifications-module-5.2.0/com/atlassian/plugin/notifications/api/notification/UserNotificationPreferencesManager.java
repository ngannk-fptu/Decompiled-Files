/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.api.notification;

import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.sal.api.user.UserKey;

public interface UserNotificationPreferencesManager {
    public UserNotificationPreferences getPreferences(UserKey var1);
}

