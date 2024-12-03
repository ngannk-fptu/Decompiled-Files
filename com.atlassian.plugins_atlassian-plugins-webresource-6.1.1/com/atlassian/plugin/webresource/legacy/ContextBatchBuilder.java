/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.legacy.ContextBatch;
import com.atlassian.plugin.webresource.legacy.ContextBatchOperations;
import com.atlassian.plugin.webresource.legacy.InclusionState;
import com.atlassian.plugin.webresource.legacy.ModuleDescriptorStub;
import com.atlassian.plugin.webresource.legacy.PluginResource;
import com.atlassian.plugin.webresource.legacy.ResourceDependencyResolver;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextBatchBuilder {
    private static final Logger log = LoggerFactory.getLogger(ContextBatchBuilder.class);
    private final ResourceDependencyResolver dependencyResolver;
    private final List<String> allIncludedResources = new ArrayList<String>();
    private final Set<String> skippedResources = new HashSet<String>();
    private final boolean resplitMergedContextBatchesForThisRequest;
    private final boolean isSuperBatchingEnabled;
    private final boolean includeDependenciesForFailedUrlReadingConditions;

    public ContextBatchBuilder(ResourceDependencyResolver dependencyResolver, boolean resplitMergedContextBatchesForThisRequest, boolean isSuperBatchingEnabled, boolean includeDependenciesForFailedUrlReadingConditions) {
        this.dependencyResolver = dependencyResolver;
        this.resplitMergedContextBatchesForThisRequest = resplitMergedContextBatchesForThisRequest;
        this.isSuperBatchingEnabled = isSuperBatchingEnabled;
        this.includeDependenciesForFailedUrlReadingConditions = includeDependenciesForFailedUrlReadingConditions;
    }

    public Iterable<PluginResource> buildBatched(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, List<String> includedContexts, Set<String> excludedContexts) {
        return this.buildBatched(requestCache, urlBuildingStrategy, includedContexts, new InclusionState(false, Collections.emptySet(), excludedContexts));
    }

    public Iterable<PluginResource> buildBatched(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, List<String> includedContexts, InclusionState excludedThings) {
        WebResourceKeysToContextBatches includedBatches = WebResourceKeysToContextBatches.create(requestCache, urlBuildingStrategy, includedContexts, this.dependencyResolver, this.isSuperBatchingEnabled, this.includeDependenciesForFailedUrlReadingConditions);
        this.skippedResources.addAll(includedBatches.getSkippedResources());
        if (includedBatches.isEmpty()) {
            return Collections.emptyList();
        }
        HashSet<String> excludedContexts = new HashSet<String>();
        if (excludedThings.contexts != null) {
            excludedContexts.addAll(excludedThings.contexts);
        }
        if (excludedThings.topLevel != null) {
            excludedContexts.addAll(excludedThings.topLevel);
        }
        WebResourceKeysToContextBatches excludedBatches = WebResourceKeysToContextBatches.create(requestCache, urlBuildingStrategy, excludedContexts, this.dependencyResolver, this.isSuperBatchingEnabled, this.includeDependenciesForFailedUrlReadingConditions);
        ArrayList<ContextBatch> batches = new ArrayList<ContextBatch>();
        ArrayList<ContextBatch> batchesToProcess = new ArrayList<ContextBatch>(includedBatches.getContextBatches());
        ContextBatchOperations contextBatchOperations = new ContextBatchOperations();
        while (!batchesToProcess.isEmpty()) {
            List<ContextBatch> subtractedWebResourcesAsContexts;
            ContextBatch contextBatch = (ContextBatch)batchesToProcess.remove(0);
            HashSet<ContextBatch> alreadyProcessedBatches = new HashSet<ContextBatch>();
            alreadyProcessedBatches.add(contextBatch);
            Iterator<ModuleDescriptorStub> resourceIterator = contextBatch.getResources().iterator();
            while (resourceIterator.hasNext()) {
                ModuleDescriptorStub contextResource = resourceIterator.next();
                String resourceKey = contextResource.getCompleteKey();
                List<ContextBatch> additionalContexts = includedBatches.getAdditionalContextsForResourceKey(resourceKey, alreadyProcessedBatches);
                if (!CollectionUtils.isNotEmpty(additionalContexts)) continue;
                if (log.isDebugEnabled()) {
                    for (ContextBatch additional : additionalContexts) {
                        log.debug("Context: {} shares a resource with {}: {}", new Object[]{contextBatch.getKey(), additional.getKey(), contextResource.getCompleteKey()});
                    }
                }
                ArrayList<ContextBatch> contextsToMerge = new ArrayList<ContextBatch>(1 + additionalContexts.size());
                contextsToMerge.add(contextBatch);
                contextsToMerge.addAll(additionalContexts);
                contextBatch = contextBatchOperations.merge(contextsToMerge);
                batchesToProcess.removeAll(additionalContexts);
                alreadyProcessedBatches.addAll(additionalContexts);
                resourceIterator = contextBatch.getResources().iterator();
            }
            HashSet contextResourceKeys = new HashSet();
            contextBatch.getResourceKeys().forEach(contextResourceKeys::add);
            TreeSet subtractedWebResources = new TreeSet();
            if (excludedThings.webresources != null && !excludedThings.webresources.isEmpty() && excludedThings.webresources.containsAll(contextResourceKeys)) {
                log.debug("Context: {} is completely served by previous resource requests", (Object)contextBatch.getKey());
                continue;
            }
            if (excludedThings.topLevel != null) {
                excludedThings.topLevel.stream().filter(contextResourceKeys::contains).forEach(subtractedWebResources::add);
            }
            if (!excludedBatches.isEmpty()) {
                for (ModuleDescriptorStub contextResource : contextBatch.getResources()) {
                    String resourceKey = contextResource.getCompleteKey();
                    List<ContextBatch> excludeContexts = excludedBatches.getContextsForResourceKey(resourceKey);
                    if (excludeContexts.isEmpty()) continue;
                    contextBatch = contextBatchOperations.subtract(contextBatch, excludeContexts);
                }
                this.skippedResources.removeAll(excludedBatches.getSkippedResources());
                excludedBatches.resourceToContextBatches.keySet().forEach(subtractedWebResources::remove);
            }
            if (!(contextBatch = contextBatchOperations.subtract(contextBatch, subtractedWebResourcesAsContexts = subtractedWebResources.stream().map(key -> new ContextBatch(Collections.singletonList(key), Collections.emptyList(), Collections.emptyList(), this.isSuperBatchingEnabled)).collect(Collectors.toList()))).isEmpty()) {
                Iterables.addAll(this.allIncludedResources, contextBatch.getResourceKeys());
                batches.add(contextBatch);
                continue;
            }
            if (!log.isDebugEnabled()) continue;
            log.debug("The context batch {} contains no resources so will be dropped.", (Object)contextBatch.getKey());
        }
        return Iterables.concat((Iterable)Iterables.transform(batches, (Function)new Function<ContextBatch, Iterable<PluginResource>>(){

            public Iterable<PluginResource> apply(ContextBatch batch) {
                return batch.buildPluginResources(ContextBatchBuilder.this.resplitMergedContextBatchesForThisRequest);
            }
        }));
    }

    public Iterable<String> getAllIncludedResources() {
        return this.allIncludedResources;
    }

    public Iterable<String> getSkippedResources() {
        return this.skippedResources;
    }

    private static class WebResourceKeysToContextBatches {
        private final Map<String, List<ContextBatch>> resourceToContextBatches;
        private final List<ContextBatch> knownBatches;
        private final Set<String> skippedResources;

        private WebResourceKeysToContextBatches(Map<String, List<ContextBatch>> resourceKeyToContext, List<ContextBatch> allBatches, Set<String> skippedResources) {
            this.resourceToContextBatches = resourceKeyToContext;
            this.knownBatches = allBatches;
            this.skippedResources = skippedResources;
        }

        static WebResourceKeysToContextBatches create(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, Iterable<String> contexts, ResourceDependencyResolver dependencyResolver, boolean isSuperBatchingEnabled, boolean includeDependenciesForFailedUrlReadingConditions) {
            HashMap<String, List<ContextBatch>> resourceKeyToContext = new HashMap<String, List<ContextBatch>>();
            ArrayList<ContextBatch> batches = new ArrayList<ContextBatch>();
            HashSet<String> skippedResources = new HashSet<String>();
            for (String context : contexts) {
                Iterable<ModuleDescriptorStub> dependencies = dependencyResolver.getDependenciesInContext(requestCache, urlBuildingStrategy, context, skippedResources, includeDependenciesForFailedUrlReadingConditions);
                ContextBatch batch = new ContextBatch(Collections.singletonList(context), null, dependencies, isSuperBatchingEnabled);
                for (ModuleDescriptorStub moduleDescriptor : dependencies) {
                    String key = moduleDescriptor.getCompleteKey();
                    if (!resourceKeyToContext.containsKey(key)) {
                        resourceKeyToContext.put(key, new ArrayList());
                    }
                    ((List)resourceKeyToContext.get(key)).add(batch);
                    if (batches.contains(batch)) continue;
                    batches.add(batch);
                }
            }
            return new WebResourceKeysToContextBatches(resourceKeyToContext, batches, skippedResources);
        }

        boolean isEmpty() {
            return this.knownBatches.isEmpty();
        }

        List<ContextBatch> getContextsForResourceKey(String key) {
            return this.getAdditionalContextsForResourceKey(key, null);
        }

        List<ContextBatch> getAdditionalContextsForResourceKey(String key, Collection<ContextBatch> knownContexts) {
            List<ContextBatch> allContexts = this.resourceToContextBatches.get(key);
            if (CollectionUtils.isEmpty(allContexts)) {
                return Collections.emptyList();
            }
            LinkedHashSet<ContextBatch> contexts = new LinkedHashSet<ContextBatch>(allContexts);
            if (CollectionUtils.isNotEmpty(knownContexts)) {
                contexts.removeAll(knownContexts);
            }
            return new ArrayList<ContextBatch>(contexts);
        }

        List<ContextBatch> getContextBatches() {
            return new ArrayList<ContextBatch>(this.knownBatches);
        }

        public Set<String> getSkippedResources() {
            return this.skippedResources;
        }
    }
}

