/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.ResourceChainRegistration;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

public class ResourceHandlerRegistration {
    private final String[] pathPatterns;
    private final List<String> locationValues = new ArrayList<String>();
    private final List<Resource> locationsResources = new ArrayList<Resource>();
    @Nullable
    private Integer cachePeriod;
    @Nullable
    private CacheControl cacheControl;
    @Nullable
    private ResourceChainRegistration resourceChainRegistration;
    private boolean useLastModified = true;
    private boolean optimizeLocations = false;

    public ResourceHandlerRegistration(String ... pathPatterns) {
        Assert.notEmpty((Object[])pathPatterns, "At least one path pattern is required for resource handling.");
        this.pathPatterns = pathPatterns;
    }

    public ResourceHandlerRegistration addResourceLocations(String ... locations) {
        this.locationValues.addAll(Arrays.asList(locations));
        return this;
    }

    public ResourceHandlerRegistration addResourceLocations(Resource ... locations) {
        this.locationsResources.addAll(Arrays.asList(locations));
        return this;
    }

    public ResourceHandlerRegistration setCachePeriod(Integer cachePeriod) {
        this.cachePeriod = cachePeriod;
        return this;
    }

    public ResourceHandlerRegistration setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
        return this;
    }

    public ResourceHandlerRegistration setUseLastModified(boolean useLastModified) {
        this.useLastModified = useLastModified;
        return this;
    }

    public ResourceHandlerRegistration setOptimizeLocations(boolean optimizeLocations) {
        this.optimizeLocations = optimizeLocations;
        return this;
    }

    public ResourceChainRegistration resourceChain(boolean cacheResources) {
        this.resourceChainRegistration = new ResourceChainRegistration(cacheResources);
        return this.resourceChainRegistration;
    }

    public ResourceChainRegistration resourceChain(boolean cacheResources, Cache cache) {
        this.resourceChainRegistration = new ResourceChainRegistration(cacheResources, cache);
        return this.resourceChainRegistration;
    }

    protected String[] getPathPatterns() {
        return this.pathPatterns;
    }

    protected ResourceHttpRequestHandler getRequestHandler() {
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        if (this.resourceChainRegistration != null) {
            handler.setResourceResolvers(this.resourceChainRegistration.getResourceResolvers());
            handler.setResourceTransformers(this.resourceChainRegistration.getResourceTransformers());
        }
        handler.setLocationValues(this.locationValues);
        handler.setLocations(this.locationsResources);
        if (this.cacheControl != null) {
            handler.setCacheControl(this.cacheControl);
        } else if (this.cachePeriod != null) {
            handler.setCacheSeconds(this.cachePeriod);
        }
        handler.setUseLastModified(this.useLastModified);
        handler.setOptimizeLocations(this.optimizeLocations);
        return handler;
    }
}

