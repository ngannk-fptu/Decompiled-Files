/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.condition.MetricsUtil;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.prebake.DimensionUnawareOverride;
import com.atlassian.plugin.webresource.url.DefaultUrlBuilder;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.webresource.api.prebake.DimensionAwareUrlReadingCondition;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Map;

public class DecoratingUrlReadingCondition
implements DecoratingCondition {
    protected final UrlReadingCondition urlReadingCondition;
    protected final Map<String, String> params;
    private final String pluginKey;
    private final String conditionClassName;

    public DecoratingUrlReadingCondition(UrlReadingCondition urlReadingCondition, Map<String, String> params) {
        this(urlReadingCondition, params, "", "");
    }

    public DecoratingUrlReadingCondition(UrlReadingCondition urlReadingCondition, Map<String, String> params, String pluginKey, String conditionClassName) {
        this.urlReadingCondition = urlReadingCondition;
        this.params = params;
        this.pluginKey = pluginKey;
        this.conditionClassName = conditionClassName;
    }

    @Override
    public Dimensions computeDimensions() {
        if (this.urlReadingCondition instanceof DimensionAwareUrlReadingCondition) {
            return ((DimensionAwareUrlReadingCondition)this.urlReadingCondition).computeDimensions();
        }
        String className = this.urlReadingCondition.getClass().getName();
        if (DimensionUnawareOverride.contains(className)) {
            return DimensionUnawareOverride.dimensions(className);
        }
        return Dimensions.empty();
    }

    @Override
    public void addToUrl(UrlBuilder urlBuilder, UrlBuildingStrategy urlBuilderStrategy) {
        urlBuilderStrategy.addToUrl(this.urlReadingCondition, urlBuilder);
    }

    @Override
    public boolean canEncodeStateIntoUrl() {
        return true;
    }

    @Override
    public boolean shouldDisplayImmediate(Map<String, Object> context, UrlBuildingStrategy urlBuilderStrategy) {
        DefaultUrlBuilder urlBuilder = new DefaultUrlBuilder();
        this.addToUrl(urlBuilder, urlBuilderStrategy);
        return this.shouldDisplay(QueryParams.of(urlBuilder.buildParams()));
    }

    @Override
    public boolean shouldDisplay(QueryParams params) {
        try (Ticker ignored = MetricsUtil.startWebConditionProfilingTimer(this.pluginKey, this.conditionClassName);){
            boolean bl = this.urlReadingCondition.shouldDisplay(params);
            return bl;
        }
    }

    @Override
    public DecoratingCondition invertCondition() {
        return new DecoratingCondition(){

            @Override
            public void addToUrl(UrlBuilder urlBuilder, UrlBuildingStrategy urlBuilderStrategy) {
                DecoratingUrlReadingCondition.this.addToUrl(urlBuilder, urlBuilderStrategy);
            }

            @Override
            public boolean shouldDisplay(QueryParams params) {
                return !DecoratingUrlReadingCondition.this.shouldDisplay(params);
            }

            @Override
            public Dimensions computeDimensions() {
                return DecoratingUrlReadingCondition.this.computeDimensions();
            }

            @Override
            public boolean canEncodeStateIntoUrl() {
                return DecoratingUrlReadingCondition.this.canEncodeStateIntoUrl();
            }

            @Override
            public boolean shouldDisplayImmediate(Map<String, Object> context, UrlBuildingStrategy urlBuilderStrategy) {
                return !DecoratingUrlReadingCondition.this.shouldDisplayImmediate(context, urlBuilderStrategy);
            }

            @Override
            public DecoratingCondition invertCondition() {
                return DecoratingUrlReadingCondition.this;
            }
        };
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public UrlReadingCondition getUrlReadingCondition() {
        return this.urlReadingCondition;
    }
}

