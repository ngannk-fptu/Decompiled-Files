/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public class CachingResourceResolver
extends AbstractResourceResolver {
    public static final String RESOLVED_RESOURCE_CACHE_KEY_PREFIX = "resolvedResource:";
    public static final String RESOLVED_URL_PATH_CACHE_KEY_PREFIX = "resolvedUrlPath:";
    private final Cache cache;
    private final List<String> contentCodings = new ArrayList<String>(EncodedResourceResolver.DEFAULT_CODINGS);

    public CachingResourceResolver(Cache cache) {
        Assert.notNull((Object)cache, "Cache is required");
        this.cache = cache;
    }

    public CachingResourceResolver(CacheManager cacheManager, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalArgumentException("Cache '" + cacheName + "' not found");
        }
        this.cache = cache;
    }

    public Cache getCache() {
        return this.cache;
    }

    public void setContentCodings(List<String> codings) {
        Assert.notEmpty(codings, "At least one content coding expected");
        this.contentCodings.clear();
        this.contentCodings.addAll(codings);
    }

    public List<String> getContentCodings() {
        return Collections.unmodifiableList(this.contentCodings);
    }

    @Override
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String key = this.computeKey(request, requestPath);
        Resource resource = this.cache.get((Object)key, Resource.class);
        if (resource != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)"Resource resolved from cache");
            }
            return resource;
        }
        resource = chain.resolveResource(request, requestPath, locations);
        if (resource != null) {
            this.cache.put(key, resource);
        }
        return resource;
    }

    protected String computeKey(@Nullable HttpServletRequest request, String requestPath) {
        String codingKey;
        if (request != null && StringUtils.hasText(codingKey = this.getContentCodingKey(request))) {
            return RESOLVED_RESOURCE_CACHE_KEY_PREFIX + requestPath + "+encoding=" + codingKey;
        }
        return RESOLVED_RESOURCE_CACHE_KEY_PREFIX + requestPath;
    }

    @Nullable
    private String getContentCodingKey(HttpServletRequest request) {
        String header = request.getHeader("Accept-Encoding");
        if (!StringUtils.hasText(header)) {
            return null;
        }
        return Arrays.stream(StringUtils.tokenizeToStringArray(header, ",")).map(token -> {
            int index = token.indexOf(59);
            return (index >= 0 ? token.substring(0, index) : token).trim().toLowerCase();
        }).filter(this.contentCodings::contains).sorted().collect(Collectors.joining(","));
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String key = RESOLVED_URL_PATH_CACHE_KEY_PREFIX + resourceUrlPath;
        String resolvedUrlPath = this.cache.get((Object)key, String.class);
        if (resolvedUrlPath != null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)"Path resolved from cache");
            }
            return resolvedUrlPath;
        }
        resolvedUrlPath = chain.resolveUrlPath(resourceUrlPath, locations);
        if (resolvedUrlPath != null) {
            this.cache.put(key, resolvedUrlPath);
        }
        return resolvedUrlPath;
    }
}

