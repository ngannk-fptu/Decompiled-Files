/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.ResourceKey;
import com.atlassian.plugin.webresource.impl.ResourceKeysSupplier;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.helpers.url.UrlGenerationHelpers;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubBatch {
    private final List<Bundle> filteredBundles;
    private final Map<String, String> resourcesParams;
    private final Map<String, ResourceKeysSupplier> resourceKeysByType;
    private final List<Bundle> allFoundBundles;

    public SubBatch(Map<String, String> resourcesParams, Bundle bundle, List<Resource> resources) {
        this(resourcesParams, Lists.newArrayList((Object[])new Bundle[]{bundle}), resources, Collections.singletonList(bundle));
    }

    public SubBatch(Map<String, String> resourcesParams, List<Bundle> filteredBundles, List<Resource> resources, List<Bundle> allFoundBundles) {
        this.resourcesParams = resourcesParams;
        this.filteredBundles = filteredBundles;
        this.resourceKeysByType = new HashMap<String, ResourceKeysSupplier>();
        this.allFoundBundles = allFoundBundles;
        for (String type : Config.BATCH_TYPES) {
            List<Resource> resourcesOfType = UrlGenerationHelpers.resourcesOfType(resources, type);
            List<ResourceKey> resourceKeys = RequestCache.toResourceKeys(resourcesOfType);
            ResourceKeysSupplier resourceKeysSupplier = new ResourceKeysSupplier(resourceKeys);
            this.resourceKeysByType.put(type, resourceKeysSupplier);
        }
    }

    public List<Bundle> getBundles() {
        return this.filteredBundles;
    }

    public List<Resource> getResourcesOfType(RequestCache requestCache, String type) {
        return requestCache.getCachedResources(this.resourceKeysByType.get(type));
    }

    public Map<String, String> getResourcesParams() {
        return this.resourcesParams;
    }

    public List<Bundle> getAllFoundBundles() {
        return this.allFoundBundles;
    }
}

