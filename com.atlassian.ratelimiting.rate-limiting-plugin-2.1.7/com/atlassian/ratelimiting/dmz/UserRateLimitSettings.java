/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.dmz;

import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.sal.api.user.UserKey;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface UserRateLimitSettings {
    default public Optional<TokenBucketSettings> getSettings() {
        return this.isWhitelisted() ? Optional.empty() : Optional.of(this.getBucketSettings());
    }

    @Nonnull
    public UserKey getUserKey();

    public boolean isWhitelisted();

    @Nonnull
    public int getCapacity();

    @Nonnull
    public int getFillRate();

    public int getIntervalFrequency();

    @Nonnull
    public ChronoUnit getIntervalTimeUnit();

    default public boolean isBlacklisted() {
        return 0 == this.getCapacity() && 0 == this.getFillRate();
    }

    @Nonnull
    default public TokenBucketSettings getBucketSettings() {
        return new TokenBucketSettings(this.getCapacity(), this.getFillRate(), this.getIntervalFrequency(), this.getIntervalTimeUnit());
    }
}

