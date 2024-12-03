/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.business.insights.core.frontend.conditions;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.Map;
import java.util.Optional;

public class UiEnabledCondition
implements Condition {
    @VisibleForTesting
    static final String UI_DISABLED_FLAG = "data.pipeline.ui.disabled";
    private final DarkFeatureManager darkFeatureManager;

    public UiEnabledCondition(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    public void init(Map<String, String> map) {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        Optional isUiDisabled = this.darkFeatureManager.isEnabledForCurrentUser(UI_DISABLED_FLAG);
        return isUiDisabled.map(uiDisabled -> uiDisabled == false).orElse(true);
    }
}

