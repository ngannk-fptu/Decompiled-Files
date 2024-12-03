/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.troubleshooting.healthcheck.checks.conditions;

import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import java.util.Objects;

public abstract class FeatureFlagCondition
implements SupportHealthCheckCondition {
    private final DarkFeatureManager darkFeatureManager;

    protected FeatureFlagCondition(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
    }

    protected abstract String getFeatureFlagName();

    @Override
    public boolean shouldDisplay() {
        return this.darkFeatureManager.isEnabledForAllUsers(this.getFeatureFlagName()).orElse(false);
    }
}

