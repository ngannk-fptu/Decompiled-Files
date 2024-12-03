/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.helpers.url.ContextBatch;
import com.atlassian.plugin.webresource.impl.helpers.url.SubBatch;
import com.atlassian.plugin.webresource.impl.helpers.url.UrlGenerationHelpers;
import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ContextSubBatchResourceUrl
extends ResourceUrl {
    private final String key;
    private final Globals globals;
    private final String type;
    private final ContextBatch contextBatch;
    private final SubBatch subBatch;
    private final String hash;
    private final Map<String, String> params;
    private final boolean hasLegacyTransformers;

    public ContextSubBatchResourceUrl(Globals globals, ContextBatch contextBatch, SubBatch subBatch, String type, Map<String, String> params, String hash, boolean hasLegacyTransformers, List<PrebakeError> prebakeErrors) {
        super(prebakeErrors);
        this.globals = globals;
        this.contextBatch = contextBatch;
        this.subBatch = subBatch;
        this.hash = hash;
        this.params = params;
        this.key = Router.encodeContexts(contextBatch.getIncluded(), contextBatch.getExcluded());
        this.type = type;
        this.hasLegacyTransformers = hasLegacyTransformers;
    }

    public SubBatch getSubBatch() {
        return this.subBatch;
    }

    public List<Bundle> getBatchedBundles() {
        return this.subBatch.getBundles();
    }

    public List<String> getIncludedContexts() {
        return this.contextBatch.getIncluded();
    }

    public LinkedHashSet<String> getExcludedContexts() {
        return this.contextBatch.getExcluded();
    }

    @Override
    public String getName() {
        return this.key + '.' + this.type;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getUrl(boolean isAbsolute) {
        boolean hasLegacyConditions = false;
        for (Bundle bundle : this.subBatch.getBundles()) {
            if (!bundle.hasLegacyConditions()) continue;
            hasLegacyConditions = true;
            break;
        }
        boolean isCdnSupported = !hasLegacyConditions && !this.hasLegacyTransformers;
        return this.globals.getRouter().cloneWithNewUrlMode(isAbsolute).contextBatchUrl(this.getKey(), this.getType(), this.getParams(), Resource.isCacheableStatic(this.subBatch.getResourcesParams()), isCdnSupported, this.hash, UrlGenerationHelpers.calculateBundlesHash(this.subBatch.getAllFoundBundles()));
    }

    @Override
    public Map<String, String> getParams() {
        return this.params;
    }

    @Override
    public PluginUrlResource.BatchType getBatchType() {
        return PluginUrlResource.BatchType.CONTEXT;
    }

    @Override
    public List<Resource> getResources(RequestCache requestCache) {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (Bundle bundle : this.subBatch.getBundles()) {
            resources.addAll(UrlGenerationHelpers.resourcesOfType(bundle.getResources(requestCache).values(), this.type));
        }
        return resources;
    }
}

