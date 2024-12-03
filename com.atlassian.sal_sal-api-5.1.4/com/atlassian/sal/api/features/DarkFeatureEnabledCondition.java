/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.sal.api.features;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.features.ValidFeatureKeyPredicate;
import java.util.Map;

public class DarkFeatureEnabledCondition
implements Condition {
    private static final String FEATURE_KEY_INIT_PARAMETER_NAME = "featureKey";
    private final DarkFeatureManager darkFeatureManager;
    private String featureKey;

    public DarkFeatureEnabledCondition(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    public void init(Map<String, String> params) {
        if (!params.containsKey(FEATURE_KEY_INIT_PARAMETER_NAME)) {
            throw new PluginParseException("Parameter 'featureKey' is mandatory.");
        }
        this.featureKey = ValidFeatureKeyPredicate.checkFeatureKey(params.get(FEATURE_KEY_INIT_PARAMETER_NAME));
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        try {
            return this.darkFeatureManager.isFeatureEnabledForCurrentUser(this.featureKey);
        }
        catch (RuntimeException e) {
            return false;
        }
    }
}

