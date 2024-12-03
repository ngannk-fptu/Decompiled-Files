/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.internal.settings;

import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.license.LicenseChecker;

class RateLimitSettingsUtil {
    private RateLimitSettingsUtil() {
    }

    static RateLimitingMode determineRateLimitingMode(RateLimitingMode mode, LicenseChecker licenseChecker, RateLimitingFeatureFlagService rateLimitingFeatureFlagService) {
        if (!licenseChecker.isDataCenterLicensed()) {
            return RateLimitingMode.OFF;
        }
        if (rateLimitingFeatureFlagService.isDryRunEnabled()) {
            return RateLimitingMode.DRY_RUN;
        }
        return mode;
    }
}

