/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.util.LastModifiedHandler
 *  com.atlassian.sourcemap.ReadableSourceMap
 *  com.atlassian.sourcemap.Util
 *  com.atlassian.sourcemap.WritableSourceMap
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.impl.http;

import com.atlassian.plugin.cache.filecache.Cache;
import com.atlassian.plugin.cache.filecache.impl.PassThroughCache;
import com.atlassian.plugin.servlet.util.LastModifiedHandler;
import com.atlassian.plugin.webresource.ResourceUtils;
import com.atlassian.plugin.webresource.analytics.EventFiringHelper;
import com.atlassian.plugin.webresource.analytics.events.InvalidBundleHashEvent;
import com.atlassian.plugin.webresource.analytics.events.RequestServingCacheEvent;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.discovery.BundleFinder;
import com.atlassian.plugin.webresource.impl.discovery.ResourceFinder;
import com.atlassian.plugin.webresource.impl.helpers.BaseHelpers;
import com.atlassian.plugin.webresource.impl.helpers.Helpers;
import com.atlassian.plugin.webresource.impl.helpers.ResourceServingHelpers;
import com.atlassian.plugin.webresource.impl.helpers.url.UrlGenerationHelpers;
import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.impl.support.ContentImpl;
import com.atlassian.plugin.webresource.impl.support.LineCountingProxyOutputStream;
import com.atlassian.plugin.webresource.impl.support.NullOutputStream;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.impl.support.http.BaseController;
import com.atlassian.plugin.webresource.impl.support.http.Request;
import com.atlassian.plugin.webresource.impl.support.http.Response;
import com.atlassian.plugin.webresource.impl.support.http.ServingType;
import com.atlassian.plugin.webresource.models.LooselyTypedRequestExpander;
import com.atlassian.plugin.webresource.models.RawRequest;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.sourcemap.Util;
import com.atlassian.sourcemap.WritableSourceMap;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller
extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private final RequestCache requestCache;

    public Controller(Globals globals, Request request, Response response) {
        super(globals, request, response);
        this.requestCache = new RequestCache(globals);
    }

    public void serveResource(String completeKey, String resourceName, ServingType servingType) {
        RawRequest raw = new RawRequest();
        raw.include(new WebResourceKey(completeKey));
        this.request.setRequestedResources(raw);
        this.request.setServingType(servingType);
        Resource resource = ResourceServingHelpers.getResource(this.requestCache, completeKey, resourceName);
        this.serveResource(raw, resource, true, false);
    }

    public void serveResourceSourceMap(String completeKey, String resourceName, ServingType servingType) {
        RawRequest raw = new RawRequest();
        raw.include(new WebResourceKey(completeKey));
        this.request.setRequestedResources(raw);
        this.request.setServingType(servingType);
        Resource resource = ResourceServingHelpers.getResource(this.requestCache, completeKey, resourceName);
        this.serveSourceMap(raw, resource);
    }

    public void serveBatch(RawRequest raw, ServingType servingType, String type, boolean resolveDependencies, boolean withLegacyConditions, boolean isCachingEnabled, boolean verifyBundleHash) {
        this.request.setRequestedResources(raw);
        this.request.setServingType(servingType);
        this.serveResources(raw, () -> {
            if (this.shouldValidateBundleHash(verifyBundleHash) && !this.isBundleHashValid(raw)) {
                EventFiringHelper.publishIfEventPublisherNonNull(this.globals.getEventPublisher(), new InvalidBundleHashEvent());
                return Collections.emptyList();
            }
            return this.getBatchResources(raw, type, resolveDependencies, withLegacyConditions);
        }, isCachingEnabled);
    }

    private boolean shouldValidateBundleHash(boolean perRequestFlag) {
        return perRequestFlag && Config.isBundleHashValidationEnabled() && ResourceUtils.shouldValidateRequest(this.request.getParams());
    }

    private boolean isBundleHashValid(RawRequest raw) {
        String bundleHashParam = this.request.getParams().get("_statichash");
        if (bundleHashParam == null) {
            return true;
        }
        String hash = bundleHashParam.substring(bundleHashParam.lastIndexOf("/") + 1);
        return UrlGenerationHelpers.calculateBundleHash(raw, this.requestCache).equals(hash);
    }

    public void serveBatchSourceMap(RawRequest raw, ServingType servingType, String type, boolean resolveDependencies, boolean withLegacyConditions) {
        this.request.setRequestedResources(raw);
        this.request.setServingType(servingType);
        this.serveResourcesSourceMap(raw, () -> this.getBatchResources(raw, type, resolveDependencies, withLegacyConditions));
    }

    protected List<Resource> getBatchResources(RawRequest raw, String type, boolean resolveDependencies, boolean withLegacyConditions) {
        LooselyTypedRequestExpander resources = new LooselyTypedRequestExpander(raw);
        LinkedHashSet<String> included = new LinkedHashSet<String>(resources.getIncluded());
        LinkedHashSet<String> excludedAndSync = new LinkedHashSet<String>(resources.getExcluded());
        Bundle syncContext = this.requestCache.getSnapshot().get("_context:_sync");
        if (null != syncContext) {
            excludedAndSync.addAll(syncContext.getDependencies());
        }
        BundleFinder bundles = new BundleFinder(this.requestCache.getSnapshot()).included(included).excluded(excludedAndSync, BaseHelpers.isConditionsSatisfied(this.requestCache, this.request.getParams())).deep(resolveDependencies).deepFilter(BaseHelpers.isConditionsSatisfied(this.requestCache, this.request.getParams())).deepFilter((Predicate<Bundle>)(withLegacyConditions ? Predicates.alwaysTrue() : Predicates.not(BaseHelpers.hasLegacyCondition())));
        return new ResourceFinder(bundles, this.requestCache).filter(ResourceServingHelpers.shouldBeIncludedInBatch(type, this.request.getParams())).end();
    }

    public void serveResourceRelativeToBatch(RawRequest raw, String resourceName, ServingType servingType, boolean resolveDependencies, boolean withLegacyConditions) {
        this.request.setRelativeResourceName(resourceName);
        this.request.setRequestedResources(raw);
        this.request.setServingType(servingType);
        Resource resourceRelativeToBatch = this.getResourceRelativeToBatch(raw, resourceName, resolveDependencies, withLegacyConditions);
        this.serveResource(raw, resourceRelativeToBatch, true, false);
    }

    public void serveResourceRelativeToBatchSourceMap(RawRequest raw, String resourceName, ServingType servingType, boolean resolveDependencies, boolean withLegacyConditions) {
        this.request.setRelativeResourceName(resourceName);
        this.request.setRequestedResources(raw);
        this.request.setServingType(servingType);
        Resource resourceRelativeToBatch = this.getResourceRelativeToBatch(raw, resourceName, resolveDependencies, withLegacyConditions);
        this.serveSourceMap(raw, resourceRelativeToBatch);
    }

    protected Resource getResourceRelativeToBatch(RawRequest raw, String resourceName, boolean resolveDependencies, boolean withLegacyConditions) {
        LooselyTypedRequestExpander resources = new LooselyTypedRequestExpander(raw);
        List<String> bundles = new BundleFinder(this.requestCache.getSnapshot()).included(resources.getIncluded()).excluded(resources.getExcluded(), BaseHelpers.isConditionsSatisfied(this.requestCache, this.request.getParams())).deep(resolveDependencies).deepFilter(BaseHelpers.isConditionsSatisfied(this.requestCache, this.request.getParams())).deepFilter((Predicate<Bundle>)(withLegacyConditions ? Predicates.alwaysTrue() : Predicates.not(BaseHelpers.hasLegacyCondition()))).end();
        return ResourceServingHelpers.getResource(this.requestCache, bundles, resourceName);
    }

    public void serveSource(String completeKey, String resourceName, ServingType servingType) {
        Resource resource;
        boolean IS_CACHING_ENABLED = false;
        RawRequest raw = new RawRequest();
        raw.include(new WebResourceKey(completeKey));
        this.request.setRequestedResources(raw);
        this.request.setServingType(servingType);
        if (Resource.isPrebuiltSourceName(resourceName)) {
            resourceName = Resource.getResourceNameFromPrebuiltSourceName(resourceName);
        }
        if (this.handleNotFoundRedirectAndNotModified(resource = ResourceServingHelpers.getResource(this.requestCache, completeKey, resourceName))) {
            return;
        }
        this.sendCached(new ContentImpl(resource.getContentType(), false){

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                String prebuildSourceName = Resource.getPrebuiltSourcePath(resource.getLocation());
                InputStream sourceStream = resource.getStreamFor(prebuildSourceName);
                if (sourceStream != null) {
                    Support.copy(sourceStream, out);
                } else {
                    resource.getContent().writeTo(out, isSourceMapEnabled);
                }
                return null;
            }
        }, resource.getParams(), false);
    }

    protected void serveResource(RawRequest raw, Resource resource) {
        boolean IS_CACHING_ENABLED = true;
        boolean APPLY_ANNOTATORS = true;
        if (this.handleNotFoundRedirectAndNotModified(resource)) {
            return;
        }
        Content content = Helpers.transform(this.globals, this.requiredResources(raw), this.request.getUrl(), resource, this.request.getParams(), true);
        this.sendCached(content, resource.getParams(), true);
    }

    protected void serveResource(RawRequest raw, Resource resource, boolean applyTransformations, boolean isCachingEnabled) {
        if (this.handleNotFoundRedirectAndNotModified(resource)) {
            return;
        }
        Content content = applyTransformations ? Helpers.transform(this.globals, this.requiredResources(raw), this.request.getUrl(), resource, this.request.getParams(), true) : resource.getContent();
        this.sendCached(content, resource.getParams(), isCachingEnabled);
    }

    protected void serveResources(RawRequest raw, Supplier<Collection<Resource>> resources, boolean isCachingEnabled) {
        LinkedHashSet<String> requiredResources = this.requiredResources(raw);
        if (log.isDebugEnabled()) {
            log.debug("Serving requiredResources {} and resources with names {}", (Object)String.join((CharSequence)"|", requiredResources), (Object)resources.get().stream().map(Resource::getFullName).collect(Collectors.joining("|")));
        }
        Content content = Helpers.transform(this.globals, requiredResources, this.request.getUrl(), this.request.getType(), resources, this.request.getParams());
        this.sendCached(content, Collections.emptyMap(), isCachingEnabled);
    }

    protected void serveSourceMap(RawRequest raw, Resource resource) {
        boolean IS_CACHING_ENABLED = false;
        boolean APPLY_ANNOTATORS = true;
        if (this.handleNotFoundRedirectAndNotModified(resource)) {
            return;
        }
        Content content = Helpers.transform(this.globals, this.requiredResources(raw), Router.sourceMapUrlToUrl(this.request.getUrl()), resource, this.request.getParams(), true);
        this.sendCached(content, resource.getParams(), false);
    }

    private void serveResourcesSourceMap(RawRequest raw, Supplier<Collection<Resource>> resources) {
        boolean IS_CACHING_ENABLED = true;
        String resourcePath = Router.sourceMapUrlToUrl(this.request.getPath());
        String type = Request.getType(resourcePath);
        Content content = Helpers.transform(this.globals, this.requiredResources(raw), Router.sourceMapUrlToUrl(this.request.getUrl()), type, resources, this.request.getParams());
        this.sendCached(content, Collections.emptyMap(), true);
    }

    @Deprecated
    private LinkedHashSet<String> requiredResources(RawRequest raw) {
        LooselyTypedRequestExpander resources = new LooselyTypedRequestExpander(raw);
        LinkedHashSet<String> onlyUsedForAnnotatingExplicitlyRequiredWebModules = new LinkedHashSet<String>(resources.getIncluded());
        return onlyUsedForAnnotatingExplicitlyRequiredWebModules;
    }

    protected boolean handleNotFoundRedirectAndNotModified(Resource resource) {
        if (resource == null) {
            this.response.sendError(404);
            return true;
        }
        if (this.checkIfCachedAndNotModified(resource.getParent().getUpdatedAt())) {
            return true;
        }
        if (resource.isRedirect()) {
            this.response.sendRedirect(resource.getLocation(), resource.getContentType());
            return true;
        }
        return false;
    }

    protected boolean checkIfCachedAndNotModified(Date updatedAt) {
        LastModifiedHandler lastModifiedHandler = new LastModifiedHandler(updatedAt);
        return this.request.isCacheable() && this.response.checkRequestHelper(lastModifiedHandler);
    }

    protected void sendCached(Content content, Map<String, String> params, boolean isCachingEnabled) {
        if (!content.isPresent() && Config.isBundleHashValidationEnabled()) {
            this.response.sendError(404);
            return;
        }
        if (Boolean.TRUE.toString().equals(params.get("allow-public-use"))) {
            this.response.addHeader("Access-Control-Allow-Origin", "*");
        }
        boolean cacheHit = this.isSourceMapEnabled() && content.isTransformed() && this.globals.getConfig().optimiseSourceMapsForDevelopment() ? this.sendCachedInDevelopment(content, isCachingEnabled) : this.sendCachedInProduction(content, isCachingEnabled);
        log.debug("Called sendCached on the resource with request URL {} and response status code {}", (Object)this.request.getPath(), (Object)this.response.getStatus());
        if (this.globals.getConfig().isPerformanceTrackingEnabled()) {
            EventFiringHelper.publishedThrottledEventIfEventPublisherNonNull(this.globals.getEventPublisher(), new RequestServingCacheEvent(this.request.isCacheable(), cacheHit, isCachingEnabled, this.request.isSourceMap(), this.request.getServingType(), this.response.numBytesWritten()));
        }
    }

    private boolean sendCachedInDevelopment(Content content, boolean isCachingEnabled) {
        String resourceContentType;
        PassThroughCache cache;
        String contentType = content.getContentType() != null ? content.getContentType() : this.request.getContentType();
        this.response.setContentTypeIfNotBlank(contentType);
        Cache cache2 = cache = isCachingEnabled && this.request.isCacheable() ? this.globals.getContentCache() : new PassThroughCache();
        if (this.request.isSourceMap()) {
            String resourcePath = Router.sourceMapUrlToUrl(this.request.getPath());
            resourceContentType = content.getContentType() != null ? content.getContentType() : this.globals.getConfig().getContentType(resourcePath);
        } else {
            resourceContentType = contentType;
        }
        Cache.TwoStreamProvider twoStreamProvider = (out1, out2) -> {
            boolean sourceMapEnabled = true;
            LineCountingProxyOutputStream lineCountingStream = new LineCountingProxyOutputStream(out1);
            ReadableSourceMap sourceMap = content.writeTo(lineCountingStream, true);
            String sourceMapUrl = this.globals.getRouter().sourceMapUrl(this.request.getPath(), this.request.getParams());
            try {
                out1.write(("\n" + Util.generateSourceMapComment((String)sourceMapUrl, (String)resourceContentType)).getBytes());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            Controller.generateOneToOneSourceMap(out2, lineCountingStream, ReadableSourceMap.toWritableSourceMap((ReadableSourceMap)sourceMap), this.request);
        };
        OutputStream out = this.response.getOutputStream();
        String cacheKey = this.buildCacheKey();
        boolean cacheHit = this.request.isSourceMap() ? cache.cacheTwo("http", cacheKey, null, out, twoStreamProvider) : cache.cacheTwo("http", cacheKey, out, null, twoStreamProvider);
        return cacheHit;
    }

    private boolean sendCachedInProduction(Content content, boolean isCachingEnabled) {
        boolean cacheHit;
        String contentType = content.getContentType() != null ? content.getContentType() : this.request.getContentType();
        this.response.setContentTypeIfNotBlank(contentType);
        PassThroughCache cache = isCachingEnabled && this.request.isCacheable() ? this.globals.getContentCache() : new PassThroughCache();
        String cacheKey = this.buildCacheKey();
        if (this.request.isSourceMap()) {
            if (this.globals.getConfig().isSourceMapEnabled()) {
                cacheHit = cache.cache("http", cacheKey, this.response.getOutputStream(), producerOut -> {
                    LineCountingProxyOutputStream lineCountingStream = new LineCountingProxyOutputStream(new NullOutputStream());
                    ReadableSourceMap sourceMap = content.writeTo(lineCountingStream, true);
                    Controller.generateOneToOneSourceMap(producerOut, lineCountingStream, ReadableSourceMap.toWritableSourceMap((ReadableSourceMap)sourceMap), this.request);
                });
            } else {
                cacheHit = false;
                this.response.sendError(503);
            }
        } else {
            cacheHit = cache.cache("http", cacheKey, this.response.getOutputStream(), producerOut -> content.writeTo(producerOut, false));
            if (this.isSourceMapEnabled() && content.isTransformed()) {
                String sourceMapUrl = this.globals.getRouter().sourceMapUrl(this.request.getPath(), this.request.getParams());
                try {
                    this.response.getOutputStream().write(("\n" + Util.generateSourceMapComment((String)sourceMapUrl, (String)contentType)).getBytes());
                }
                catch (IOException | RuntimeException e) {
                    Support.LOGGER.error("can't generate source map comment", (Throwable)e);
                }
            }
        }
        return cacheHit;
    }

    private static void generateOneToOneSourceMap(OutputStream out, LineCountingProxyOutputStream lineCountingStream, WritableSourceMap sourceMap, Request request) {
        if (sourceMap == null) {
            String resourceUrl = Router.sourceMapUrlToUrl(request.getUrl());
            sourceMap = Util.create1to1SourceMap((int)lineCountingStream.getLinesCount(), (String)resourceUrl);
        }
        try {
            out.write(sourceMap.generate().getBytes());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String buildCacheKey() {
        return this.request.getRequestHash();
    }

    protected boolean isSourceMapEnabled() {
        return this.globals.getConfig().isSourceMapEnabledFor(this.request.getType());
    }
}

