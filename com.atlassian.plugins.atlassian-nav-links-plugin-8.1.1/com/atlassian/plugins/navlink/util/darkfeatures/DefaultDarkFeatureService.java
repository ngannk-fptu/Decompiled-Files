/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.navlink.util.darkfeatures;

import com.atlassian.plugins.navlink.util.darkfeatures.DarkFeatureService;

public class DefaultDarkFeatureService
implements DarkFeatureService {
    private static final String DARKFEATURES_PREFIX = "atlassian.darkfeature.";

    @Override
    public boolean isDarkFeatureEnabledForCurrentUser(String featureKey) {
        Object property = System.getProperties().get(DARKFEATURES_PREFIX + featureKey);
        return property != null && Boolean.parseBoolean(property.toString());
    }
}

