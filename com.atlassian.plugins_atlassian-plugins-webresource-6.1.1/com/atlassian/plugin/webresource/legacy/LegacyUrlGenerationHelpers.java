/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.helpers.url.ContextBatchKey;
import com.atlassian.plugin.webresource.legacy.BatchPluginResource;
import com.atlassian.plugin.webresource.legacy.ContextBatchPluginResource;
import com.atlassian.plugin.webresource.legacy.DefaultResourceDependencyResolver;
import com.atlassian.plugin.webresource.legacy.InclusionState;
import com.atlassian.plugin.webresource.legacy.PluginResource;
import com.atlassian.plugin.webresource.legacy.PluginResourceLocatorImpl;
import com.atlassian.plugin.webresource.legacy.ResourceRequirer;
import com.atlassian.plugin.webresource.legacy.SuperBatchPluginResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LegacyUrlGenerationHelpers {
    public static Resolved calculateBatches(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, Collection<String> topLevelIncluded, Collection<String> excludedResolved, boolean includeDependenciesForFailedUrlReadingConditions) {
        return LegacyUrlGenerationHelpers.calculateBatches(requestCache, urlBuildingStrategy, topLevelIncluded, excludedResolved, Collections.emptySet(), includeDependenciesForFailedUrlReadingConditions);
    }

    public static Resolved calculateBatches(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, Collection<String> topLevelIncluded, Collection<String> allExcluded, Set<String> topLevelExcluded, boolean includeDependenciesForFailedUrlReadingConditions) {
        LinkedHashSet<String> excluded;
        boolean superBatchIsNotEmpty = requestCache.getGlobals().getConfig().isSuperbatchCreated();
        String prefix = "_context:";
        LinkedHashSet<String> requiredContexts = new LinkedHashSet<String>();
        LinkedHashSet<String> requiredWebResources = new LinkedHashSet<String>();
        boolean isSuperBatchEnabled = false;
        for (String key : topLevelIncluded) {
            if (Config.isContextKey(key)) {
                if ("_context:_super".equals(key)) {
                    isSuperBatchEnabled = true;
                    continue;
                }
                requiredContexts.add(key.replace("_context:", ""));
                continue;
            }
            requiredWebResources.add(key);
        }
        LinkedHashSet<String> excludedContexts = new LinkedHashSet<String>();
        LinkedHashSet<String> excludedWebResources = new LinkedHashSet<String>();
        boolean isSuperBatchHasBeenEscluded = false;
        for (String key : allExcluded) {
            if ("_context:_super".equals(key)) {
                isSuperBatchHasBeenEscluded = true;
                continue;
            }
            if (Config.isContextKey(key)) {
                excludedContexts.add(key.replace("_context:", ""));
                continue;
            }
            excludedWebResources.add(key);
        }
        Config config = requestCache.getGlobals().getConfig();
        ArrayList<String> superBatchKeys = new ArrayList<String>(config.getBeforeAllResources());
        superBatchKeys.addAll(config.getBatchingConfiguration().getSuperBatchModuleCompleteKeys());
        DefaultResourceDependencyResolver legacyDependencyResolver = new DefaultResourceDependencyResolver(requestCache.getGlobals(), config.getIntegration(), isSuperBatchEnabled |= isSuperBatchHasBeenEscluded, superBatchKeys);
        PluginResourceLocatorImpl legacyResourceLocator = new PluginResourceLocatorImpl(config.getIntegration());
        ResourceRequirer resourceRequirer = new ResourceRequirer(config.getIntegration(), legacyResourceLocator, legacyDependencyResolver, config.resplitMergedContextBatchesForThisRequest(), isSuperBatchEnabled, includeDependenciesForFailedUrlReadingConditions);
        InclusionState inclusionState = new InclusionState(isSuperBatchHasBeenEscluded, excludedWebResources, excludedContexts, topLevelExcluded);
        Collection<PluginResource> resources = resourceRequirer.includeResources(requestCache, urlBuildingStrategy, requiredWebResources, requiredContexts, inclusionState);
        ArrayList<ContextBatchKey> contextBatchKeys = new ArrayList<ContextBatchKey>();
        ArrayList<String> webResourceBatchKeys = new ArrayList<String>();
        boolean hasSuperbatch = false;
        for (PluginResource pluginResource : resources) {
            if (!(pluginResource instanceof SuperBatchPluginResource)) continue;
            ArrayList<String> included = new ArrayList<String>();
            SuperBatchPluginResource contextBatchPluginResource = (SuperBatchPluginResource)pluginResource;
            included.add("_context:_super");
            excluded = new LinkedHashSet<String>();
            for (String key : contextBatchPluginResource.getExcludedContexts()) {
                excluded.add("_context:" + key);
            }
            contextBatchKeys.add(new ContextBatchKey(included, excluded));
            hasSuperbatch = true;
            break;
        }
        boolean shouldSubtractSuperbatch = superBatchIsNotEmpty && (isSuperBatchHasBeenEscluded || hasSuperbatch && inclusionState.superbatch);
        for (PluginResource pluginResource : resources) {
            if (pluginResource instanceof ContextBatchPluginResource) {
                ArrayList<String> included = new ArrayList<String>();
                excluded = new LinkedHashSet();
                ContextBatchPluginResource contextBatchPluginResource = (ContextBatchPluginResource)pluginResource;
                for (String key : contextBatchPluginResource.getContexts()) {
                    included.add("_context:" + key);
                }
                if (shouldSubtractSuperbatch) {
                    excluded.add("_context:_super");
                }
                for (String key : contextBatchPluginResource.getExcludedContexts()) {
                    excluded.add("_context:" + key);
                }
                contextBatchKeys.add(new ContextBatchKey(included, excluded));
            }
            if (!(pluginResource instanceof BatchPluginResource)) continue;
            BatchPluginResource batchPluginResource = (BatchPluginResource)pluginResource;
            webResourceBatchKeys.add(batchPluginResource.getModuleCompleteKey());
        }
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
        if (shouldSubtractSuperbatch) {
            linkedHashSet.add("_context:_super");
        }
        for (String key : inclusionState.contexts) {
            if ("_super".equals(key)) continue;
            linkedHashSet.add("_context:" + key);
        }
        linkedHashSet.addAll(inclusionState.webresources);
        return new Resolved(contextBatchKeys, webResourceBatchKeys, linkedHashSet);
    }

    public static class Resolved {
        public final List<ContextBatchKey> contextBatchKeys;
        public final List<String> webResourceBatchKeys;
        public final Set<String> excludedResolved;

        public Resolved(List<ContextBatchKey> contextBatchKeys, List<String> webResourceBatchKeys, Set<String> excludedResolved) {
            this.contextBatchKeys = contextBatchKeys;
            this.webResourceBatchKeys = webResourceBatchKeys;
            this.excludedResolved = excludedResolved;
        }
    }
}

