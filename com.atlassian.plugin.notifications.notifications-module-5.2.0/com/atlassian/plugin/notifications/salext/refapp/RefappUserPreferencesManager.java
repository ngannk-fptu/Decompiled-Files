/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.salext.refapp;

import com.atlassian.plugin.notifications.salext.refapp.RefappUserPreferences;
import com.atlassian.plugin.notifications.spi.salext.UserPreferences;
import com.atlassian.plugin.notifications.spi.salext.UserPreferencesManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;

public class RefappUserPreferencesManager
implements UserPreferencesManager {
    private final PluginSettingsFactory pluginSettingsFactory;

    public RefappUserPreferencesManager(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public UserPreferences getPreferences(UserKey userKey) {
        if (userKey != null) {
            PluginSettings globalSettings = this.pluginSettingsFactory.createGlobalSettings();
            return new RefappUserPreferences(globalSettings, userKey);
        }
        return null;
    }

    @Override
    public String getNotificationPreferencesUrl() {
        return "/plugins/servlet/usersettings";
    }
}

