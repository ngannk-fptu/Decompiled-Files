/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.upm.core.permission.Permission;

public enum UpmSettings {
    PAC_DISABLED("pacDisabled", Permission.MANAGE_ON_PREMISE_SETTINGS, "upm.pac.disable", true, true),
    REQUESTS_DISABLED("requestsDisabled", Permission.MANAGE_ON_PREMISE_SETTINGS, "upm.plugin.requests.disable", true, true),
    EMAIL_DISABLED("emailDisabled", Permission.MANAGE_ON_PREMISE_SETTINGS, "upm.email.notifications.disable", false, true),
    AUTO_UPDATE_ENABLED("autoUpdateEnabled", Permission.MANAGE_ON_PREMISE_SETTINGS, "upm.auto.update.enable", false, false);

    private String key;
    private Permission permission;
    private String sysPropertyKey;
    private boolean requiresRefresh;
    private boolean defaultCheckedValue;

    private UpmSettings(String key, Permission permission, String sysPropertyKey, boolean requiresRefresh, boolean defaultCheckedValue) {
        this.key = key;
        this.permission = permission;
        this.sysPropertyKey = sysPropertyKey;
        this.requiresRefresh = requiresRefresh;
        this.defaultCheckedValue = defaultCheckedValue;
    }

    public String getKey() {
        return this.key;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public String getSysPropertyKey() {
        return this.sysPropertyKey;
    }

    public boolean isRequiresRefresh() {
        return this.requiresRefresh;
    }

    public boolean getDefaultCheckedValue() {
        return this.defaultCheckedValue;
    }

    public static UpmSettings withKey(String key) {
        for (UpmSettings s : UpmSettings.values()) {
            if (!s.getKey().equals(key)) continue;
            return s;
        }
        throw new IllegalArgumentException("Invalid settings key");
    }
}

