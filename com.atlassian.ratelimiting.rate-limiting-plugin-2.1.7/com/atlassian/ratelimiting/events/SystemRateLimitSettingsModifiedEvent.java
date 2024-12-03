/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.events;

import com.atlassian.ratelimiting.dmz.TokenBucketSettings;

public class SystemRateLimitSettingsModifiedEvent {
    private final TokenBucketSettings newSettings;
    private final TokenBucketSettings oldSettings;

    public TokenBucketSettings getNewSettings() {
        return this.newSettings;
    }

    public TokenBucketSettings getOldSettings() {
        return this.oldSettings;
    }

    public SystemRateLimitSettingsModifiedEvent(TokenBucketSettings newSettings, TokenBucketSettings oldSettings) {
        this.newSettings = newSettings;
        this.oldSettings = oldSettings;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SystemRateLimitSettingsModifiedEvent)) {
            return false;
        }
        SystemRateLimitSettingsModifiedEvent other = (SystemRateLimitSettingsModifiedEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        TokenBucketSettings this$newSettings = this.getNewSettings();
        TokenBucketSettings other$newSettings = other.getNewSettings();
        if (this$newSettings == null ? other$newSettings != null : !((Object)this$newSettings).equals(other$newSettings)) {
            return false;
        }
        TokenBucketSettings this$oldSettings = this.getOldSettings();
        TokenBucketSettings other$oldSettings = other.getOldSettings();
        return !(this$oldSettings == null ? other$oldSettings != null : !((Object)this$oldSettings).equals(other$oldSettings));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SystemRateLimitSettingsModifiedEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        TokenBucketSettings $newSettings = this.getNewSettings();
        result = result * 59 + ($newSettings == null ? 43 : ((Object)$newSettings).hashCode());
        TokenBucketSettings $oldSettings = this.getOldSettings();
        result = result * 59 + ($oldSettings == null ? 43 : ((Object)$oldSettings).hashCode());
        return result;
    }

    public String toString() {
        return "SystemRateLimitSettingsModifiedEvent(newSettings=" + this.getNewSettings() + ", oldSettings=" + this.getOldSettings() + ")";
    }
}

