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
import com.atlassian.plugin.webresource.impl.helpers.url.SubBatch;
import com.atlassian.plugin.webresource.impl.helpers.url.UrlGenerationHelpers;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import java.util.List;
import java.util.Map;

public class WebResourceSubBatchUrl
extends ResourceUrl {
    private final Globals globals;
    private final String type;
    private final Map<String, String> params;
    private final String hash;
    private final String key;
    private final SubBatch subBatch;
    private final boolean hasLegacyTransformers;

    public WebResourceSubBatchUrl(Globals globals, String key, SubBatch subBatch, String type, Map<String, String> params, String hash, boolean hasLegacyTransformers, List<PrebakeError> prebakeErrors) {
        super(prebakeErrors);
        this.globals = globals;
        this.key = key;
        this.subBatch = subBatch;
        this.type = type;
        this.hash = hash;
        this.params = params;
        this.hasLegacyTransformers = hasLegacyTransformers;
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
        boolean isCacheable = Resource.isCacheableStatic(this.subBatch.getResourcesParams());
        boolean isCdnSupported = !this.getBundle().hasLegacyConditions() && !this.hasLegacyTransformers;
        return this.globals.getRouter().cloneWithNewUrlMode(isAbsolute).webResourceBatchUrl(this.getKey(), this.getType(), this.getParams(), isCacheable, isCdnSupported, this.hash, this.getBundle().getVersion());
    }

    @Override
    public Map<String, String> getParams() {
        return this.params;
    }

    @Override
    public PluginUrlResource.BatchType getBatchType() {
        return PluginUrlResource.BatchType.RESOURCE;
    }

    public Bundle getBundle() {
        return this.subBatch.getBundles().get(0);
    }

    @Override
    public List<Resource> getResources(RequestCache requestCache) {
        return UrlGenerationHelpers.resourcesOfType(this.getBundle().getResources(requestCache).values(), this.type);
    }
}

