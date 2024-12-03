/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.UnknownFeatureException;
import java.util.Set;

@Deprecated
public interface FeatureService {
    public Set<String> getRegisteredFeatures();

    default public void verifyFeature(String featureKey) throws UnknownFeatureException {
    }

    default public void verifyFeatures(Set<String> featureKeys) throws UnknownFeatureException {
        for (String featureKey : featureKeys) {
            this.verifyFeature(featureKey);
        }
    }
}

