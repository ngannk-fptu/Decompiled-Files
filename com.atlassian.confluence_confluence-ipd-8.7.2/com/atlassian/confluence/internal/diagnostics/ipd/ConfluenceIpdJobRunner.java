/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.DefaultIpdJobRunner
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.confluence.internal.diagnostics.ipd;

import com.atlassian.diagnostics.internal.ipd.DefaultIpdJobRunner;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.Objects;

public class ConfluenceIpdJobRunner
extends DefaultIpdJobRunner {
    private final DarkFeatureManager darkFeatureManager;

    public ConfluenceIpdJobRunner(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
    }

    public boolean isIpdFeatureFlagEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers("confluence.in.product.diagnostics.deny").orElse(false) == false;
    }

    public boolean isWipIpdFeatureFlagEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers("confluence.in.product.diagnostics.wip").orElse(false);
    }
}

