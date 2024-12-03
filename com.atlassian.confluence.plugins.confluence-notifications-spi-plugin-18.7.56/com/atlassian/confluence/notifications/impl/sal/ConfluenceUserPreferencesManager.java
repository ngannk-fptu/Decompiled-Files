/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.notifications.spi.salext.UserPreferences
 *  com.atlassian.plugin.notifications.spi.salext.UserPreferencesManager
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.notifications.impl.sal;

import com.atlassian.confluence.notifications.impl.sal.ConfluenceUserPreferences;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.notifications.spi.salext.UserPreferences;
import com.atlassian.plugin.notifications.spi.salext.UserPreferencesManager;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;

public class ConfluenceUserPreferencesManager
implements UserPreferencesManager {
    private final UserAccessor userAccessor;

    public ConfluenceUserPreferencesManager(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public UserPreferences getPreferences(UserKey userKey) {
        ConfluenceUser user = this.userAccessor.getExistingUserByKey(userKey);
        if (user == null) {
            return null;
        }
        com.atlassian.core.user.preferences.UserPreferences preferences = this.userAccessor.getUserPreferences((User)user);
        return new ConfluenceUserPreferences(userKey, preferences);
    }

    public String getNotificationPreferencesUrl() {
        return "/notifications/usersettings.action";
    }
}

