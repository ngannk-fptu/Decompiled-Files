/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.rest.api.RestApplicationUser;
import com.atlassian.ratelimiting.rest.api.RestTokenBucketSettings;
import com.atlassian.sal.api.user.UserProfile;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class RestUserRateLimitSettings {
    private RestApplicationUser user;
    private boolean whitelisted;
    private boolean blacklisted;
    private RestTokenBucketSettings settings;

    public RestUserRateLimitSettings(UserRateLimitSettings userRateLimitSettings, UserProfile userProfile) {
        this.user = new RestApplicationUser(userProfile);
        this.populateUserSettings(userRateLimitSettings);
    }

    private void populateUserSettings(UserRateLimitSettings userRateLimitSettings) {
        if (userRateLimitSettings.isWhitelisted()) {
            this.whitelisted = true;
        } else if (userRateLimitSettings.isBlacklisted()) {
            this.blacklisted = true;
        } else {
            this.settings = userRateLimitSettings.getSettings().map(RestTokenBucketSettings::new).orElse(null);
        }
    }

    public boolean hasSettings() {
        return this.settings != null;
    }

    public RestApplicationUser getUser() {
        return this.user;
    }

    public boolean isWhitelisted() {
        return this.whitelisted;
    }

    public boolean isBlacklisted() {
        return this.blacklisted;
    }

    public RestTokenBucketSettings getSettings() {
        return this.settings;
    }

    public void setUser(RestApplicationUser user) {
        this.user = user;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public void setSettings(RestTokenBucketSettings settings) {
        this.settings = settings;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestUserRateLimitSettings)) {
            return false;
        }
        RestUserRateLimitSettings other = (RestUserRateLimitSettings)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isWhitelisted() != other.isWhitelisted()) {
            return false;
        }
        if (this.isBlacklisted() != other.isBlacklisted()) {
            return false;
        }
        RestApplicationUser this$user = this.getUser();
        RestApplicationUser other$user = other.getUser();
        if (this$user == null ? other$user != null : !((Object)this$user).equals(other$user)) {
            return false;
        }
        RestTokenBucketSettings this$settings = this.getSettings();
        RestTokenBucketSettings other$settings = other.getSettings();
        return !(this$settings == null ? other$settings != null : !((Object)this$settings).equals(other$settings));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestUserRateLimitSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isWhitelisted() ? 79 : 97);
        result = result * 59 + (this.isBlacklisted() ? 79 : 97);
        RestApplicationUser $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : ((Object)$user).hashCode());
        RestTokenBucketSettings $settings = this.getSettings();
        result = result * 59 + ($settings == null ? 43 : ((Object)$settings).hashCode());
        return result;
    }

    public String toString() {
        return "RestUserRateLimitSettings(user=" + this.getUser() + ", whitelisted=" + this.isWhitelisted() + ", blacklisted=" + this.isBlacklisted() + ", settings=" + this.getSettings() + ")";
    }

    public RestUserRateLimitSettings(RestApplicationUser user, boolean whitelisted, boolean blacklisted, RestTokenBucketSettings settings) {
        this.user = user;
        this.whitelisted = whitelisted;
        this.blacklisted = blacklisted;
        this.settings = settings;
    }

    public RestUserRateLimitSettings() {
    }
}

