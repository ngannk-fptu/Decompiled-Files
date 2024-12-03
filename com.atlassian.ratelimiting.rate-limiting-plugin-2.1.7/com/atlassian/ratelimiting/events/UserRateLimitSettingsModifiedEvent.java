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

public class UserRateLimitSettingsModifiedEvent
implements UserRateLimitSettingsEvent {
    private final UserRateLimitSettings newSettings;
    private final UserRateLimitSettings oldSettings;

    @Override
    public UserKey getUserKey() {
        return this.oldSettings.getUserKey();
    }

    public UserRateLimitSettings getNewSettings() {
        return this.newSettings;
    }

    public UserRateLimitSettings getOldSettings() {
        return this.oldSettings;
    }

    public UserRateLimitSettingsModifiedEvent(UserRateLimitSettings newSettings, UserRateLimitSettings oldSettings) {
        this.newSettings = newSettings;
        this.oldSettings = oldSettings;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserRateLimitSettingsModifiedEvent)) {
            return false;
        }
        UserRateLimitSettingsModifiedEvent other = (UserRateLimitSettingsModifiedEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        UserRateLimitSettings this$newSettings = this.getNewSettings();
        UserRateLimitSettings other$newSettings = other.getNewSettings();
        if (this$newSettings == null ? other$newSettings != null : !this$newSettings.equals(other$newSettings)) {
            return false;
        }
        UserRateLimitSettings this$oldSettings = this.getOldSettings();
        UserRateLimitSettings other$oldSettings = other.getOldSettings();
        return !(this$oldSettings == null ? other$oldSettings != null : !this$oldSettings.equals(other$oldSettings));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserRateLimitSettingsModifiedEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        UserRateLimitSettings $newSettings = this.getNewSettings();
        result = result * 59 + ($newSettings == null ? 43 : $newSettings.hashCode());
        UserRateLimitSettings $oldSettings = this.getOldSettings();
        result = result * 59 + ($oldSettings == null ? 43 : $oldSettings.hashCode());
        return result;
    }

    public String toString() {
        return "UserRateLimitSettingsModifiedEvent(newSettings=" + this.getNewSettings() + ", oldSettings=" + this.getOldSettings() + ")";
    }
}

