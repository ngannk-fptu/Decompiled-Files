/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.sal.api.user.UserKey
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.confluence.notifications.impl.spi.ConfluenceAwareUserNotificationPreferences;
import com.atlassian.confluence.notifications.impl.spi.StaticServerPreferenceKeyProvider;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.user.UserKey;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConfluenceAwareUserNotificationPreferencesManager
implements UserNotificationPreferencesManager {
    private final UserAccessor userAccessor;
    private final UserNotificationPreferencesManager delegate;
    private StaticServerPreferenceKeyProvider notificationKeyProvider;

    public ConfluenceAwareUserNotificationPreferencesManager(UserAccessor userAccessor, @Qualifier(value="defaultNotificationPreferenceManager") UserNotificationPreferencesManager delegate, StaticServerPreferenceKeyProvider notificationKeyProvider) {
        this.userAccessor = userAccessor;
        this.delegate = delegate;
        this.notificationKeyProvider = notificationKeyProvider;
    }

    public UserNotificationPreferences getPreferences(UserKey userKey) {
        if (userKey == null) {
            return null;
        }
        ConfluenceUser user = this.userAccessor.getExistingUserByKey(userKey);
        if (user == null) {
            return null;
        }
        UserNotificationPreferences pref = this.delegate.getPreferences(userKey);
        UserPreferences confluenceUserPreferences = new UserPreferences(this.userAccessor.getPropertySet(user));
        return new ConfluenceAwareUserNotificationPreferences(pref, confluenceUserPreferences, this.notificationKeyProvider);
    }
}

