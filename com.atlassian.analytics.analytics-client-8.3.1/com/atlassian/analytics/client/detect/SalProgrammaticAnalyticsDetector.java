/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.analytics.client.detect;

import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;
import com.atlassian.sal.api.features.DarkFeatureManager;

public class SalProgrammaticAnalyticsDetector
implements ProgrammaticAnalyticsDetector {
    private static final String DARK_FEATURE_PROGRAMMATIC_ANALYTICS = "com.atlassian.grow.programmatic.analytics";
    private final DarkFeatureManager darkFeatureManager;

    public SalProgrammaticAnalyticsDetector(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    public boolean isEnabled() {
        return this.darkFeatureManager.isFeatureEnabledForCurrentUser(DARK_FEATURE_PROGRAMMATIC_ANALYTICS);
    }
}

