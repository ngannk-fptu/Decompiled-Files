/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.legacy.BatchPluginResource;
import com.atlassian.plugin.webresource.legacy.InclusionState;
import com.atlassian.plugin.webresource.legacy.ModuleDescriptorStub;
import com.atlassian.plugin.webresource.legacy.PluginResource;
import com.atlassian.plugin.webresource.legacy.PluginResourceLocator;
import com.atlassian.plugin.webresource.legacy.ResourceDependencyResolver;
import com.atlassian.plugin.webresource.legacy.SuperBatchPluginResource;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SuperBatchBuilder {
    private final ResourceDependencyResolver dependencyResolver;
    private final PluginResourceLocator pluginResourceLocator;
    private final InclusionState excludedThings;

    public SuperBatchBuilder(ResourceDependencyResolver dependencyResolver, PluginResourceLocator pluginResourceLocator) {
        this(dependencyResolver, pluginResourceLocator, null);
    }

    public SuperBatchBuilder(ResourceDependencyResolver dependencyResolver, PluginResourceLocator pluginResourceLocator, InclusionState inclusionState) {
        this.dependencyResolver = dependencyResolver;
        this.pluginResourceLocator = pluginResourceLocator;
        this.excludedThings = inclusionState;
    }

    public Iterable<PluginResource> build() {
        Predicate<String> alreadyIncluded = this.excludedThings.webresources::contains;
        Predicate<String> notIncludedYet = alreadyIncluded.negate();
        List superBatchModuleKeys = StreamSupport.stream(this.dependencyResolver.getSuperBatchDependencies().spliterator(), false).map(ModuleDescriptorStub::getCompleteKey).collect(Collectors.toList());
        SuperBatchPluginResource superBatchPluginResource = new SuperBatchPluginResource();
        this.excludedThings.topLevel.forEach(superBatchPluginResource::addExcludedContext);
        superBatchModuleKeys.stream().filter(notIncludedYet).map(this.pluginResourceLocator::getPluginResources).flatMap(Collection::stream).filter(BatchPluginResource.class::isInstance).map(BatchPluginResource.class::cast).map(BatchPluginResource::getModuleCompleteKey).forEachOrdered(superBatchPluginResource::addBatchedWebResourceDescriptor);
        if (superBatchPluginResource.isEmpty()) {
            return Collections.emptyList();
        }
        Iterables.addAll(this.excludedThings.webresources, superBatchModuleKeys);
        return Collections.singletonList(superBatchPluginResource);
    }
}

