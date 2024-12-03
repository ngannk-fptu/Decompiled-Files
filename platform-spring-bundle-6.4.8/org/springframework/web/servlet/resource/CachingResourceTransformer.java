/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

public class CachingResourceTransformer
implements ResourceTransformer {
    private static final Log logger = LogFactory.getLog(CachingResourceTransformer.class);
    private final Cache cache;

    public CachingResourceTransformer(Cache cache) {
        Assert.notNull((Object)cache, "Cache is required");
        this.cache = cache;
    }

    public CachingResourceTransformer(CacheManager cacheManager, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalArgumentException("Cache '" + cacheName + "' not found");
        }
        this.cache = cache;
    }

    public Cache getCache() {
        return this.cache;
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
        Resource transformed = this.cache.get((Object)resource, Resource.class);
        if (transformed != null) {
            logger.trace((Object)"Resource resolved from cache");
            return transformed;
        }
        transformed = transformerChain.transform(request, resource);
        this.cache.put(resource, transformed);
        return transformed;
    }
}

