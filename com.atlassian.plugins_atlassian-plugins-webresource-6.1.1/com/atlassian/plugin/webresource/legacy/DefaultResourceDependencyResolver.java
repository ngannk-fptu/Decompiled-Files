/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.legacy.ModuleDescriptorStub;
import com.atlassian.plugin.webresource.legacy.ResourceDependencyResolver;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultResourceDependencyResolver
implements ResourceDependencyResolver {
    private static final Logger log = LoggerFactory.getLogger(DefaultResourceDependencyResolver.class);
    private final WebResourceIntegration webResourceIntegration;
    private final Cache cached = new Cache();
    private final boolean isSuperBatchingEnabled;
    private final List<String> superBatchModuleCompleteKeys;
    private final Globals globals;

    public DefaultResourceDependencyResolver(Globals globals, WebResourceIntegration webResourceIntegration, boolean isSuperBatchingEnabled, List<String> superBatchModuleCompleteKeys) {
        this.globals = globals;
        this.webResourceIntegration = webResourceIntegration;
        this.isSuperBatchingEnabled = isSuperBatchingEnabled;
        this.superBatchModuleCompleteKeys = superBatchModuleCompleteKeys;
    }

    @Override
    public Iterable<ModuleDescriptorStub> getSuperBatchDependencies() {
        return this.cached.resourceMap().values();
    }

    private Iterable<String> getSuperBatchDependencyKeys() {
        return this.cached.resourceMap().keySet();
    }

    @Override
    public Iterable<ModuleDescriptorStub> getDependencies(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, String moduleKey, boolean excludeSuperBatchedResources, boolean includeDependenciesForFailedUrlReadingConditions) {
        LinkedHashMap<String, ModuleDescriptorStub> orderedResources = new LinkedHashMap<String, ModuleDescriptorStub>();
        List<String> superBatchResources = excludeSuperBatchedResources ? this.getSuperBatchDependencyKeys() : Collections.emptyList();
        this.resolveDependencies(requestCache, urlBuildingStrategy, moduleKey, orderedResources, superBatchResources, new Stack<String>(), null, includeDependenciesForFailedUrlReadingConditions);
        return orderedResources.values();
    }

    @Override
    public Iterable<ModuleDescriptorStub> getDependenciesInContext(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, String context, Set<String> skippedResources, boolean includeDependenciesForFailedUrlReadingConditions) {
        return this.getDependenciesInContext(requestCache, urlBuildingStrategy, context, true, skippedResources, includeDependenciesForFailedUrlReadingConditions);
    }

    public Iterable<ModuleDescriptorStub> getDependenciesInContext(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, String context, boolean excludeSuperBatchedResources, Set<String> skippedResources, boolean includeDependenciesForFailedUrlReadingConditions) {
        LinkedHashSet<ModuleDescriptorStub> contextResources = new LinkedHashSet<ModuleDescriptorStub>();
        Class<WebResourceModuleDescriptor> clazz = WebResourceModuleDescriptor.class;
        ArrayList<String> contextDependencies = new ArrayList<String>();
        for (WebResourceModuleDescriptor descriptor : this.webResourceIntegration.getPluginAccessor().getEnabledModuleDescriptorsByClass(clazz)) {
            if (descriptor == null || !descriptor.getContexts().contains(context)) continue;
            contextDependencies.add(descriptor.getCompleteKey());
        }
        Bundle bundle = requestCache.getSnapshot().get("_context:" + context);
        if (bundle != null) {
            contextDependencies.addAll(bundle.getDependencies());
        }
        for (String key : contextDependencies) {
            LinkedHashMap<String, ModuleDescriptorStub> dependencies = new LinkedHashMap<String, ModuleDescriptorStub>();
            List<String> superBatchResources = excludeSuperBatchedResources ? this.getSuperBatchDependencyKeys() : Collections.emptyList();
            this.resolveDependencies(requestCache, urlBuildingStrategy, key, dependencies, superBatchResources, new Stack<String>(), skippedResources, includeDependenciesForFailedUrlReadingConditions);
            for (ModuleDescriptorStub dependency : dependencies.values()) {
                contextResources.add(dependency);
            }
        }
        return Collections.unmodifiableCollection(contextResources);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void resolveDependencies(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, String moduleKey, Map<String, ModuleDescriptorStub> orderedResourceKeys, Iterable<String> superBatchResources, Stack<String> stack, Set<String> skippedResources, boolean includeDependenciesForFailedUrlReadingConditions) {
        CachedCondition condition;
        Bundle bundle;
        ModuleDescriptor moduleDescriptor;
        if (Iterables.contains(superBatchResources, (Object)moduleKey)) {
            log.debug("Not requiring web-resource `{}` because it is part of a super-batch\nInclusion stack: {}", (Object)moduleKey, stack);
            return;
        }
        if (stack.contains(moduleKey)) {
            log.debug("Cyclic web-resource dependency detected with `{}`\nInclusion stack: {}", (Object)moduleKey, stack);
            return;
        }
        try {
            moduleDescriptor = this.webResourceIntegration.getPluginAccessor().getEnabledPluginModule(moduleKey);
        }
        catch (IllegalArgumentException e) {
            log.debug("Cannot find web-resource with key `{}`\nInclusion stack: {}", (Object)moduleKey, stack);
            return;
        }
        if (moduleDescriptor == null) {
            return;
        }
        if (!(moduleDescriptor instanceof WebResourceModuleDescriptor)) {
            if (log.isDebugEnabled()) {
                if (this.webResourceIntegration.getPluginAccessor().getPluginModule(moduleKey) != null) {
                    log.debug("Cannot include disabled web-resource module `{}`\nInclusion stack: {}", (Object)moduleKey, stack);
                } else {
                    log.debug("Cannot find web-resource module for `{}`\nInclusion stack: {}", (Object)moduleKey, stack);
                }
            }
            return;
        }
        WebResourceModuleDescriptor webResourceModuleDescriptor = (WebResourceModuleDescriptor)moduleDescriptor;
        boolean skipDependencies = false;
        if (!webResourceModuleDescriptor.canEncodeStateIntoUrl()) {
            if (null != skippedResources) {
                skippedResources.add(moduleKey);
                return;
            }
            if (!webResourceModuleDescriptor.shouldDisplayImmediate()) {
                log.debug("Not including web-resource module `{}`, adding to skipped list: {}\nInclusion stack: {}", new Object[]{webResourceModuleDescriptor.getCompleteKey(), skippedResources, stack});
                return;
            }
        } else if (!includeDependenciesForFailedUrlReadingConditions && (bundle = requestCache.getSnapshot().get(webResourceModuleDescriptor.getCompleteKey())) != null && (condition = bundle.getCondition()) != null) {
            boolean conditionResult;
            skipDependencies = conditionResult = !condition.evaluateSafely(requestCache, urlBuildingStrategy);
            log.debug("Condition for web-resource {} evaluated; updating skipDependencies from {} to {}", new Object[]{webResourceModuleDescriptor.getCompleteKey(), skipDependencies, conditionResult});
        }
        List<String> dependencies = webResourceModuleDescriptor.getDependencies();
        log.debug("About to add web-resource `{}` and its dependencies: {} and skipDependencies is {}\nInclusion stack: {}", new Object[]{moduleKey, dependencies, skipDependencies, stack});
        stack.push(moduleKey);
        try {
            if (!skipDependencies) {
                for (String dependency : dependencies) {
                    if (orderedResourceKeys.get(dependency) != null) continue;
                    this.resolveDependencies(requestCache, urlBuildingStrategy, dependency, orderedResourceKeys, superBatchResources, stack, skippedResources, includeDependenciesForFailedUrlReadingConditions);
                }
            }
        }
        finally {
            log.debug("Finished adding web-resource `{}` and its dependencies\nInclusion stack: {}", (Object)moduleKey, stack);
            stack.pop();
        }
        orderedResourceKeys.put(moduleKey, new ModuleDescriptorStub(webResourceModuleDescriptor));
    }

    final class Cache {
        ResettableLazyReference<SuperBatch> lazy = new ResettableLazyReference<SuperBatch>(){

            protected SuperBatch create() throws Exception {
                String version = DefaultResourceDependencyResolver.this.webResourceIntegration.getSuperBatchVersion();
                return new SuperBatch(version, this.loadDescriptors(DefaultResourceDependencyResolver.this.superBatchModuleCompleteKeys));
            }

            Map<String, ModuleDescriptorStub> loadDescriptors(Iterable<String> moduleKeys) {
                if (Iterables.isEmpty(moduleKeys)) {
                    return Collections.emptyMap();
                }
                LinkedHashMap resources = new LinkedHashMap();
                for (String moduleKey : moduleKeys) {
                    RequestCache requestCache = new RequestCache(DefaultResourceDependencyResolver.this.globals);
                    UrlBuildingStrategy normalStrategy = UrlBuildingStrategy.normal();
                    DefaultResourceDependencyResolver.this.resolveDependencies(requestCache, normalStrategy, moduleKey, resources, Collections.emptyList(), new Stack(), null, true);
                }
                return Collections.unmodifiableMap(resources);
            }
        };

        Cache() {
        }

        Map<String, ModuleDescriptorStub> resourceMap() {
            if (!DefaultResourceDependencyResolver.this.isSuperBatchingEnabled) {
                log.debug("Super batching not enabled, but getSuperBatchDependencies() called. Returning empty.");
                return Collections.emptyMap();
            }
            while (true) {
                SuperBatch batch = (SuperBatch)this.lazy.get();
                if (batch.version.equals(DefaultResourceDependencyResolver.this.webResourceIntegration.getSuperBatchVersion())) {
                    return batch.resources;
                }
                this.lazy.reset();
            }
        }
    }

    static final class SuperBatch {
        final String version;
        final Map<String, ModuleDescriptorStub> resources;

        SuperBatch(String version, Map<String, ModuleDescriptorStub> resources) {
            this.version = (String)Preconditions.checkNotNull((Object)version);
            this.resources = (Map)Preconditions.checkNotNull(resources);
        }
    }
}

