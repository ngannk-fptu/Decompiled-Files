/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.graph;

import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.plugin.webresource.graph.DependencyGraph;
import com.atlassian.plugin.webresource.graph.RequestableKeyValidator;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.plugin.webresource.models.RootPageKey;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyGraphBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyGraphBuilder.class);
    private final Set<String> rootPageKeys;
    private final DependencyGraph<Requestable> resourceDependencyGraph = new DependencyGraph<Requestable>(Requestable.class);

    DependencyGraphBuilder() {
        this.rootPageKeys = new HashSet<String>();
    }

    @Nonnull
    public DependencyGraphBuilder addDependencies(@Nonnull WebResourceModuleDescriptor webResourceModuleDescriptor) {
        WebResourceKey resourceKey;
        Objects.requireNonNull(webResourceModuleDescriptor, "The web resource module descriptor is mandatory.");
        String completeKey = webResourceModuleDescriptor.getCompleteKey();
        if (webResourceModuleDescriptor.isRootPage()) {
            this.rootPageKeys.add(completeKey);
            resourceKey = new RootPageKey(completeKey);
            this.addWebResourceContextDependencies(resourceKey, webResourceModuleDescriptor.getContextDependencies());
        } else {
            resourceKey = new WebResourceKey(completeKey);
        }
        this.addWebResourceContexts(resourceKey, webResourceModuleDescriptor.getContexts());
        this.addWebResourceDependencies(resourceKey, webResourceModuleDescriptor.getDependencies());
        return this;
    }

    @Nonnull
    public DependencyGraphBuilder addDependencies(@Nonnull Collection<WebResourceModuleDescriptor> webResourceModuleDescriptors) {
        Objects.requireNonNull(webResourceModuleDescriptors, "The web resource module descriptors are mandatory.");
        webResourceModuleDescriptors.stream().filter(Objects::nonNull).forEach(this::addDependencies);
        return this;
    }

    @Nonnull
    public DependencyGraphBuilder addWebResourceDependency(@Nonnull Requestable requestable, @Nonnull String webResourceDependency) {
        Objects.requireNonNull(requestable, "The requestable key is mandatory.");
        Objects.requireNonNull(webResourceDependency, "The web resource dependency is mandatory.");
        this.resourceDependencyGraph.addDependency(requestable, new WebResourceKey(webResourceDependency));
        return this;
    }

    @Nonnull
    public DependencyGraph<Requestable> build() {
        return this.resourceDependencyGraph;
    }

    private void addWebResourceDependencies(Requestable requestable, Collection<String> dependencies) {
        RequestableKeyValidator webResourceKeyValidator = new RequestableKeyValidator(this.rootPageKeys);
        Collection webResourceDependencyKeys = dependencies.stream().filter(webResourceKeyValidator::isWebResource).map(WebResourceKey::new).collect(Collectors.toSet());
        this.resourceDependencyGraph.addDependencies(requestable, webResourceDependencyKeys);
    }

    private void addWebResourceContexts(Requestable requestable, Collection<String> contexts) {
        if (requestable instanceof WebResourceContextKey) {
            String message = String.format("Ignoring contexts for '%s': a context cannot depend on other contexts.", requestable.getKey());
            LOGGER.debug(message);
            return;
        }
        contexts.stream().filter(RequestableKeyValidator::isWebResourceContext).map(WebResourceContextKey::new).forEach(ctx -> this.resourceDependencyGraph.addDependency((Requestable)ctx, requestable));
    }

    private void addWebResourceContextDependencies(Requestable requestable, Collection<String> contextDependencies) {
        if (!(requestable instanceof RootPageKey)) {
            String message = String.format("Ignoring context dependencies for '%s': context dependencies are only supported in root-pages at the moment.", requestable.getKey());
            LOGGER.debug(message);
            return;
        }
        Collection webResourceDependencyKeys = contextDependencies.stream().filter(RequestableKeyValidator::isWebResourceContext).map(WebResourceContextKey::new).collect(Collectors.toSet());
        this.resourceDependencyGraph.addDependencies(requestable, webResourceDependencyKeys);
    }
}

