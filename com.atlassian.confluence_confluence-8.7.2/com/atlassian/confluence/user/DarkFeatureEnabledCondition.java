/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.plugin.PluginParseException;
import java.util.Map;

public class DarkFeatureEnabledCondition
extends BaseConfluenceCondition {
    private DarkFeaturesManager darkFeaturesManager;
    private String darkFeatureKey;

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
        this.darkFeatureKey = params.get("key");
    }

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        return this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(this.darkFeatureKey);
    }

    public void setDarkFeaturesManager(DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }
}

