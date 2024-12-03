/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.conditions;

import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.troubleshooting.healthcheck.checks.conditions.FeatureFlagCondition;
import org.springframework.beans.factory.annotation.Autowired;

public class LicenseLimitFeatureFlagCondition
extends FeatureFlagCondition {
    @Autowired
    protected LicenseLimitFeatureFlagCondition(DarkFeatureManager darkFeatureManager) {
        super(darkFeatureManager);
    }

    @Override
    protected String getFeatureFlagName() {
        return "com.atlassian.troubleshooting.healthcheck.confluence.license.limit";
    }
}

