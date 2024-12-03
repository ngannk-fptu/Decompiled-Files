/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Throwables
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.confluence.notifications.impl.spi.StaticServerPreferenceKeyProvider;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceAwareUserNotificationPreferences
implements UserNotificationPreferences {
    @VisibleForTesting
    static final String CONFLUENCE_HIPCHAT_MEDIUM_KEY = "hipchat";
    private final UserNotificationPreferences delegate;
    private final UserPreferences confluenceUserPreferences;
    private final StaticServerPreferenceKeyProvider notificationKeyProvider;

    public ConfluenceAwareUserNotificationPreferences(UserNotificationPreferences delegate, UserPreferences confluenceUserPreferences, StaticServerPreferenceKeyProvider notificationKeyProvider) {
        this.delegate = delegate;
        this.confluenceUserPreferences = confluenceUserPreferences;
        this.notificationKeyProvider = notificationKeyProvider;
    }

    public String getServerMapping(ServerConfiguration serverConfiguration) {
        return this.delegate.getServerMapping(serverConfiguration);
    }

    public boolean isNotificationEnabled(ServerConfiguration server, UserRole role) {
        if (CONFLUENCE_HIPCHAT_MEDIUM_KEY.equals(server.getNotificationMedium().getKey())) {
            return true;
        }
        if (!server.isConfigurable()) {
            String key = this.notificationKeyProvider.getPreferenceKey(server, role);
            String enabledValue = this.confluenceUserPreferences.getString(key);
            if (StringUtils.isBlank((CharSequence)enabledValue)) {
                return server.isEnabledForAllUsers();
            }
            return Boolean.valueOf(enabledValue);
        }
        return this.delegate.isNotificationEnabled(server, role);
    }

    public boolean isOwnEventNotificationsEnabled(ServerConfiguration server) {
        if (server.isConfigurable()) {
            return this.delegate.isOwnEventNotificationsEnabled(server);
        }
        return this.confluenceUserPreferences.getBoolean("confluence.prefs.notify.for.my.own.actions");
    }

    public void setNotificationEnabled(ServerConfiguration server, UserRole role, boolean isEnabled) {
        if (server.isConfigurable()) {
            this.delegate.setNotificationEnabled(server, role, isEnabled);
        } else {
            try {
                String preferenceKey = this.notificationKeyProvider.getPreferenceKey(server, role);
                this.confluenceUserPreferences.setString(preferenceKey, Boolean.toString(isEnabled));
            }
            catch (AtlassianCoreException e) {
                Throwables.propagate((Throwable)e);
            }
        }
    }

    public void setOwnEventNotificationsEnabled(ServerConfiguration server, boolean isEnabled) {
        if (server.isConfigurable()) {
            this.delegate.setOwnEventNotificationsEnabled(server, isEnabled);
        }
    }

    public void setServerMapping(int serverId, String userMapping) {
        this.delegate.setServerMapping(serverId, userMapping);
    }
}

