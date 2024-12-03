/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.events;

import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsEvent;
import com.atlassian.sal.api.user.UserKey;

public class UserRateLimitSettingsCreatedEvent
implements UserRateLimitSettingsEvent {
    private final UserRateLimitSettings createdSettings;

    @Override
    public UserKey getUserKey() {
        return this.createdSettings.getUserKey();
    }

    public UserRateLimitSettings getCreatedSettings() {
        return this.createdSettings;
    }

    public UserRateLimitSettingsCreatedEvent(UserRateLimitSettings createdSettings) {
        this.createdSettings = createdSettings;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserRateLimitSettingsCreatedEvent)) {
            return false;
        }
        UserRateLimitSettingsCreatedEvent other = (UserRateLimitSettingsCreatedEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        UserRateLimitSettings this$createdSettings = this.getCreatedSettings();
        UserRateLimitSettings other$createdSettings = other.getCreatedSettings();
        return !(this$createdSettings == null ? other$createdSettings != null : !this$createdSettings.equals(other$createdSettings));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserRateLimitSettingsCreatedEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        UserRateLimitSettings $createdSettings = this.getCreatedSettings();
        result = result * 59 + ($createdSettings == null ? 43 : $createdSettings.hashCode());
        return result;
    }

    public String toString() {
        return "UserRateLimitSettingsCreatedEvent(createdSettings=" + this.getCreatedSettings() + ")";
    }
}

