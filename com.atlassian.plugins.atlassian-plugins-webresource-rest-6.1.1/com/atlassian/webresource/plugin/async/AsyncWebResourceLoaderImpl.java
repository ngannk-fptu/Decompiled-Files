/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.impl.config.Config
 *  com.atlassian.plugin.webresource.models.Requestable
 *  com.atlassian.plugin.webresource.models.WebResourceContextKey
 *  com.atlassian.plugin.webresource.models.WebResourceKey
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webresource.plugin.async;

import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.plugin.async.AsyncWebResourceLoader;
import com.atlassian.webresource.plugin.async.model.ResourcesAndData;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncWebResourceLoaderImpl
implements AsyncWebResourceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncWebResourceLoaderImpl.class);
    private final WebResourceAssemblerFactory webResourceAssemblerFactory;

    public AsyncWebResourceLoaderImpl(@Nonnull WebResourceAssemblerFactory webResourceAssemblerFactory) {
        this.webResourceAssemblerFactory = Objects.requireNonNull(webResourceAssemblerFactory);
    }

    @Override
    @Nonnull
    public ResourcesAndData resolve(@Nonnull Map<ResourcePhase, Set<WebResourceKey>> webResourcesByPhase, @Nonnull Map<ResourcePhase, Set<WebResourceContextKey>> contextsByPhase, @Nonnull Set<WebResourceKey> excludeResources, @Nonnull Set<WebResourceContextKey> excludeContexts) throws IOException {
        WebResourceSet webResourceSet = this.resolveWebResourceSet(webResourcesByPhase, contextsByPhase, excludeResources, excludeContexts);
        ResourcesAndData resourcesAndData = new ResourcesAndData(webResourceSet.getResources());
        while (!webResourceSet.isComplete()) {
            resourcesAndData.merge(webResourceSet.getResources());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AsyncWebResourceLoaderImpl.resolve Returning async resource response: {}", (Object)resourcesAndData);
        }
        return resourcesAndData;
    }

    private WebResourceSet resolveWebResourceSet(Map<ResourcePhase, Set<WebResourceKey>> webResourcesByPhase, Map<ResourcePhase, Set<WebResourceContextKey>> contextsByPhase, Set<WebResourceKey> excludeResources, Set<WebResourceContextKey> excludeContexts) {
        if (LOGGER.isDebugEnabled()) {
            webResourcesByPhase.forEach((resourcePhase, webResourceKeys) -> LOGGER.debug("Requiring webresources with phase {} : {}", resourcePhase, webResourceKeys));
            contextsByPhase.forEach((resourcePhase, webResourceContextKeys) -> LOGGER.debug("Requiring contexts with phase {} : {}", resourcePhase, webResourceContextKeys));
            LOGGER.debug("Excluding webresources {}", excludeResources);
            LOGGER.debug("Excluding contexts {}", excludeContexts);
        }
        WebResourceAssembler assembler = this.webResourceAssemblerFactory.create().includeSuperbatchResources(false).build();
        assembler.resources().exclude(excludeResources.stream().map(Requestable::toLooseType).collect(Collectors.toSet()), excludeContexts.stream().map(Requestable::toLooseType).collect(Collectors.toSet()));
        webResourcesByPhase.forEach((resourcePhase, webResourceKeys) -> {
            for (WebResourceKey webResource : webResourceKeys) {
                if (Config.isWebResourceKey((String)webResource.toLooseType())) {
                    assembler.resources().requireWebResource(resourcePhase, webResource.toLooseType());
                    continue;
                }
                assembler.resources().requireModule(resourcePhase, webResource.toLooseType());
            }
        });
        contextsByPhase.forEach((resourcePhase, webResourceContextKeys) -> webResourceContextKeys.forEach(webResourceContextKey -> assembler.resources().requireContext(resourcePhase, webResourceContextKey.toLooseType())));
        return assembler.assembled().pollIncludedResources();
    }
}

