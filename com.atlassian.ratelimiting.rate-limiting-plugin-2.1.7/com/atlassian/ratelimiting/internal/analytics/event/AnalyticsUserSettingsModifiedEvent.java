/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.ratelimiting.internal.analytics.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsUserSettingsEvent;

public class AnalyticsUserSettingsModifiedEvent
extends AnalyticsUserSettingsEvent {
    private final int previousCapacity;
    private final int previousFillRate;
    private final int previousIntervalFrequency;
    private final String previousIntervalTimeUnit;
    private final boolean previouslyWhitelisted;
    private final boolean previouslyBlacklisted;

    public AnalyticsUserSettingsModifiedEvent(UserRateLimitSettings oldSettings, UserRateLimitSettings newSettings) {
        super(newSettings);
        this.previousCapacity = oldSettings.getCapacity();
        this.previousFillRate = oldSettings.getFillRate();
        this.previousIntervalFrequency = oldSettings.getIntervalFrequency();
        this.previousIntervalTimeUnit = oldSettings.getIntervalTimeUnit().name();
        this.previouslyWhitelisted = oldSettings.isWhitelisted();
        this.previouslyBlacklisted = oldSettings.isBlacklisted();
    }

    @Override
    @EventName
    public String getAnalyticsEventName() {
        return "rate_limit.user.settings.modified";
    }

    public int getPreviousCapacity() {
        return this.previousCapacity;
    }

    public int getPreviousFillRate() {
        return this.previousFillRate;
    }

    public int getPreviousIntervalFrequency() {
        return this.previousIntervalFrequency;
    }

    public String getPreviousIntervalTimeUnit() {
        return this.previousIntervalTimeUnit;
    }

    public boolean isPreviouslyWhitelisted() {
        return this.previouslyWhitelisted;
    }

    public boolean isPreviouslyBlacklisted() {
        return this.previouslyBlacklisted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnalyticsUserSettingsModifiedEvent)) {
            return false;
        }
        AnalyticsUserSettingsModifiedEvent other = (AnalyticsUserSettingsModifiedEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (this.getPreviousCapacity() != other.getPreviousCapacity()) {
            return false;
        }
        if (this.getPreviousFillRate() != other.getPreviousFillRate()) {
            return false;
        }
        if (this.getPreviousIntervalFrequency() != other.getPreviousIntervalFrequency()) {
            return false;
        }
        if (this.isPreviouslyWhitelisted() != other.isPreviouslyWhitelisted()) {
            return false;
        }
        if (this.isPreviouslyBlacklisted() != other.isPreviouslyBlacklisted()) {
            return false;
        }
        String this$previousIntervalTimeUnit = this.getPreviousIntervalTimeUnit();
        String other$previousIntervalTimeUnit = other.getPreviousIntervalTimeUnit();
        return !(this$previousIntervalTimeUnit == null ? other$previousIntervalTimeUnit != null : !this$previousIntervalTimeUnit.equals(other$previousIntervalTimeUnit));
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof AnalyticsUserSettingsModifiedEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        result = result * 59 + this.getPreviousCapacity();
        result = result * 59 + this.getPreviousFillRate();
        result = result * 59 + this.getPreviousIntervalFrequency();
        result = result * 59 + (this.isPreviouslyWhitelisted() ? 79 : 97);
        result = result * 59 + (this.isPreviouslyBlacklisted() ? 79 : 97);
        String $previousIntervalTimeUnit = this.getPreviousIntervalTimeUnit();
        result = result * 59 + ($previousIntervalTimeUnit == null ? 43 : $previousIntervalTimeUnit.hashCode());
        return result;
    }

    public String toString() {
        return "AnalyticsUserSettingsModifiedEvent(previousCapacity=" + this.getPreviousCapacity() + ", previousFillRate=" + this.getPreviousFillRate() + ", previousIntervalFrequency=" + this.getPreviousIntervalFrequency() + ", previousIntervalTimeUnit=" + this.getPreviousIntervalTimeUnit() + ", previouslyWhitelisted=" + this.isPreviouslyWhitelisted() + ", previouslyBlacklisted=" + this.isPreviouslyBlacklisted() + ")";
    }
}

