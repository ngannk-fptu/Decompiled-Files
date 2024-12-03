/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.internal.settings;

import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.dao.UserRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.settings.RateLimitSettingsUtil;
import com.atlassian.ratelimiting.license.LicenseChecker;
import com.atlassian.sal.api.user.UserKey;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class RateLimitLightweightAccessService {
    private final SystemRateLimitingSettingsProvider systemRateLimitingSettingsProvider;
    private final UserRateLimitingSettingsProvider userRateLimitingSettingsProvider;
    private final LicenseChecker licenseChecker;
    private final RateLimitingFeatureFlagService rateLimitingFeatureFlagService;
    private final boolean jmxEnabled;

    public RateLimitLightweightAccessService(SystemRateLimitingSettingsProvider systemRateLimitingSettingsProvider, UserRateLimitingSettingsProvider userRateLimitingSettingsProvider, LicenseChecker licenseChecker, RateLimitingFeatureFlagService rateLimitingFeatureFlagService, boolean jmxEnabled) {
        this.systemRateLimitingSettingsProvider = systemRateLimitingSettingsProvider;
        this.userRateLimitingSettingsProvider = userRateLimitingSettingsProvider;
        this.licenseChecker = licenseChecker;
        this.rateLimitingFeatureFlagService = rateLimitingFeatureFlagService;
        this.jmxEnabled = jmxEnabled;
    }

    @Nonnull
    public Optional<UserRateLimitSettings> getUserSettings(@Nonnull UserKey userKey) {
        return this.userRateLimitingSettingsProvider.get(Objects.requireNonNull(userKey, "userKey"));
    }

    public TokenBucketSettings getSystemDefaultBucketSettings() {
        return this.systemRateLimitingSettingsProvider.getSystemSettings().getBucketSettings();
    }

    public SystemRateLimitingSettings getSystemSettings() {
        return this.systemRateLimitingSettingsProvider.getSystemSettings();
    }

    public boolean isJmxEnabled() {
        return this.jmxEnabled;
    }

    public RateLimitingMode getRateLimitingMode() {
        return RateLimitSettingsUtil.determineRateLimitingMode(this.systemRateLimitingSettingsProvider.getSystemSettings().getMode(), this.licenseChecker, this.rateLimitingFeatureFlagService);
    }
}

