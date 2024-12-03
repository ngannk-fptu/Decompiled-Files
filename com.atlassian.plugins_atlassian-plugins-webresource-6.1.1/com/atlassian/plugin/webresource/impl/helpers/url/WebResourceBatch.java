/*
 * Decompiled with CFR 0.152.
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
import java.util.List;
import java.util.Map;

public class WebResourceBatch {
    private final String key;
    private final List<SubBatch> subBatches;
    private final Map<String, ResourceKeysSupplier> standaloneResourceKeysByType = new HashMap<String, ResourceKeysSupplier>();

    public WebResourceBatch(String key, List<SubBatch> subBatches, List<Resource> standaloneResources) {
        this.key = key;
        this.subBatches = subBatches;
        for (String type : Config.BATCH_TYPES) {
            List<Resource> resourcesOfType = UrlGenerationHelpers.resourcesOfType(standaloneResources, type);
            List<ResourceKey> resourceKeys = RequestCache.toResourceKeys(resourcesOfType);
            ResourceKeysSupplier resourceKeysSupplier = new ResourceKeysSupplier(resourceKeys);
            this.standaloneResourceKeysByType.put(type, resourceKeysSupplier);
        }
    }

    public String getKey() {
        return this.key;
    }

    public List<Resource> getStandaloneResourcesOfType(RequestCache requestCache, String type) {
        return requestCache.getCachedResources(this.standaloneResourceKeysByType.get(type));
    }

    public List<SubBatch> getSubBatches() {
        return this.subBatches;
    }
}

