/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.ResourceKey;
import com.atlassian.plugin.webresource.impl.ResourceKeysSupplier;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.helpers.url.SubBatch;
import com.atlassian.plugin.webresource.impl.helpers.url.UrlGenerationHelpers;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ContextBatch {
    private final List<String> included;
    private final LinkedHashSet<String> excluded;
    private final List<String> excludedWithoutApplyingConditions;
    private final boolean isAdditionalSortingRequired;
    private final List<String> skippedWebResourcesWithUrlReadingConditions;
    private final Map<String, ResourceKeysSupplier> standaloneResourceKeysByType;
    private final List<SubBatch> subBatches;

    public ContextBatch(List<String> included, LinkedHashSet<String> excluded, List<String> skippedWebResourcesWithUrlReadingConditions, List<String> excludedWithoutApplyingConditions, List<SubBatch> subBatches, List<Resource> standaloneResources, boolean isAdditionalSortingRequired) {
        this.included = included;
        this.excluded = excluded;
        this.excludedWithoutApplyingConditions = excludedWithoutApplyingConditions;
        this.skippedWebResourcesWithUrlReadingConditions = skippedWebResourcesWithUrlReadingConditions;
        this.subBatches = subBatches;
        this.standaloneResourceKeysByType = new HashMap<String, ResourceKeysSupplier>();
        for (String type : Config.BATCH_TYPES) {
            List<Resource> resourcesOfType = UrlGenerationHelpers.resourcesOfType(standaloneResources, type);
            List<ResourceKey> resourcesKey = RequestCache.toResourceKeys(resourcesOfType);
            ResourceKeysSupplier resourceKeysSupplier = new ResourceKeysSupplier(resourcesKey);
            this.standaloneResourceKeysByType.put(type, resourceKeysSupplier);
        }
        this.isAdditionalSortingRequired = isAdditionalSortingRequired;
    }

    public List<Resource> getStandaloneResourcesOfType(RequestCache requestCache, String type) {
        return requestCache.getCachedResources(this.standaloneResourceKeysByType.get(type));
    }

    public List<String> getIncluded() {
        return this.included;
    }

    public LinkedHashSet<String> getExcluded() {
        return this.excluded;
    }

    public List<String> getSkippedWebResourcesWithUrlReadingConditions() {
        return this.skippedWebResourcesWithUrlReadingConditions;
    }

    public List<String> getExcludedWithoutApplyingConditions() {
        return this.excludedWithoutApplyingConditions;
    }

    public List<SubBatch> getSubBatches() {
        return this.subBatches;
    }

    public boolean isAdditionalSortingRequired() {
        return this.isAdditionalSortingRequired;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(StringUtils.join(this.included, (String)", "));
        if (!this.excluded.isEmpty()) {
            buffer.append('-');
            buffer.append(StringUtils.join(this.excluded, (String)", "));
        }
        return buffer.toString();
    }
}

