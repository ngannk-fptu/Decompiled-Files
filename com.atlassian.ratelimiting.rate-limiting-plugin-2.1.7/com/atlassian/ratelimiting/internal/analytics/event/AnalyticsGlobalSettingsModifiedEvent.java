/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.ratelimiting.internal.analytics.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.internal.analytics.event.RateLimitingAnalyticsEvent;

public class AnalyticsGlobalSettingsModifiedEvent
implements RateLimitingAnalyticsEvent {
    private final int previousCapacity;
    private final int previousFillRate;
    private final int previousIntervalFrequency;
    private final String previousIntervalTimeUnit;
    private final boolean previouslyAllowedAll;
    private final boolean previouslyBlockedAll;
    private final int capacity;
    private final int fillRate;
    private final int intervalFrequency;
    private final String intervalTimeUnit;
    private final boolean allowAll;
    private final boolean blockAll;

    public AnalyticsGlobalSettingsModifiedEvent(TokenBucketSettings previousTokenBucketSettings, TokenBucketSettings newTokenBucketSettings) {
        this.previousCapacity = previousTokenBucketSettings.getCapacity();
        this.previousFillRate = previousTokenBucketSettings.getFillRate();
        this.previousIntervalFrequency = previousTokenBucketSettings.getIntervalFrequency();
        this.previousIntervalTimeUnit = previousTokenBucketSettings.getIntervalTimeUnit().toString();
        this.previouslyAllowedAll = previousTokenBucketSettings.isWhitelisted();
        this.previouslyBlockedAll = previousTokenBucketSettings.isBlacklisted();
        this.capacity = newTokenBucketSettings.getCapacity();
        this.fillRate = newTokenBucketSettings.getFillRate();
        this.intervalFrequency = newTokenBucketSettings.getIntervalFrequency();
        this.intervalTimeUnit = newTokenBucketSettings.getIntervalTimeUnit().toString();
        this.allowAll = newTokenBucketSettings.isWhitelisted();
        this.blockAll = newTokenBucketSettings.isBlacklisted();
    }

    @Override
    @EventName
    public String getAnalyticsEventName() {
        return "rate_limit.global.settings.modified";
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

    public boolean isPreviouslyAllowedAll() {
        return this.previouslyAllowedAll;
    }

    public boolean isPreviouslyBlockedAll() {
        return this.previouslyBlockedAll;
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

    public boolean isAllowAll() {
        return this.allowAll;
    }

    public boolean isBlockAll() {
        return this.blockAll;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnalyticsGlobalSettingsModifiedEvent)) {
            return false;
        }
        AnalyticsGlobalSettingsModifiedEvent other = (AnalyticsGlobalSettingsModifiedEvent)o;
        if (!other.canEqual(this)) {
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
        if (this.isPreviouslyAllowedAll() != other.isPreviouslyAllowedAll()) {
            return false;
        }
        if (this.isPreviouslyBlockedAll() != other.isPreviouslyBlockedAll()) {
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
        if (this.isAllowAll() != other.isAllowAll()) {
            return false;
        }
        if (this.isBlockAll() != other.isBlockAll()) {
            return false;
        }
        String this$previousIntervalTimeUnit = this.getPreviousIntervalTimeUnit();
        String other$previousIntervalTimeUnit = other.getPreviousIntervalTimeUnit();
        if (this$previousIntervalTimeUnit == null ? other$previousIntervalTimeUnit != null : !this$previousIntervalTimeUnit.equals(other$previousIntervalTimeUnit)) {
            return false;
        }
        String this$intervalTimeUnit = this.getIntervalTimeUnit();
        String other$intervalTimeUnit = other.getIntervalTimeUnit();
        return !(this$intervalTimeUnit == null ? other$intervalTimeUnit != null : !this$intervalTimeUnit.equals(other$intervalTimeUnit));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AnalyticsGlobalSettingsModifiedEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPreviousCapacity();
        result = result * 59 + this.getPreviousFillRate();
        result = result * 59 + this.getPreviousIntervalFrequency();
        result = result * 59 + (this.isPreviouslyAllowedAll() ? 79 : 97);
        result = result * 59 + (this.isPreviouslyBlockedAll() ? 79 : 97);
        result = result * 59 + this.getCapacity();
        result = result * 59 + this.getFillRate();
        result = result * 59 + this.getIntervalFrequency();
        result = result * 59 + (this.isAllowAll() ? 79 : 97);
        result = result * 59 + (this.isBlockAll() ? 79 : 97);
        String $previousIntervalTimeUnit = this.getPreviousIntervalTimeUnit();
        result = result * 59 + ($previousIntervalTimeUnit == null ? 43 : $previousIntervalTimeUnit.hashCode());
        String $intervalTimeUnit = this.getIntervalTimeUnit();
        result = result * 59 + ($intervalTimeUnit == null ? 43 : $intervalTimeUnit.hashCode());
        return result;
    }

    public String toString() {
        return "AnalyticsGlobalSettingsModifiedEvent(previousCapacity=" + this.getPreviousCapacity() + ", previousFillRate=" + this.getPreviousFillRate() + ", previousIntervalFrequency=" + this.getPreviousIntervalFrequency() + ", previousIntervalTimeUnit=" + this.getPreviousIntervalTimeUnit() + ", previouslyAllowedAll=" + this.isPreviouslyAllowedAll() + ", previouslyBlockedAll=" + this.isPreviouslyBlockedAll() + ", capacity=" + this.getCapacity() + ", fillRate=" + this.getFillRate() + ", intervalFrequency=" + this.getIntervalFrequency() + ", intervalTimeUnit=" + this.getIntervalTimeUnit() + ", allowAll=" + this.isAllowAll() + ", blockAll=" + this.isBlockAll() + ")";
    }
}

