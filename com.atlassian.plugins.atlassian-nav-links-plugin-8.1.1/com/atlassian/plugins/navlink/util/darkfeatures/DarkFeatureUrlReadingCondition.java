/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.IllegalPluginStateException
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.util.validation.ValidationException
 *  com.atlassian.plugin.webresource.condition.SimpleUrlReadingCondition
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.plugins.navlink.util.darkfeatures;

import com.atlassian.plugin.IllegalPluginStateException;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.util.validation.ValidationException;
import com.atlassian.plugin.webresource.condition.SimpleUrlReadingCondition;
import com.atlassian.plugins.navlink.util.darkfeatures.DarkFeatureService;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DarkFeatureUrlReadingCondition
extends SimpleUrlReadingCondition {
    private final DarkFeatureService darkFeatureService;
    private String featureKey;

    public DarkFeatureUrlReadingCondition(DarkFeatureService darkFeatureService) {
        this.darkFeatureService = darkFeatureService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.featureKey = params.get("key");
        if (this.featureKey == null) {
            throw new ValidationException("Failed to initialise condition", (List)ImmutableList.of((Object)"A dark feature 'key' must be provided"));
        }
    }

    protected boolean isConditionTrue() {
        return this.featureKey != null && this.darkFeatureService.isDarkFeatureEnabledForCurrentUser(this.featureKey);
    }

    protected String queryKey() {
        return Optional.ofNullable(this.featureKey).map(fk -> "darkFeature." + fk + ".enabled").orElseThrow(() -> new IllegalPluginStateException(((Object)((Object)this)).getClass().getSimpleName() + "is not initialised."));
    }
}

