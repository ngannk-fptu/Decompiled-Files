/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.notifications.spi.salext.UserPreferences
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.impl.sal;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.plugin.notifications.spi.salext.UserPreferences;
import com.atlassian.sal.api.user.UserKey;

public class ConfluenceUserPreferences
implements UserPreferences {
    private final UserKey userKey;
    private final com.atlassian.core.user.preferences.UserPreferences preferences;

    public ConfluenceUserPreferences(UserKey userKey, com.atlassian.core.user.preferences.UserPreferences preferences) {
        this.userKey = userKey;
        this.preferences = preferences;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public String getString(String key) {
        return this.preferences.getString(key);
    }

    public long getLong(String key) {
        return this.preferences.getLong(key);
    }

    public boolean getBoolean(String key) {
        return this.preferences.getBoolean(key);
    }

    public void setString(String key, String newValue) throws RuntimeException {
        try {
            if (newValue == null) {
                this.remove(key);
            } else {
                this.preferences.setString(key, newValue);
            }
        }
        catch (AtlassianCoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLong(String key, long newValue) throws RuntimeException {
        try {
            this.preferences.setLong(key, newValue);
        }
        catch (AtlassianCoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBoolean(String key, boolean newValue) throws RuntimeException {
        try {
            this.preferences.setBoolean(key, newValue);
        }
        catch (AtlassianCoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(String key) throws RuntimeException {
        try {
            this.preferences.remove(key);
        }
        catch (AtlassianCoreException e) {
            throw new RuntimeException(e);
        }
    }
}

