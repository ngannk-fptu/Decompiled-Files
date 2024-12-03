/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.legacy.ContextBatchBuilder;
import com.atlassian.plugin.webresource.legacy.InclusionState;
import com.atlassian.plugin.webresource.legacy.ModuleDescriptorStub;
import com.atlassian.plugin.webresource.legacy.PluginResource;
import com.atlassian.plugin.webresource.legacy.PluginResourceLocator;
import com.atlassian.plugin.webresource.legacy.ResourceDependencyResolver;
import com.atlassian.plugin.webresource.legacy.SuperBatchBuilder;
import com.atlassian.plugin.webresource.legacy.TransformDescriptorToKey;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceRequirer {
    private static final Logger log = LoggerFactory.getLogger(ResourceRequirer.class);
    public final WebResourceIntegration webResourceIntegration;
    public final PluginResourceLocator pluginResourceLocator;
    private final ResourceDependencyResolver dependencyResolver;
    private final boolean isSuperBatchingEnabled;
    private final boolean resplitMergedContextBatchesForThisRequest;
    private final boolean includeDependenciesForFailedUrlReadingConditions;

    public ResourceRequirer(WebResourceIntegration webResourceIntegration, PluginResourceLocator pluginResourceLocator, ResourceDependencyResolver dependencyResolver, boolean resplitMergedContextBatchesForThisRequest, boolean isSuperBatchingEnabled, boolean includeDependenciesForFailedUrlReadingConditions) {
        this.webResourceIntegration = webResourceIntegration;
        this.pluginResourceLocator = pluginResourceLocator;
        this.dependencyResolver = dependencyResolver;
        this.resplitMergedContextBatchesForThisRequest = resplitMergedContextBatchesForThisRequest;
        this.isSuperBatchingEnabled = isSuperBatchingEnabled;
        this.includeDependenciesForFailedUrlReadingConditions = includeDependenciesForFailedUrlReadingConditions;
    }

    public Collection<PluginResource> includeResources(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, Set<String> requiredWebResources, Set<String> requiredContexts, InclusionState inclusion) {
        LinkedList<PluginResource> resourcesToInclude = new LinkedList<PluginResource>();
        this.addSuperBatchResources(resourcesToInclude, inclusion);
        this.addContextBatchDependencies(requestCache, urlBuildingStrategy, resourcesToInclude, requiredWebResources, requiredContexts, inclusion);
        Iterable<String> dependencyModuleKeys = this.getAllModuleKeysDependencies(requestCache, urlBuildingStrategy, requiredWebResources);
        this.addModuleResources(resourcesToInclude, dependencyModuleKeys, inclusion.webresources);
        Iterables.addAll(inclusion.contexts, requiredContexts);
        Iterables.addAll(inclusion.webresources, dependencyModuleKeys);
        return resourcesToInclude;
    }

    private void addSuperBatchResources(List<PluginResource> resourcesToInclude, InclusionState inclusion) {
        if (inclusion.superbatch || !this.isSuperBatchingEnabled) {
            return;
        }
        inclusion.superbatch = true;
        Iterables.addAll(resourcesToInclude, new SuperBatchBuilder(this.dependencyResolver, this.pluginResourceLocator, inclusion).build());
    }

    private void addContextBatchDependencies(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, List<PluginResource> resourcesToInclude, Set<String> requiredWebResources, Set<String> requiredContexts, InclusionState inclusion) {
        ContextBatchBuilder builder = new ContextBatchBuilder(this.dependencyResolver, this.resplitMergedContextBatchesForThisRequest, this.isSuperBatchingEnabled, this.includeDependenciesForFailedUrlReadingConditions);
        Iterable<PluginResource> contextResources = builder.buildBatched(requestCache, urlBuildingStrategy, new ArrayList<String>(requiredContexts), inclusion);
        Iterables.addAll(resourcesToInclude, contextResources);
        Iterables.addAll(inclusion.webresources, builder.getAllIncludedResources());
        Iterables.addAll(requiredWebResources, builder.getSkippedResources());
    }

    private Iterable<String> getAllModuleKeysDependencies(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, Iterable<String> moduleCompleteKeys) {
        LinkedHashSet<String> dependencyModuleCompleteKeys = new LinkedHashSet<String>();
        for (String moduleCompleteKey : moduleCompleteKeys) {
            Iterable<String> dependencies = this.toModuleKeys(this.dependencyResolver.getDependencies(requestCache, urlBuildingStrategy, moduleCompleteKey, this.isSuperBatchingEnabled, this.includeDependenciesForFailedUrlReadingConditions));
            Iterables.addAll(dependencyModuleCompleteKeys, dependencies);
        }
        return dependencyModuleCompleteKeys;
    }

    private void addModuleResources(List<PluginResource> resourcesToInclude, Iterable<String> dependencyModuleCompleteKeys, Set<String> excludeModuleKeys) {
        for (String moduleKey : dependencyModuleCompleteKeys) {
            if (excludeModuleKeys.contains(moduleKey)) continue;
            for (PluginResource moduleResource : this.pluginResourceLocator.getPluginResources(moduleKey)) {
                resourcesToInclude.add(moduleResource);
            }
        }
    }

    private Iterable<String> toModuleKeys(Iterable<ModuleDescriptorStub> descriptors) {
        return Iterables.transform(descriptors, (Function)new TransformDescriptorToKey());
    }
}

