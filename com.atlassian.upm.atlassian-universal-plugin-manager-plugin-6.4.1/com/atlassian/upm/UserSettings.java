/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.upm.api.util.Option;

public enum UserSettings {
    DISABLE_EMAIL("emailDisabled", false, false),
    ACCEPTED_MARKETPLACE_EULA("acceptedMpacEula", true, false);

    private String key;
    private boolean allowedForSysadmin;
    private boolean defaultValueTrue;

    private UserSettings(String key, boolean allowedForSysadmin, boolean defaultValueTrue) {
        this.key = key;
        this.allowedForSysadmin = allowedForSysadmin;
        this.defaultValueTrue = defaultValueTrue;
    }

    public String getKey() {
        return this.key;
    }

    public String getStorageKey() {
        return this.key;
    }

    public boolean isAllowedForSysadmin() {
        return this.allowedForSysadmin;
    }

    public boolean isDefaultValueTrue() {
        return this.defaultValueTrue;
    }

    public static Option<UserSettings> withKey(String key) {
        for (UserSettings s : UserSettings.values()) {
            if (!s.getKey().equals(key)) continue;
            return Option.some(s);
        }
        return Option.none();
    }
}

