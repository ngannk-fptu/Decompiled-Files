/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import java.util.function.BooleanSupplier;

public class DarkFeatureEnabledSupplier
implements BooleanSupplier {
    private final DarkFeaturesManager darkFeaturesManager;
    private final String darkFeatureKey;

    public DarkFeatureEnabledSupplier(DarkFeaturesManager darkFeaturesManager, String darkFeatureKey) {
        this.darkFeaturesManager = darkFeaturesManager;
        this.darkFeatureKey = darkFeatureKey;
    }

    @Override
    public boolean getAsBoolean() {
        return this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(this.darkFeatureKey);
    }
}

