/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.ratelimiting.dmz;

import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.sal.api.user.UserProfile;

public class UserRateLimitSettingsSearchResult {
    private final UserProfile userProfile;
    private final UserRateLimitSettings userRateLimitSettings;

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserRateLimitSettingsSearchResult)) {
            return false;
        }
        UserRateLimitSettingsSearchResult other = (UserRateLimitSettingsSearchResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        UserProfile this$userProfile = this.getUserProfile();
        UserProfile other$userProfile = other.getUserProfile();
        if (this$userProfile == null ? other$userProfile != null : !this$userProfile.equals(other$userProfile)) {
            return false;
        }
        UserRateLimitSettings this$userRateLimitSettings = this.getUserRateLimitSettings();
        UserRateLimitSettings other$userRateLimitSettings = other.getUserRateLimitSettings();
        return !(this$userRateLimitSettings == null ? other$userRateLimitSettings != null : !this$userRateLimitSettings.equals(other$userRateLimitSettings));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserRateLimitSettingsSearchResult;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        UserProfile $userProfile = this.getUserProfile();
        result = result * 59 + ($userProfile == null ? 43 : $userProfile.hashCode());
        UserRateLimitSettings $userRateLimitSettings = this.getUserRateLimitSettings();
        result = result * 59 + ($userRateLimitSettings == null ? 43 : $userRateLimitSettings.hashCode());
        return result;
    }

    public String toString() {
        return "UserRateLimitSettingsSearchResult(userProfile=" + this.getUserProfile() + ", userRateLimitSettings=" + this.getUserRateLimitSettings() + ")";
    }

    public UserRateLimitSettingsSearchResult(UserProfile userProfile, UserRateLimitSettings userRateLimitSettings) {
        this.userProfile = userProfile;
        this.userRateLimitSettings = userRateLimitSettings;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public UserRateLimitSettings getUserRateLimitSettings() {
        return this.userRateLimitSettings;
    }
}

