/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.url.DefaultUrlBuilder;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CachedCondition {
    private static final Map<String, Object> EMPTY_IMMUTABLE_CONTEXT = Collections.unmodifiableMap(new HashMap());
    private final DecoratingCondition condition;
    private final boolean isLegacy;

    public CachedCondition(DecoratingCondition condition) {
        this.condition = condition;
        this.isLegacy = !condition.canEncodeStateIntoUrl();
    }

    public boolean evaluateSafely(RequestCache requestCache, UrlBuildingStrategy urlBuilderStrategy) {
        Boolean result = requestCache.getCachedConditionsEvaluation().get(this);
        if (result == null) {
            if (this.isLegacy) {
                try {
                    result = this.condition.shouldDisplayImmediate(EMPTY_IMMUTABLE_CONTEXT, urlBuilderStrategy);
                }
                catch (RuntimeException e) {
                    Support.LOGGER.warn("exception thrown in `shouldDisplayImmediate` during condition evaluation", (Throwable)e);
                    return false;
                }
            } else {
                DefaultUrlBuilder defaultUrlBuilder = new DefaultUrlBuilder();
                this.addToUrlSafely(requestCache, defaultUrlBuilder, urlBuilderStrategy);
                result = this.evaluateSafely(requestCache, defaultUrlBuilder.buildParams());
            }
            requestCache.getCachedConditionsEvaluation().put(this, result);
        }
        return result;
    }

    public boolean evaluateSafely(RequestCache requestCache, Map<String, String> params) {
        Boolean result = requestCache.getCachedConditionsEvaluation().get(this);
        if (result == null) {
            try {
                result = this.condition.shouldDisplay(QueryParams.of(params));
            }
            catch (RuntimeException e) {
                Support.LOGGER.warn("exception thrown in `shouldDisplay` during condition evaluation", (Throwable)e);
                return false;
            }
            requestCache.getCachedConditionsEvaluation().put(this, result);
        }
        return result;
    }

    public void addToUrlSafely(RequestCache requestCache, UrlBuilder urlBuilder, UrlBuildingStrategy urlBuilderStrategy) {
        if (!this.isLegacy) {
            DefaultUrlBuilder result = requestCache.getCachedConditionsParameters().get(this);
            if (result == null) {
                result = new DefaultUrlBuilder();
                try {
                    this.condition.addToUrl(result, urlBuilderStrategy);
                }
                catch (RuntimeException e) {
                    Support.LOGGER.warn("exception thrown in `addToUrl` during condition evaluation", (Throwable)e);
                }
                requestCache.getCachedConditionsParameters().put(this, result);
            }
            result.applyTo(urlBuilder);
        }
    }

    public boolean isLegacy() {
        return this.isLegacy;
    }

    public Dimensions computeDimensions() {
        return this.condition.computeDimensions();
    }
}

