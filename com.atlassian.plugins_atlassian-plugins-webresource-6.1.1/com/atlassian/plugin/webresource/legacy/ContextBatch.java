/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.legacy.ContextBatchPluginResource;
import com.atlassian.plugin.webresource.legacy.ModuleDescriptorStub;
import com.atlassian.plugin.webresource.legacy.PluginResource;
import com.atlassian.plugin.webresource.legacy.TransformDescriptorToKey;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ContextBatch {
    private static final String UTF8 = "UTF-8";
    private static final String MD5 = "MD5";
    private static final Ordering<ModuleDescriptorStub> MODULE_KEY_ORDERING = Ordering.natural().onResultOf((Function)new TransformDescriptorToKey());
    private final List<String> contexts;
    private final Iterable<String> excludedContexts;
    private final Iterable<ModuleDescriptorStub> resources;
    private final Iterable<String> resourceKeys;
    private final boolean removeSuperResources;

    public ContextBatch(List<String> contexts, Iterable<String> excludedContexts, Iterable<ModuleDescriptorStub> resources, boolean removeSuperResources) {
        this.contexts = ImmutableList.copyOf(contexts);
        this.excludedContexts = excludedContexts == null ? Collections.emptyList() : ImmutableList.copyOf(excludedContexts);
        this.resources = ImmutableSortedSet.copyOf(MODULE_KEY_ORDERING, resources);
        this.resourceKeys = Iterables.transform(resources, (Function)new TransformDescriptorToKey());
        this.removeSuperResources = removeSuperResources;
    }

    private static Iterable<PluginResource> postContextBatchesProcess(List<ContextBatchPluginResource> contextBatchResources, boolean resplitMergedBatches) {
        LinkedList<PluginResource> result = new LinkedList<PluginResource>();
        if (resplitMergedBatches) {
            for (ContextBatchPluginResource batchResource : contextBatchResources) {
                result.addAll(batchResource.splitIntoParts());
            }
        } else {
            result.addAll(contextBatchResources);
        }
        return result;
    }

    public boolean isEmpty() {
        return Iterables.isEmpty(this.resources);
    }

    public boolean isRemoveSuperResources() {
        return this.removeSuperResources;
    }

    public Iterable<PluginResource> buildPluginResources(boolean resplitMergedBatches) {
        HashSet<String> descriptors = new HashSet<String>();
        for (ModuleDescriptorStub wrmd : this.getResources()) {
            descriptors.add(wrmd.getCompleteKey());
        }
        ArrayList<ContextBatchPluginResource> resources = new ArrayList<ContextBatchPluginResource>();
        resources.add(new ContextBatchPluginResource(this.contexts, this.excludedContexts, descriptors, this.removeSuperResources));
        return ContextBatch.postContextBatchesProcess(resources, resplitMergedBatches);
    }

    public List<String> getContexts() {
        return this.contexts;
    }

    public Iterable<String> getExcludedContexts() {
        return this.excludedContexts;
    }

    public Iterable<ModuleDescriptorStub> getResources() {
        return this.resources;
    }

    Iterable<String> getResourceKeys() {
        return Iterables.transform(this.resources, ModuleDescriptorStub::getCompleteKey);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ContextBatch)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        ContextBatch other = (ContextBatch)obj;
        return this.contexts.equals(other.contexts) && this.excludedContexts.equals(other.excludedContexts);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.contexts, this.excludedContexts});
    }

    public String getKey() {
        return Router.encodeContexts(this.contexts, this.excludedContexts);
    }
}

