/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.assembler.LegacyPageBuilderService
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.atlassian.webresource.api.assembler.AssembledResources
 *  com.atlassian.webresource.api.assembler.RequiredData
 *  com.atlassian.webresource.api.assembler.RequiredResources
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.plugin.webresource.assembler.LegacyPageBuilderService;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.webresource.api.assembler.AssembledResources;
import com.atlassian.webresource.api.assembler.RequiredData;
import com.atlassian.webresource.api.assembler.RequiredResources;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.atlassian.fugue.Pair;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadlocalWebResourceDependenciesRecorder
implements WebResourceDependenciesRecorder {
    private static final Logger log = LoggerFactory.getLogger(ThreadlocalWebResourceDependenciesRecorder.class);
    private final LegacyPageBuilderService pageBuilderService;
    private final ThreadLocal<RecordingWebResourceAssembler> assemblerStorage = new ThreadLocal();
    private final WebResourceAssemblerFactory webResourceAssemblerFactory;

    public ThreadlocalWebResourceDependenciesRecorder(WebResourceAssemblerFactory webResourceAssemblerFactory, LegacyPageBuilderService pageBuilderService) {
        this.pageBuilderService = Objects.requireNonNull(pageBuilderService);
        this.webResourceAssemblerFactory = Objects.requireNonNull(webResourceAssemblerFactory);
    }

    @Override
    public <T> Pair<T, WebResourceDependenciesRecorder.RecordedResources> recordResources(Iterable<String> additionalContexts, Iterable<String> additionalResources, Callable<T> callback) throws Exception {
        return this.recordResources(additionalContexts, additionalResources, true, callback);
    }

    @Override
    public <T> Pair<T, WebResourceDependenciesRecorder.RecordedResources> recordResources(Iterable<String> additionalContexts, Iterable<String> additionalResources, boolean includeSuperbatch, Callable<T> callback) throws Exception {
        return this.recordResources(additionalContexts, additionalResources, Collections.emptyList(), Collections.emptyList(), includeSuperbatch, callback);
    }

    @Override
    public <T> Pair<T, WebResourceDependenciesRecorder.RecordedResources> recordResources(Iterable<String> additionalContexts, Iterable<String> additionalResources, Iterable<String> excludeContexts, Iterable<String> excludeResources, boolean includeSuperbatch, Callable<T> callback) throws Exception {
        try {
            Pair pair;
            block11: {
                Ticker ignored = Timers.start((String)(ThreadlocalWebResourceDependenciesRecorder.class.getSimpleName() + ".record()"));
                try {
                    log.debug("start recording webresource dependencies");
                    WebResourceAssemblerBuilder builder = this.webResourceAssemblerFactory.create();
                    builder.includeSuperbatchResources(includeSuperbatch);
                    RecordingWebResourceAssembler assembler = new RecordingWebResourceAssembler(builder.build());
                    this.assemblerStorage.set(assembler);
                    this.pageBuilderService.clearRequestLocal();
                    this.pageBuilderService.seed((WebResourceAssembler)assembler);
                    for (String contextOverride : additionalContexts) {
                        assembler.resources().requireContext(contextOverride);
                    }
                    for (String resourceOverride : additionalResources) {
                        assembler.resources().requireWebResource(resourceOverride);
                    }
                    assembler.resources().exclude((Set)Sets.newHashSet(excludeResources), (Set)Sets.newHashSet(excludeContexts));
                    T result = callback.call();
                    pair = Pair.pair(result, (Object)new CachingRecordedResources(assembler));
                    if (ignored == null) break block11;
                }
                catch (Throwable throwable) {
                    if (ignored != null) {
                        try {
                            ignored.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ignored.close();
            }
            return pair;
        }
        finally {
            this.pageBuilderService.clearRequestLocal();
            this.assemblerStorage.remove();
            log.debug("ended recording webresource dependencies");
        }
    }

    @Override
    public <T> Pair<T, WebResourceDependenciesRecorder.RecordedResources> recordResources(Callable<T> callback) throws Exception {
        return this.recordResources(Collections.emptyList(), Collections.emptyList(), callback);
    }

    private static class RecordingWebResourceAssembler
    implements WebResourceAssembler {
        private final WebResourceAssembler delegate;
        private final LinkedHashMap<String, ResourcePhase> contexts = Maps.newLinkedHashMap();
        private final LinkedHashMap<String, ResourcePhase> webResources = Maps.newLinkedHashMap();

        RecordingWebResourceAssembler(WebResourceAssembler delegate) {
            this.delegate = delegate;
        }

        public AssembledResources assembled() {
            return this.delegate.assembled();
        }

        public WebResourceAssembler copy() {
            return this.delegate.copy();
        }

        public RequiredData data() {
            return this.delegate.data();
        }

        public RequiredResources resources() {
            return new RecordingRequiredResources(this.delegate.resources());
        }

        private class RecordingRequiredResources
        implements RequiredResources {
            private final RequiredResources realImplementation;

            RecordingRequiredResources(RequiredResources resources) {
                this.realImplementation = resources;
            }

            @Nonnull
            public RequiredResources exclude(@Nullable Set<String> webResource, @Nullable Set<String> context) {
                if (context != null) {
                    context.forEach(RecordingWebResourceAssembler.this.contexts::remove);
                }
                if (webResource != null) {
                    webResource.forEach(RecordingWebResourceAssembler.this.webResources::remove);
                }
                this.realImplementation.exclude(webResource, context);
                return this;
            }

            @Nonnull
            public RequiredResources requireContext(@Nonnull String context) {
                return this.requireContext(ResourcePhase.defaultPhase(), context);
            }

            @Nonnull
            public RequiredResources requireContext(@Nonnull ResourcePhase phase, @Nonnull String context) {
                RecordingWebResourceAssembler.this.contexts.put(context, phase);
                this.realImplementation.requireContext(phase, context);
                return this;
            }

            @Nonnull
            public RequiredResources requireWebResource(@Nonnull String webResource) {
                return this.requireWebResource(ResourcePhase.defaultPhase(), webResource);
            }

            @Nonnull
            public RequiredResources requireWebResource(@Nonnull ResourcePhase phase, @Nonnull String webResource) {
                RecordingWebResourceAssembler.this.webResources.put(webResource, phase);
                this.realImplementation.requireWebResource(webResource);
                return this;
            }

            @Nonnull
            public RequiredResources requireModule(@Nonnull String module) {
                return this.requireModule(ResourcePhase.defaultPhase(), module);
            }

            @Nonnull
            public RequiredResources requireModule(@Nonnull ResourcePhase phase, @Nonnull String s) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Nonnull
            public RequiredResources requirePage(@Nonnull String page) {
                return this.requirePage(ResourcePhase.defaultPhase(), page);
            }

            @Nonnull
            public RequiredResources requirePage(@Nonnull ResourcePhase phase, @Nonnull String page) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Nonnull
            public RequiredResources excludeSuperbatch() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Nonnull
            public RequiredResources requireSuperbatch(@Nonnull ResourcePhase resourcePhase) {
                throw new UnsupportedOperationException("Not implemented yet");
            }
        }
    }

    private class CachingRecordedResources
    implements WebResourceDependenciesRecorder.RecordedResources {
        private final LazyReference<WebResourceSet> superbatchResourceCache;
        private final LazyReference<WebResourceSet> resourcesCache;
        private final RecordingWebResourceAssembler assembler;

        CachingRecordedResources(final RecordingWebResourceAssembler assembler) {
            this.assembler = assembler;
            this.superbatchResourceCache = new LazyReference<WebResourceSet>(){

                protected WebResourceSet create() throws Exception {
                    try {
                        WebResourceSet webResourceSet;
                        block9: {
                            Ticker ignored = Timers.start((String)(ThreadlocalWebResourceDependenciesRecorder.class.getSimpleName() + ".superbatch.create()"));
                            try {
                                log.trace("start superbatch webresource dependencies calculation");
                                WebResourceAssemblerBuilder superBatchBuilder = ThreadlocalWebResourceDependenciesRecorder.this.webResourceAssemblerFactory.create();
                                superBatchBuilder.includeSuperbatchResources(true);
                                WebResourceAssembler superBatchAssembler = superBatchBuilder.build();
                                webResourceSet = superBatchAssembler.assembled().drainIncludedResources();
                                if (ignored == null) break block9;
                            }
                            catch (Throwable throwable) {
                                if (ignored != null) {
                                    try {
                                        ignored.close();
                                    }
                                    catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            }
                            ignored.close();
                        }
                        return webResourceSet;
                    }
                    finally {
                        log.trace("finish superbatch webresource dependencies calculation");
                    }
                }
            };
            this.resourcesCache = new LazyReference<WebResourceSet>(){

                protected WebResourceSet create() throws Exception {
                    return assembler.assembled().drainIncludedResources();
                }
            };
        }

        @Override
        public Supplier<WebResourceSet> webresources() {
            return () -> this.resourcesCache.get();
        }

        @Override
        public Iterable<String> contexts() {
            return ImmutableList.copyOf(this.assembler.contexts.keySet());
        }

        @Override
        public Iterable<String> resourceKeys() {
            return ImmutableList.copyOf(this.assembler.webResources.keySet());
        }

        @Override
        public Supplier<WebResourceSet> superbatch() {
            return () -> this.superbatchResourceCache.get();
        }
    }
}

