/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.notifications.spi.salext.UserPreferences
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.userpreferences;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.notifications.spi.salext.UserPreferences;
import org.apache.commons.lang3.StringUtils;

public class DefaultUserNotificationPreferences
implements UserNotificationPreferences {
    private static final String NOTIFICATION_PREFERENCE_PREFIX = "notifications.user.pref.";
    private static final String OWN_NOTIFICATION_PREFERENCE_PREFIX = "notifications.user.notify.own.pref.";
    private static final String NOTIFICATION_PREFERENCE_MAPPING_PREFIX = "notifications.user.pref.server.mapping.";
    private final UserPreferences preferences;

    public DefaultUserNotificationPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean isNotificationEnabled(ServerConfiguration server, UserRole role) {
        String preferenceKey = this.getPreferenceKey(server, role);
        String value = this.preferences.getString(preferenceKey);
        if (StringUtils.isBlank((CharSequence)value)) {
            return server.isEnabledForAllUsers();
        }
        return Boolean.parseBoolean(value);
    }

    public void setNotificationEnabled(ServerConfiguration server, UserRole role, boolean isEnabled) {
        this.preferences.setString(this.getPreferenceKey(server, role), Boolean.toString(isEnabled));
    }

    public String getServerMapping(ServerConfiguration server) {
        String mapping = this.preferences.getString(this.getServerKey(server.getId()));
        if (StringUtils.isBlank((CharSequence)mapping)) {
            return server.getDefaultUserIDTemplate();
        }
        return mapping;
    }

    public void setServerMapping(int serverId, String userMapping) {
        this.preferences.setString(this.getServerKey(serverId), userMapping);
    }

    private String getServerKey(int serverId) {
        return NOTIFICATION_PREFERENCE_MAPPING_PREFIX + Integer.toString(serverId);
    }

    private String getPreferenceKey(ServerConfiguration server, UserRole role) {
        return NOTIFICATION_PREFERENCE_PREFIX + server.getId() + "." + role.getID();
    }

    public boolean isOwnEventNotificationsEnabled(ServerConfiguration server) {
        String value = this.preferences.getString(OWN_NOTIFICATION_PREFERENCE_PREFIX + server.getId());
        if (StringUtils.isBlank((CharSequence)value)) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    public void setOwnEventNotificationsEnabled(ServerConfiguration server, boolean isEnabled) {
        this.preferences.setString(OWN_NOTIFICATION_PREFERENCE_PREFIX + server.getId(), Boolean.toString(isEnabled));
    }
}

