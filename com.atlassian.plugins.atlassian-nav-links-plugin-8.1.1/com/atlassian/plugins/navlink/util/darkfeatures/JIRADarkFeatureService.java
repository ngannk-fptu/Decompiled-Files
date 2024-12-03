/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.config.FeatureManager
 */
package com.atlassian.plugins.navlink.util.darkfeatures;

import com.atlassian.jira.config.FeatureManager;
import com.atlassian.plugins.navlink.util.darkfeatures.DarkFeatureService;

public class JIRADarkFeatureService
implements DarkFeatureService {
    private final FeatureManager featureManager;

    public JIRADarkFeatureService(FeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    @Override
    public boolean isDarkFeatureEnabledForCurrentUser(String featureKey) {
        return this.featureManager.isEnabled(featureKey);
    }
}

