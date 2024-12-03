/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.salext.refapp;

import com.atlassian.plugin.notifications.spi.salext.UserPreferences;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.user.UserKey;
import org.apache.commons.lang3.StringUtils;

public class RefappUserPreferences
implements UserPreferences {
    private final PluginSettings preferences;
    private final UserKey userKey;

    public RefappUserPreferences(PluginSettings preferences, UserKey userKey) {
        this.preferences = preferences;
        this.userKey = userKey;
    }

    @Override
    public UserKey getUserKey() {
        return this.userKey;
    }

    @Override
    public String getString(String key) {
        return (String)this.preferences.get(key);
    }

    @Override
    public long getLong(String key) {
        String value = this.getString(key);
        if (StringUtils.isNumeric((CharSequence)value)) {
            return Long.parseLong(value);
        }
        return 0L;
    }

    @Override
    public boolean getBoolean(String key) {
        return "true".equals(this.getString(key));
    }

    @Override
    public void setString(String key, String newValue) throws RuntimeException {
        if (newValue == null) {
            this.remove(key);
        } else {
            this.preferences.put(key, (Object)newValue);
        }
    }

    @Override
    public void setLong(String key, long newValue) throws RuntimeException {
        this.setString(key, String.valueOf(newValue));
    }

    @Override
    public void setBoolean(String key, boolean newValue) throws RuntimeException {
        this.setString(key, newValue ? "true" : "false");
    }

    @Override
    public void remove(String key) throws RuntimeException {
        this.preferences.remove(key);
    }
}

