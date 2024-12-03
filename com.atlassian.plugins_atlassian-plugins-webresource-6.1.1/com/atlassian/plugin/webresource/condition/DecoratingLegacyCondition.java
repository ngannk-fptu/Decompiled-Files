/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.condition.MetricsUtil;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;

class DecoratingLegacyCondition
implements DecoratingCondition {
    protected final Condition legacyCondition;
    private final boolean invert;
    private final String pluginKey;
    private final String conditionClassName;

    public DecoratingLegacyCondition(Condition legacyCondition) {
        this(legacyCondition, false);
    }

    public DecoratingLegacyCondition(Condition legacyCondition, boolean invert) {
        this(legacyCondition, "", "", invert);
    }

    public DecoratingLegacyCondition(Condition legacyCondition, String pluginKey, String conditionClassName) {
        this(legacyCondition, pluginKey, conditionClassName, false);
    }

    public DecoratingLegacyCondition(Condition legacyCondition, String pluginKey, String conditionClassName, boolean invert) {
        this.legacyCondition = legacyCondition;
        this.pluginKey = pluginKey;
        this.conditionClassName = conditionClassName;
        this.invert = invert;
    }

    @Override
    public void addToUrl(UrlBuilder urlBuilder, UrlBuildingStrategy urlBuilderStrategy) {
    }

    @Override
    public Dimensions computeDimensions() {
        return Dimensions.empty();
    }

    @Override
    public boolean shouldDisplay(QueryParams params) {
        return true;
    }

    @Override
    public boolean shouldDisplayImmediate(Map<String, Object> params, UrlBuildingStrategy urlBuilderStrategy) {
        try (Ticker ignored = MetricsUtil.startWebConditionProfilingTimer(this.pluginKey, this.conditionClassName);){
            boolean shouldDisplay = this.legacyCondition.shouldDisplay(params);
            boolean bl = this.invert ? !shouldDisplay : shouldDisplay;
            return bl;
        }
    }

    @Override
    public boolean canEncodeStateIntoUrl() {
        return false;
    }

    @Override
    public DecoratingCondition invertCondition() {
        return new DecoratingLegacyCondition(this.legacyCondition, this.pluginKey, this.conditionClassName, !this.invert);
    }
}

