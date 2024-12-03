/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.helper;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EditingHelper {
    public static final String SHARED_DRAFTS_DF_KEY = "site-wide.shared-drafts";
    public static final String SHARED_DRAFTS_DISABLE_DF_KEY = "site-wide.shared-drafts.disable";
    public static final String SYNCHRONY_DF_KEY = "site-wide.synchrony";
    public static final String SYNCHRONY_DISABLE_DF_KEY = "site-wide.synchrony.disable";
    private final DarkFeaturesManager darkFeaturesManager;

    @Autowired
    public EditingHelper(@ComponentImport DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }

    public boolean isSharedDraftsEnabled() {
        return this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled(SHARED_DRAFTS_DF_KEY) && !this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled(SHARED_DRAFTS_DISABLE_DF_KEY);
    }

    public boolean isSynchronyEnabled() {
        return this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled(SYNCHRONY_DF_KEY) && !this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled(SYNCHRONY_DISABLE_DF_KEY);
    }

    public EditingMode getEditingMode() {
        if (this.isSynchronyEnabled() && this.isSharedDraftsEnabled()) {
            return EditingMode.COLLAB_EDITING;
        }
        if (this.isSharedDraftsEnabled() && !this.isSynchronyEnabled()) {
            return EditingMode.LIMITED;
        }
        return EditingMode.LEGACY;
    }

    public static enum EditingMode {
        LEGACY,
        LIMITED,
        COLLAB_EDITING;

    }
}

