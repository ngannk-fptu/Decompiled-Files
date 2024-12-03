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
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceUrlImpl
extends ResourceUrl {
    private final Globals globals;
    private final Resource resource;
    private final String hash;
    private final Map<String, String> params;
    private final boolean hasLegacyTransformer;

    public ResourceUrlImpl(Globals globals, Resource resource, Map<String, String> params, String hash, boolean hasLegacyTransformer, List<PrebakeError> prebakeErrors) {
        super(prebakeErrors);
        this.globals = globals;
        this.resource = resource;
        this.params = params;
        this.hash = hash;
        this.hasLegacyTransformer = hasLegacyTransformer;
    }

    @Override
    public String getName() {
        return this.resource.getName();
    }

    @Override
    public String getKey() {
        return this.resource.getKey();
    }

    @Override
    public String getType() {
        return this.resource.getNameOrLocationType();
    }

    @Override
    public String getUrl(boolean isAbsolute) {
        boolean isCdnSupported = !this.hasLegacyTransformer && !this.resource.getParent().hasLegacyConditions();
        return this.globals.getRouter().cloneWithNewUrlMode(isAbsolute).resourceUrl(this.getKey(), this.getName(), this.getParams(), this.resource.isCacheable(), isCdnSupported, this.hash, this.resource.getVersion());
    }

    @Override
    public Map<String, String> getParams() {
        return this.params;
    }

    @Override
    public PluginUrlResource.BatchType getBatchType() {
        return PluginUrlResource.BatchType.RESOURCE;
    }

    @Override
    public List<Resource> getResources(RequestCache requestCache) {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(this.resource);
        return resources;
    }
}

