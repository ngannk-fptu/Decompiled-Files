/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.helpers.ResourceGenerationInfo;
import com.atlassian.plugin.webresource.impl.helpers.url.CalculatedBatches;
import com.atlassian.plugin.webresource.impl.helpers.url.Resolved;
import com.atlassian.plugin.webresource.impl.helpers.url.UrlGenerationHelpers;
import com.atlassian.plugin.webresource.impl.support.UrlCache;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ResourceUrlGenerator {
    private final UrlCache cache;

    public ResourceUrlGenerator(@Nonnull UrlCache cache) {
        this.cache = Objects.requireNonNull(cache);
    }

    @Nonnull
    public Resolved generate(@Nonnull ResourceGenerationInfo information) {
        RequestState requestState = information.getData();
        LinkedHashSet allIncluded = information.getResourcePhase().map(requestState::getIncluded).orElseGet(requestState::getIncluded);
        LinkedHashSet<String> allExcluded = new LinkedHashSet<String>();
        allExcluded.addAll(requestState.getExcludedResolved());
        allExcluded.addAll(requestState.getExcluded());
        if (allIncluded.isEmpty()) {
            return new Resolved(allExcluded);
        }
        UrlCache.IncludedExcludedConditionsAndBatchingOptions cacheKey = UrlGenerationHelpers.buildIncludedExcludedConditionsAndBatchingOptions(requestState.getRequestCache(), requestState.getUrlStrategy(), allIncluded, requestState.getExcluded());
        CalculatedBatches calculatedBatches = this.cache.getBatches(cacheKey, key -> UrlGenerationHelpers.calculateBatches(requestState.getRequestCache(), requestState.getUrlStrategy(), allIncluded, allExcluded, key.getExcluded()));
        List<ResourceUrl> resourceUrls = UrlGenerationHelpers.collectUrlStateAndBuildResourceUrls(requestState, requestState.getUrlStrategy(), calculatedBatches.getContextBatches(), calculatedBatches.getWebResourceBatches());
        return new Resolved(resourceUrls, calculatedBatches.getExcludedResolved());
    }
}

