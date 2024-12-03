/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.lesscss.spi.DimensionAwareUriResolver
 *  com.atlassian.lesscss.spi.EncodeStateResult
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.atlassian.lesscss.spi.UriResolverStateChangedEvent
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.google.common.base.Joiner
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.less;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.lesscss.spi.DimensionAwareUriResolver;
import com.atlassian.lesscss.spi.EncodeStateResult;
import com.atlassian.lesscss.spi.UriResolver;
import com.atlassian.lesscss.spi.UriResolverStateChangedEvent;
import com.atlassian.plugins.less.DimensionUnawareUriResolverPrebakeError;
import com.atlassian.plugins.less.PreCompilationUtils;
import com.atlassian.plugins.less.PrebakeStateResult;
import com.atlassian.plugins.less.UriDependencyCollector;
import com.atlassian.plugins.less.UriResolverManager;
import com.atlassian.plugins.less.UriStateManager;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingUriStateManager
implements UriStateManager {
    private static final Logger log = LoggerFactory.getLogger(CachingUriStateManager.class);
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final LoadingCache<URI, UriInfo> cache;
    private final EventPublisher eventPublisher;
    private final UriResolverManager uriResolverManager;
    private final UriDependencyCollector uriDependencyCollector;

    public CachingUriStateManager(EventPublisher eventPublisher, UriResolverManager uriResolverManager, UriDependencyCollector uriDependencyCollector) {
        this.eventPublisher = eventPublisher;
        this.uriResolverManager = uriResolverManager;
        this.uriDependencyCollector = uriDependencyCollector;
        this.cache = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<URI, UriInfo>(){

            public UriInfo load(URI uri) throws Exception {
                return CachingUriStateManager.this.computeUriInfo(uri);
            }
        });
    }

    public void registerEventListeners() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void unRegisterEventListeners() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public String getState(URI uri) {
        ArrayList states = Lists.newArrayList();
        this.collectUriState(Sets.newHashSet((Object[])new URI[]{uri}), states, uri, true, arg_0 -> this.cache.getUnchecked(arg_0));
        return Joiner.on((char)',').join((Iterable)states);
    }

    @Override
    public PrebakeStateResult getState(URI uri, Coordinate coord) {
        ArrayList<String> states = new ArrayList<String>();
        ArrayList<PrebakeError> prebakeErrors = new ArrayList<PrebakeError>();
        this.collectUriState(Sets.newHashSet((Object[])new URI[]{uri}), states, uri, true, _uri -> {
            PrebakeComputeUriInfoResult prebakeErrorsAndInfo = this.computeUriInfo((URI)_uri, coord);
            prebakeErrorsAndInfo.prebakeError.ifPresent(prebakeErrors::add);
            return prebakeErrorsAndInfo.uriInfo;
        });
        return new PrebakeStateResult(Joiner.on((char)',').join(states), prebakeErrors);
    }

    @EventListener
    public void onStateChanged(UriResolverStateChangedEvent event) {
        Iterator it = this.cache.asMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            if (!event.hasChanged((URI)entry.getKey())) continue;
            log.debug("LESS has changed. Expiring lastModified cache. uri={}", entry.getKey());
            it.remove();
        }
    }

    private void collectUriState(Set<URI> alreadySeen, List<String> states, URI uri, boolean root, Function<URI, UriInfo> computeUriInfo) {
        UriInfo value = computeUriInfo.apply(uri);
        if (root && value.preCompiledState != null) {
            states.add(value.preCompiledState);
            return;
        }
        states.add(value.state);
        for (URI dependency : value.dependencies) {
            if (!alreadySeen.add(dependency)) continue;
            this.collectUriState(alreadySeen, states, dependency, false, computeUriInfo);
        }
    }

    private UriInfo computeUriInfo(URI uri) {
        log.debug("Computing LESS uri info. uri={}", (Object)uri);
        UriResolver uriResolver = this.uriResolverManager.getResolverOrThrow(uri);
        URI preCompiledUri = PreCompilationUtils.resolvePreCompiledUri(uriResolver, uri);
        Set<URI> dependencies = this.uriDependencyCollector.getDependencies(uri);
        return new UriInfo(dependencies, preCompiledUri == null ? null : uriResolver.encodeState(preCompiledUri), uriResolver.encodeState(uri));
    }

    private PrebakeComputeUriInfoResult computeUriInfo(URI uri, Coordinate coord) {
        log.debug("Computing LESS uri info. uri={}", (Object)uri);
        UriResolver uriResolver = this.uriResolverManager.getResolverOrThrow(uri);
        URI preCompiledUri = PreCompilationUtils.resolvePreCompiledUri(uriResolver, uri);
        Set<URI> dependencies = this.uriDependencyCollector.getDependencies(uri);
        if (preCompiledUri != null) {
            String precompiledState = uriResolver.encodeState(preCompiledUri);
            UriInfo uriInfo = new UriInfo(dependencies, precompiledState, uriResolver.encodeState(uri));
            return new PrebakeComputeUriInfoResult(uriInfo, Optional.empty());
        }
        if (uriResolver instanceof DimensionAwareUriResolver) {
            DimensionAwareUriResolver dimensionAwareUriResolver = (DimensionAwareUriResolver)uriResolver;
            EncodeStateResult encodeStateResult = dimensionAwareUriResolver.encodeState(uri, coord);
            UriInfo uriInfo = new UriInfo(dependencies, null, encodeStateResult.getState());
            return new PrebakeComputeUriInfoResult(uriInfo, encodeStateResult.getPrebakeError());
        }
        String state = uriResolver.encodeState(uri);
        UriInfo uriInfo = new UriInfo(dependencies, null, state);
        DimensionUnawareUriResolverPrebakeError<UriResolver> prebakeError = new DimensionUnawareUriResolverPrebakeError<UriResolver>(uriResolver);
        return new PrebakeComputeUriInfoResult(uriInfo, Optional.of(prebakeError));
    }

    private static class PrebakeComputeUriInfoResult {
        private final UriInfo uriInfo;
        private final Optional<PrebakeError> prebakeError;

        public PrebakeComputeUriInfoResult(UriInfo uriInfo, Optional<PrebakeError> prebakeError) {
            this.uriInfo = uriInfo;
            this.prebakeError = prebakeError;
        }
    }

    private static class UriInfo {
        private final Set<URI> dependencies;
        private final String preCompiledState;
        private final String state;

        private UriInfo(Set<URI> dependencies, String preCompiledState, String state) {
            this.dependencies = dependencies;
            this.preCompiledState = preCompiledState;
            this.state = state;
        }
    }
}

