/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package com.atlassian.plugin.webresource.impl.discovery;

import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.discovery.BundleFinder;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceFinder {
    private final BundleFinder bundleFinder;
    private final RequestCache requestCache;
    private Predicate<Resource> filter;

    public ResourceFinder(BundleFinder bundleFinder, RequestCache requestCache) {
        this.bundleFinder = bundleFinder;
        this.requestCache = requestCache;
    }

    public ResourceFinder filter(Predicate<Resource> filter) {
        this.filter = filter;
        return this;
    }

    public List<Resource> end() {
        List<String> keys = this.bundleFinder.end();
        List<Resource> resources = this.getResources(keys);
        if (this.filter != null) {
            return resources.stream().filter(this.filter).collect(Collectors.toList());
        }
        return resources;
    }

    public List<Resource> getResources(Collection<String> keys) {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (String key : keys) {
            Bundle bundle = this.requestCache.getSnapshot().get(key);
            if (bundle == null) continue;
            resources.addAll(bundle.getResources(this.requestCache).values());
        }
        return resources;
    }
}

