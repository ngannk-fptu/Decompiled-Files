/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.UserSettings;
import com.atlassian.upm.UserSettingsStore;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import java.util.Objects;

public class PluginSettingsUserSettingsStore
implements UserSettingsStore {
    public static final String SETTINGS_PREFIX = "com.atlassian.upm.UserSettingsStore:";
    private final PluginSettingsFactory pluginSettingsFactory;

    public PluginSettingsUserSettingsStore(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
    }

    @Override
    public boolean getBoolean(UserKey userKey, UserSettings setting) {
        Object value = this.getPluginSettings().get(this.getSettingsKey(userKey, setting));
        if (value instanceof String) {
            return Boolean.parseBoolean((String)value);
        }
        return setting.isDefaultValueTrue();
    }

    @Override
    public void setBoolean(UserKey userKey, UserSettings setting, boolean value) {
        this.getPluginSettings().put(this.getSettingsKey(userKey, setting), (Object)String.valueOf(value));
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), SETTINGS_PREFIX);
    }

    private String getSettingsKey(UserKey userKey, UserSettings setting) {
        return setting.getStorageKey() + ":" + userKey.getStringValue();
    }
}

