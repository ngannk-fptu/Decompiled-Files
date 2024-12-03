/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.plugins.navlink.util.darkfeatures;

import com.atlassian.plugins.navlink.util.darkfeatures.DarkFeatureService;
import com.atlassian.sal.api.features.DarkFeatureManager;

public class SalDarkFeatureService
implements DarkFeatureService {
    private final DarkFeatureManager darkFeaturesManager;

    public SalDarkFeatureService(DarkFeatureManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }

    @Override
    public boolean isDarkFeatureEnabledForCurrentUser(String featureKey) {
        return this.darkFeaturesManager.getFeaturesEnabledForCurrentUser().isFeatureEnabled(featureKey);
    }
}

