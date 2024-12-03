/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.internal.analytics.event;

import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.internal.analytics.event.RateLimitingAnalyticsEvent;

abstract class AnalyticsUserSettingsEvent
implements RateLimitingAnalyticsEvent {
    static final String USER_SETTINGS_EVENT_NAME_PREFIX = "user.settings";
    private final int capacity;
    private final int fillRate;
    private final int intervalFrequency;
    private final String intervalTimeUnit;
    private final boolean whitelisted;
    private final boolean blacklisted;

    AnalyticsUserSettingsEvent(UserRateLimitSettings settings) {
        this.capacity = settings.getCapacity();
        this.fillRate = settings.getFillRate();
        this.intervalFrequency = settings.getIntervalFrequency();
        this.intervalTimeUnit = settings.getIntervalTimeUnit().name();
        this.whitelisted = settings.isWhitelisted();
        this.blacklisted = settings.isBlacklisted();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getFillRate() {
        return this.fillRate;
    }

    public int getIntervalFrequency() {
        return this.intervalFrequency;
    }

    public String getIntervalTimeUnit() {
        return this.intervalTimeUnit;
    }

    public boolean isWhitelisted() {
        return this.whitelisted;
    }

    public boolean isBlacklisted() {
        return this.blacklisted;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnalyticsUserSettingsEvent)) {
            return false;
        }
        AnalyticsUserSettingsEvent other = (AnalyticsUserSettingsEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getCapacity() != other.getCapacity()) {
            return false;
        }
        if (this.getFillRate() != other.getFillRate()) {
            return false;
        }
        if (this.getIntervalFrequency() != other.getIntervalFrequency()) {
            return false;
        }
        if (this.isWhitelisted() != other.isWhitelisted()) {
            return false;
        }
        if (this.isBlacklisted() != other.isBlacklisted()) {
            return false;
        }
        String this$intervalTimeUnit = this.getIntervalTimeUnit();
        String other$intervalTimeUnit = other.getIntervalTimeUnit();
        return !(this$intervalTimeUnit == null ? other$intervalTimeUnit != null : !this$intervalTimeUnit.equals(other$intervalTimeUnit));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AnalyticsUserSettingsEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCapacity();
        result = result * 59 + this.getFillRate();
        result = result * 59 + this.getIntervalFrequency();
        result = result * 59 + (this.isWhitelisted() ? 79 : 97);
        result = result * 59 + (this.isBlacklisted() ? 79 : 97);
        String $intervalTimeUnit = this.getIntervalTimeUnit();
        result = result * 59 + ($intervalTimeUnit == null ? 43 : $intervalTimeUnit.hashCode());
        return result;
    }
}

