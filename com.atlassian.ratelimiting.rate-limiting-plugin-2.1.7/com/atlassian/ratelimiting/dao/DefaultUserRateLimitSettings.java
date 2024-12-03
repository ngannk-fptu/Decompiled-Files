/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.dao;

import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DefaultUserRateLimitSettings
implements UserRateLimitSettings {
    private final int capacity;
    private final int fillRate;
    private final UserKey userKey;
    private final boolean whitelisted;
    private int intervalFrequency;
    private ChronoUnit intervalTimeUnit;

    private DefaultUserRateLimitSettings(Builder builder) {
        this.capacity = builder.capacity;
        this.fillRate = builder.fillRate;
        this.userKey = builder.user;
        this.whitelisted = builder.whitelisted;
        this.intervalFrequency = builder.intervalFrequency;
        this.intervalTimeUnit = builder.intervalTimeUnit;
    }

    @Nonnull
    public static Builder builder(@Nonnull UserKey user) {
        return new Builder(user);
    }

    @Nonnull
    public static Builder builder(@Nonnull UserRateLimitSettings userSettings) {
        return new Builder(userSettings);
    }

    public Builder copy() {
        return new Builder(this);
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getFillRate() {
        return this.fillRate;
    }

    @Override
    public UserKey getUserKey() {
        return this.userKey;
    }

    @Override
    public boolean isWhitelisted() {
        return this.whitelisted;
    }

    @Override
    public int getIntervalFrequency() {
        return this.intervalFrequency;
    }

    @Override
    public ChronoUnit getIntervalTimeUnit() {
        return this.intervalTimeUnit;
    }

    public void setIntervalFrequency(int intervalFrequency) {
        this.intervalFrequency = intervalFrequency;
    }

    public void setIntervalTimeUnit(ChronoUnit intervalTimeUnit) {
        this.intervalTimeUnit = intervalTimeUnit;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DefaultUserRateLimitSettings)) {
            return false;
        }
        DefaultUserRateLimitSettings other = (DefaultUserRateLimitSettings)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getCapacity() != other.getCapacity()) {
            return false;
        }
        if (this.getFillRate() != other.getFillRate()) {
            return false;
        }
        if (this.isWhitelisted() != other.isWhitelisted()) {
            return false;
        }
        if (this.getIntervalFrequency() != other.getIntervalFrequency()) {
            return false;
        }
        UserKey this$userKey = this.getUserKey();
        UserKey other$userKey = other.getUserKey();
        if (this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey)) {
            return false;
        }
        ChronoUnit this$intervalTimeUnit = this.getIntervalTimeUnit();
        ChronoUnit other$intervalTimeUnit = other.getIntervalTimeUnit();
        return !(this$intervalTimeUnit == null ? other$intervalTimeUnit != null : !this$intervalTimeUnit.equals(other$intervalTimeUnit));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DefaultUserRateLimitSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCapacity();
        result = result * 59 + this.getFillRate();
        result = result * 59 + (this.isWhitelisted() ? 79 : 97);
        result = result * 59 + this.getIntervalFrequency();
        UserKey $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        ChronoUnit $intervalTimeUnit = this.getIntervalTimeUnit();
        result = result * 59 + ($intervalTimeUnit == null ? 43 : $intervalTimeUnit.hashCode());
        return result;
    }

    public String toString() {
        return "DefaultUserRateLimitSettings(capacity=" + this.getCapacity() + ", fillRate=" + this.getFillRate() + ", userKey=" + this.getUserKey() + ", whitelisted=" + this.isWhitelisted() + ", intervalFrequency=" + this.getIntervalFrequency() + ", intervalTimeUnit=" + this.getIntervalTimeUnit() + ")";
    }

    public static class Builder {
        private static final int NULL_PLACEHOLDER_VALUE = -1;
        private final UserKey user;
        private int capacity;
        private int fillRate;
        private boolean whitelisted;
        private int intervalFrequency;
        private ChronoUnit intervalTimeUnit;

        private Builder(@Nonnull UserKey user) {
            this.user = user;
            this.capacity = -1;
            this.fillRate = -1;
            this.intervalFrequency = 1;
            this.intervalTimeUnit = ChronoUnit.SECONDS;
        }

        private Builder(@Nonnull UserRateLimitSettings settings) {
            this.capacity = settings.isWhitelisted() ? -1 : settings.getCapacity();
            this.fillRate = settings.isWhitelisted() ? -1 : settings.getFillRate();
            this.user = Objects.requireNonNull(settings.getUserKey(), "settings.user");
            this.whitelisted = settings.isWhitelisted();
            this.intervalFrequency = settings.getIntervalFrequency();
            this.intervalTimeUnit = settings.getIntervalTimeUnit();
        }

        public DefaultUserRateLimitSettings build() {
            Preconditions.checkState((this.whitelisted || this.capacity != -1 && this.fillRate != -1 ? 1 : 0) != 0, (Object)"Either user is 'whitelisted' or valid rate limit settings must be provided");
            return new DefaultUserRateLimitSettings(this);
        }

        public Builder whitelisted() {
            this.capacity = -1;
            this.fillRate = -1;
            this.whitelisted = true;
            this.intervalFrequency = 1;
            this.intervalTimeUnit = ChronoUnit.SECONDS;
            return this;
        }

        public Builder blacklisted() {
            this.capacity = 0;
            this.fillRate = 0;
            this.whitelisted = false;
            this.intervalFrequency = 1;
            this.intervalTimeUnit = ChronoUnit.SECONDS;
            return this;
        }

        public Builder withSettings(@Nonnull TokenBucketSettings settings) {
            Objects.requireNonNull(settings, "settings");
            this.capacity = settings.getCapacity();
            this.fillRate = settings.getFillRate();
            this.whitelisted = false;
            this.intervalFrequency = settings.getIntervalFrequency();
            this.intervalTimeUnit = settings.getIntervalTimeUnit();
            return this;
        }
    }
}

