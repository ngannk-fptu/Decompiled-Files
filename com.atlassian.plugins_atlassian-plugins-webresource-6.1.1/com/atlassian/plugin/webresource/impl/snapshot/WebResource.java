/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 */
package com.atlassian.plugin.webresource.impl.snapshot;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.CachedTransformers;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebResource
extends Bundle {
    private final TransformerParameters transformerParameters;
    private final Map<String, Set<String>> locationResourceTypes;

    public WebResource(Snapshot snapshot, String key, List<String> dependencies, Date updatedAt, String version, boolean isTransformable, TransformerParameters transformerParameters, Map<String, Set<String>> locationResourceTypes) {
        super(snapshot, key, dependencies, updatedAt, version, isTransformable);
        this.transformerParameters = transformerParameters;
        this.locationResourceTypes = locationResourceTypes;
    }

    @Override
    public LinkedHashMap<String, Resource> getResources(RequestCache cache) {
        LinkedHashMap<String, Resource> resources = cache.getCachedResources().get(this);
        if (resources == null) {
            resources = this.snapshot.config.getResourcesWithoutCache(this);
            cache.getCachedResources().put(this, resources);
        }
        return resources;
    }

    @Override
    public Set<String> getLocationResourceTypesFor(String nameType) {
        Set<String> types = this.locationResourceTypes.get(nameType);
        if (types == null) {
            types = new HashSet<String>();
        }
        return types;
    }

    @Override
    public CachedCondition getCondition() {
        return this.snapshot.webResourcesCondition.get(this);
    }

    @Override
    public CachedTransformers getTransformers() {
        return this.snapshot.webResourcesTransformations.get(this);
    }

    @Override
    public TransformerParameters getTransformerParameters() {
        return this.transformerParameters;
    }

    @Override
    public LinkedHashMap<String, Jsonable> getData() {
        return this.snapshot.config.getWebResourceData(this.getKey());
    }

    @Deprecated
    public String getWebResourceKey() {
        return this.getKey().split(":")[1];
    }

    @Deprecated
    public String getPluginKey() {
        return this.getKey().split(":")[0];
    }
}

