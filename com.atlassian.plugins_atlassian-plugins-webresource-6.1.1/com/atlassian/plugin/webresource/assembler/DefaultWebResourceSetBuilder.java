/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  com.atlassian.webresource.api.data.PluginDataResource
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.webresource.assembler.DefaultWebResourceSet;
import com.atlassian.plugin.webresource.assembler.WebResourceInformation;
import com.atlassian.plugin.webresource.bigpipe.BigPipe;
import com.atlassian.plugin.webresource.bigpipe.KeyedValue;
import com.atlassian.plugin.webresource.data.DefaultPluginDataResource;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.SuperbatchConfiguration;
import com.atlassian.plugin.webresource.impl.helpers.ResourceGenerationInfo;
import com.atlassian.plugin.webresource.impl.helpers.data.ResourceDataGenerator;
import com.atlassian.plugin.webresource.impl.helpers.url.Resolved;
import com.atlassian.plugin.webresource.impl.helpers.url.ResourceUrlGenerator;
import com.atlassian.plugin.webresource.models.SuperBatchKey;
import com.atlassian.plugin.webresource.models.SyncBatchKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.api.data.PluginDataResource;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebResourceSetBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWebResourceSetBuilder.class);
    @VisibleForTesting
    static final WebResourceKey WEB_RESOURCE_MANAGER_RESOURCE = new WebResourceKey("com.atlassian.plugins.atlassian-plugins-webresource-rest:web-resource-manager");
    @VisibleForTesting
    static final WebResourceKey DATA_COLLECTOR_OBSERVER_RESOURCE = new WebResourceKey("com.atlassian.plugins.atlassian-plugins-webresource-rest:data-collector-perf-observer");
    @VisibleForTesting
    static final WebResourceKey DATA_COLLECTOR_ASYNC_RESOURCE = new WebResourceKey("com.atlassian.plugins.atlassian-plugins-webresource-rest:data-collector-async");
    private final RequestState requestState;
    private boolean blockOnBigPipe;
    private boolean cleanUpAfterInclude;
    private boolean drainBigPipe;
    private boolean addWebResourceJavascriptApiDependencies;
    private boolean isSuperbatchEnabled = false;

    public DefaultWebResourceSetBuilder(@Nonnull RequestState requestState) {
        this.requestState = Objects.requireNonNull(requestState, "The request state is mandatory for building the DefaultWebResourceSetBuilder.");
        this.blockOnBigPipe = false;
        this.cleanUpAfterInclude = false;
        this.drainBigPipe = false;
        this.addWebResourceJavascriptApiDependencies = true;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder disableBlockOnBigPipe() {
        this.blockOnBigPipe = false;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder enableBlockOnBigPipe() {
        this.blockOnBigPipe = true;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder disableCleanUpAfterInclude() {
        this.cleanUpAfterInclude = false;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder enableCleanUpAfterInclude() {
        this.cleanUpAfterInclude = true;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder disableDrainBigPipe() {
        this.drainBigPipe = false;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder enableDrainBigPipe() {
        this.drainBigPipe = true;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder disableAdditionOfWebResourceJavascriptApiDependencies() {
        this.addWebResourceJavascriptApiDependencies = false;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder enableAdditionOfWebResourceJavascriptApiDependencies() {
        this.addWebResourceJavascriptApiDependencies = true;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder enableSuperbatch() {
        this.isSuperbatchEnabled = true;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSetBuilder disableSuperbatch() {
        this.isSuperbatchEnabled = false;
        return this;
    }

    @Nonnull
    public DefaultWebResourceSet build() {
        SuperbatchConfiguration superbatchConfiguration = this.requestState.getSuperbatchConfiguration();
        if (this.isSuperbatchEnabled && superbatchConfiguration.isEnabled()) {
            this.requestState.getRawRequest().includeFirst(superbatchConfiguration.getResourcePhase(), SuperBatchKey.getInstance());
        }
        if (this.requestState.isSyncbatchEnabled()) {
            this.requestState.getRawRequest().includeFirst(ResourcePhase.INLINE, SyncBatchKey.getInstance());
        }
        if (this.addWebResourceJavascriptApiDependencies && this.requestState.isAutoIncludeFrontendRuntimeEnabled()) {
            if (this.requestState.getGlobals().getConfig().isPerformanceTrackingEnabled()) {
                this.requestState.getRawRequest().include(ResourcePhase.INLINE, DATA_COLLECTOR_OBSERVER_RESOURCE);
                this.requestState.getRawRequest().include(ResourcePhase.INTERACTION, DATA_COLLECTOR_ASYNC_RESOURCE);
            }
            if (this.requestState.getRawRequest().hasAny(ResourcePhase.INTERACTION)) {
                this.requestState.getRawRequest().includeFirst(superbatchConfiguration.getResourcePhase(), WEB_RESOURCE_MANAGER_RESOURCE);
            }
        }
        Deque webResourceInformation = Stream.of(ResourcePhase.values()).map(this::buildDependenciesByPhase).collect(Collectors.toCollection(ArrayDeque::new));
        return new DefaultWebResourceSet(this.requestState.getBigPipe().isComplete(), this.requestState.getGlobals().getConfig(), this.requestState, webResourceInformation);
    }

    private WebResourceInformation buildDependenciesByPhase(ResourcePhase resourcePhase) {
        ResourceGenerationInfo resourceGenerationInfo = new ResourceGenerationInfo(resourcePhase, this.requestState);
        Resolved resolved = new ResourceUrlGenerator(this.requestState.getGlobals().getUrlCache()).generate(resourceGenerationInfo);
        Set<PluginDataResource> pluginDataResources = new ResourceDataGenerator().generate(resourceGenerationInfo);
        if (this.drainBigPipe) {
            for (KeyedValue<String, Jsonable> drainedData : this.drainBigPipe(this.blockOnBigPipe)) {
                drainedData.value().left().forEach(exception -> LOGGER.error("Error generating big pipe content for '{}': {}", new Object[]{drainedData.key(), exception.getMessage(), exception}));
                if (drainedData.value().isRight()) {
                    pluginDataResources.add(new DefaultPluginDataResource(drainedData.key(), (Jsonable)drainedData.value().right().get(), resourcePhase));
                    continue;
                }
                pluginDataResources.add(new DefaultPluginDataResource(drainedData.key(), Optional.empty(), resourcePhase));
            }
        }
        if (this.cleanUpAfterInclude) {
            this.requestState.clearIncludedAndUpdateExcluded(resourcePhase, resolved.getExcludedResolved());
        }
        return new WebResourceInformation(pluginDataResources, resourcePhase, resolved.getUrls());
    }

    private Iterable<KeyedValue<String, Jsonable>> drainBigPipe(boolean blockOnBigPipe) {
        try {
            boolean isDeadlineExceeded;
            BigPipe bigPipe = this.requestState.getBigPipe();
            boolean isBigPipeDeadlineDisabled = this.requestState.getGlobals().getConfig().getBigPipeDeadlineDisabled();
            if (isBigPipeDeadlineDisabled) {
                if (blockOnBigPipe && bigPipe.isNotComplete()) {
                    return bigPipe.waitForContent();
                }
                return bigPipe.getAvailableContent();
            }
            long waitedTime = this.requestState.getBigPipeDeadline() - System.currentTimeMillis();
            boolean bl = isDeadlineExceeded = waitedTime <= 0L;
            if (isDeadlineExceeded) {
                return bigPipe.forceCompleteAll();
            }
            if (blockOnBigPipe && bigPipe.isNotComplete()) {
                return bigPipe.waitForContent(waitedTime, TimeUnit.MILLISECONDS);
            }
            return bigPipe.getAvailableContent();
        }
        catch (InterruptedException exception) {
            LOGGER.info("Interrupted while waiting for big pipe", (Throwable)exception);
            return Collections.emptyList();
        }
    }
}

