/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.notifications.impl.analytics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="notifications.user.settings.modified")
public class UserNotificationPreferenceChangeEvent {
    private final String medium;
    private final String role;
    private final boolean enabled;
    private final boolean defaults;

    public UserNotificationPreferenceChangeEvent(String medium, String role, boolean enabled, boolean defaults) {
        this.medium = medium;
        this.role = role;
        this.enabled = enabled;
        this.defaults = defaults;
    }

    public String getMedium() {
        return this.medium;
    }

    public String getRole() {
        return this.role;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isDefaults() {
        return this.defaults;
    }
}

