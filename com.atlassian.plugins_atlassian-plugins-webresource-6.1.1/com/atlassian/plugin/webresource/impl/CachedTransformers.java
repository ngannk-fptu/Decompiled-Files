/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.plugin.webresource.WebResourceTransformation;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.transformer.TransformerCache;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CachedTransformers {
    private final List<WebResourceTransformation> transformations;
    private Set<String> paramKeys;

    public CachedTransformers(List<WebResourceTransformation> transformations) {
        this.transformations = transformations;
    }

    public boolean addToUrlSafely(UrlBuilder urlBuilder, UrlBuildingStrategy urlBuildingStrategy, String type, TransformerCache transformerCache, TransformerParameters transformerParameters, String webResourceKey) {
        boolean containsLegacyTransformers = false;
        for (WebResourceTransformation transformation : this.transformations) {
            if (!transformation.matches(type)) continue;
            try {
                transformation.addTransformParameters(transformerCache, transformerParameters, urlBuilder, urlBuildingStrategy);
            }
            catch (RuntimeException e) {
                Support.LOGGER.warn("error thrown in transformer during url generation for " + webResourceKey, (Throwable)e);
            }
            if (transformation.containsOnlyPureUrlReadingTransformers(transformerCache)) continue;
            containsLegacyTransformers = true;
        }
        return containsLegacyTransformers;
    }

    public List<WebResourceTransformation> getTransformations() {
        return this.transformations;
    }

    public Dimensions computeDimensions(TransformerCache transformerCache) {
        Dimensions d = Dimensions.empty();
        for (WebResourceTransformation transformation : this.transformations) {
            d = d.product(transformation.computeDimensions(transformerCache));
        }
        return d;
    }

    public void computeParamKeys(TransformerCache transformerCache) {
        if (this.paramKeys != null) {
            throw new IllegalStateException("invalid usage, method computeParamKeys should be called only once!");
        }
        this.paramKeys = new HashSet<String>();
        for (WebResourceTransformation transformation : this.transformations) {
            transformation.computeDimensions(transformerCache).cartesianProduct().forEach(coordinate -> {
                for (String key : coordinate.getKeys()) {
                    this.paramKeys.add(key);
                }
            });
        }
    }

    public Set<String> getParamKeys() {
        return this.paramKeys;
    }
}

