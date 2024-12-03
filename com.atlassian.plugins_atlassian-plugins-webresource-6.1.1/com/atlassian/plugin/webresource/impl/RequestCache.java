/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.ResourceKey;
import com.atlassian.plugin.webresource.impl.ResourceKeysSupplier;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.url.DefaultUrlBuilder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestCache {
    private Snapshot cachedSnapshot;
    private final Map<CachedCondition, Boolean> cachedConditionsEvaluation;
    private final Map<CachedCondition, DefaultUrlBuilder> cachedConditionsParameters;
    private final Map<Bundle, LinkedHashMap<String, Resource>> cachedResources;
    private final Map<ResourceKeysSupplier, List<Resource>> cachedResourceLists;
    private final Globals globals;

    public RequestCache(Globals globals) {
        this.globals = globals;
        this.cachedResources = new HashMap<Bundle, LinkedHashMap<String, Resource>>();
        this.cachedResourceLists = new HashMap<ResourceKeysSupplier, List<Resource>>();
        this.cachedConditionsEvaluation = new HashMap<CachedCondition, Boolean>();
        this.cachedConditionsParameters = new HashMap<CachedCondition, DefaultUrlBuilder>();
    }

    public static List<ResourceKey> toResourceKeys(List<Resource> resources) {
        return resources.stream().map(ResourceKey::new).collect(Collectors.toList());
    }

    public Globals getGlobals() {
        return this.globals;
    }

    public Map<Bundle, LinkedHashMap<String, Resource>> getCachedResources() {
        return this.cachedResources;
    }

    public List<Resource> getCachedResources(ResourceKeysSupplier resourceKeysSupplier) {
        return Optional.ofNullable(this.cachedResourceLists.get(resourceKeysSupplier)).orElseGet(() -> {
            List resources = resourceKeysSupplier.getKeys().stream().map(key -> this.cachedSnapshot.get(key.getKey()).getResources(this).get(key.getName())).collect(Collectors.toList());
            this.cachedResourceLists.put(resourceKeysSupplier, resources);
            return resources;
        });
    }

    public Map<CachedCondition, Boolean> getCachedConditionsEvaluation() {
        return this.cachedConditionsEvaluation;
    }

    public Map<CachedCondition, DefaultUrlBuilder> getCachedConditionsParameters() {
        return this.cachedConditionsParameters;
    }

    public Snapshot getSnapshot() {
        this.cachedSnapshot = Optional.ofNullable(this.cachedSnapshot).orElseGet(this.globals::getSnapshot);
        return this.cachedSnapshot;
    }
}

