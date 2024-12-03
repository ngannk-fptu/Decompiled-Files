/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.dmz;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TokenBucketSettings {
    private int capacity;
    private int fillRate;
    private int intervalFrequency;
    private ChronoUnit intervalTimeUnit;

    public static TokenBucketSettings unlimited() {
        return TokenBucketSettings.builder().capacity(-1).fillRate(-1).build();
    }

    public static TokenBucketSettings prohibited() {
        return TokenBucketSettings.builder().capacity(0).fillRate(0).build();
    }

    public boolean isBlacklisted() {
        return 0 == this.getCapacity() && 0 == this.getFillRate();
    }

    public boolean isWhitelisted() {
        return -1 == this.getCapacity() && -1 == this.getFillRate();
    }

    public boolean isCustomSettings() {
        return !this.isBlacklisted() && !this.isWhitelisted();
    }

    public Duration getIntervalDuration() {
        return Duration.of(this.intervalFrequency, this.intervalTimeUnit);
    }

    private static int $default$intervalFrequency() {
        return 1;
    }

    private static ChronoUnit $default$intervalTimeUnit() {
        return ChronoUnit.SECONDS;
    }

    public static TokenBucketSettingsBuilder builder() {
        return new TokenBucketSettingsBuilder();
    }

    public TokenBucketSettingsBuilder toBuilder() {
        return new TokenBucketSettingsBuilder().capacity(this.capacity).fillRate(this.fillRate).intervalFrequency(this.intervalFrequency).intervalTimeUnit(this.intervalTimeUnit);
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

    public ChronoUnit getIntervalTimeUnit() {
        return this.intervalTimeUnit;
    }

    public String toString() {
        return "TokenBucketSettings(capacity=" + this.getCapacity() + ", fillRate=" + this.getFillRate() + ", intervalFrequency=" + this.getIntervalFrequency() + ", intervalTimeUnit=" + this.getIntervalTimeUnit() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenBucketSettings)) {
            return false;
        }
        TokenBucketSettings other = (TokenBucketSettings)o;
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
        ChronoUnit this$intervalTimeUnit = this.getIntervalTimeUnit();
        ChronoUnit other$intervalTimeUnit = other.getIntervalTimeUnit();
        return !(this$intervalTimeUnit == null ? other$intervalTimeUnit != null : !this$intervalTimeUnit.equals(other$intervalTimeUnit));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TokenBucketSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCapacity();
        result = result * 59 + this.getFillRate();
        result = result * 59 + this.getIntervalFrequency();
        ChronoUnit $intervalTimeUnit = this.getIntervalTimeUnit();
        result = result * 59 + ($intervalTimeUnit == null ? 43 : $intervalTimeUnit.hashCode());
        return result;
    }

    public TokenBucketSettings(int capacity, int fillRate, int intervalFrequency, ChronoUnit intervalTimeUnit) {
        this.capacity = capacity;
        this.fillRate = fillRate;
        this.intervalFrequency = intervalFrequency;
        this.intervalTimeUnit = intervalTimeUnit;
    }

    public static class TokenBucketSettingsBuilder {
        private int capacity;
        private int fillRate;
        private boolean intervalFrequency$set;
        private int intervalFrequency$value;
        private boolean intervalTimeUnit$set;
        private ChronoUnit intervalTimeUnit$value;

        TokenBucketSettingsBuilder() {
        }

        public TokenBucketSettingsBuilder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public TokenBucketSettingsBuilder fillRate(int fillRate) {
            this.fillRate = fillRate;
            return this;
        }

        public TokenBucketSettingsBuilder intervalFrequency(int intervalFrequency) {
            this.intervalFrequency$value = intervalFrequency;
            this.intervalFrequency$set = true;
            return this;
        }

        public TokenBucketSettingsBuilder intervalTimeUnit(ChronoUnit intervalTimeUnit) {
            this.intervalTimeUnit$value = intervalTimeUnit;
            this.intervalTimeUnit$set = true;
            return this;
        }

        public TokenBucketSettings build() {
            int intervalFrequency$value = this.intervalFrequency$value;
            if (!this.intervalFrequency$set) {
                intervalFrequency$value = TokenBucketSettings.$default$intervalFrequency();
            }
            ChronoUnit intervalTimeUnit$value = this.intervalTimeUnit$value;
            if (!this.intervalTimeUnit$set) {
                intervalTimeUnit$value = TokenBucketSettings.$default$intervalTimeUnit();
            }
            return new TokenBucketSettings(this.capacity, this.fillRate, intervalFrequency$value, intervalTimeUnit$value);
        }

        public String toString() {
            return "TokenBucketSettings.TokenBucketSettingsBuilder(capacity=" + this.capacity + ", fillRate=" + this.fillRate + ", intervalFrequency$value=" + this.intervalFrequency$value + ", intervalTimeUnit$value=" + this.intervalTimeUnit$value + ")";
        }
    }
}

