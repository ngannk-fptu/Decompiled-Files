/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.ratelimiting.internal.featureflag;

import com.atlassian.ratelimiting.internal.featureflag.DefaultRateLimitingFeatureFlagService;
import com.atlassian.sal.api.features.DarkFeatureManager;

public class SalRateLimitingFeatureFlagService
extends DefaultRateLimitingFeatureFlagService {
    private final DarkFeatureManager darkFeatureManager;

    public SalRateLimitingFeatureFlagService(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    public boolean getDryRunEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers("com.atlassian.ratelimiting.dry.run").orElse(false);
    }
}

