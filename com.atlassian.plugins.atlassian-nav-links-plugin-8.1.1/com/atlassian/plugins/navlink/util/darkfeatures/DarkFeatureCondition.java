/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.plugins.navlink.util.darkfeatures;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugins.navlink.util.darkfeatures.DarkFeatureService;
import java.util.Map;

public class DarkFeatureCondition
implements Condition {
    private final DarkFeatureService darkFeatureService;
    private String featureKey;

    public DarkFeatureCondition(DarkFeatureService darkFeatureService) {
        this.darkFeatureService = darkFeatureService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.featureKey = params.get("key");
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return this.featureKey != null && this.darkFeatureService.isDarkFeatureEnabledForCurrentUser(this.featureKey);
    }
}

