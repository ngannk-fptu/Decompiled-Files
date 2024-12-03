/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.spi.salext;

import com.atlassian.plugin.notifications.spi.salext.UserPreferences;
import com.atlassian.sal.api.user.UserKey;

public interface UserPreferencesManager {
    public UserPreferences getPreferences(UserKey var1);

    public String getNotificationPreferencesUrl();
}

