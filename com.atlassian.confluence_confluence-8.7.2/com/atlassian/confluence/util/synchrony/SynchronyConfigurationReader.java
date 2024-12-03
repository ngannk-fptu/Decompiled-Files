/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.synchrony;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;

public class SynchronyConfigurationReader {
    private static final String SHARED_DRAFT_DARK_FEATURE_KEY = "site-wide.shared-drafts";
    private DarkFeaturesManager darkFeaturesManager;

    public SynchronyConfigurationReader(DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }

    @Deprecated
    public boolean isSynchronyEnabled() {
        return this.isSharedDraftsEnabled();
    }

    public boolean isSharedDraftsEnabled() {
        return this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled(SHARED_DRAFT_DARK_FEATURE_KEY);
    }
}

