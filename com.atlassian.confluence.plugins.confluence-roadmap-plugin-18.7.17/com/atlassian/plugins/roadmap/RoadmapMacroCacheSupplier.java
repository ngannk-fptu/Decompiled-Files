/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.plugins.roadmap.TimelinePlannerMacroManager;
import com.atlassian.plugins.roadmap.models.RoadmapPageLink;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.util.concurrent.TimeUnit;

class RoadmapMacroCacheSupplier {
    private final Supplier<Cache<String, byte[]>> imageCacheRef = Lazy.supplier(() -> RoadmapMacroCacheSupplier.createImageCache((CacheFactory)cacheFactory));
    private final Supplier<Cache<String, String>> macroSourceCacheRef = Lazy.supplier(() -> RoadmapMacroCacheSupplier.createMacroSourceCache((CacheFactory)cacheFactory));
    private final Supplier<Cache<String, TimelinePlannerMacroManager.LinkStatus>> linkStatusCacheRef = Lazy.supplier(() -> RoadmapMacroCacheSupplier.createLinkStatusCache((CacheFactory)cacheFactory));
    private final Supplier<Cache<String, RoadmapPageLink>> pageLinkCacheRef = Lazy.supplier(() -> RoadmapMacroCacheSupplier.createPageLinkCache((CacheFactory)cacheFactory));
    public static final String IMAGE_CACHE_NAME = "RoadmapMacroImages";
    public static final String MACRO_SOURCE_CACHE_NAME = "RoadmapMacroSources";
    public static final String LINK_STATUS_CACHE_NAME = "RoadmapMacroLinkStatuses";
    public static final String PAGE_LINK_CACHE_NAME = "RoadmapMacroPageLinks";

    RoadmapMacroCacheSupplier(CacheManager cacheFactory) {
    }

    private static Cache<String, byte[]> createImageCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(IMAGE_CACHE_NAME, null, new CacheSettingsBuilder().remote().build());
    }

    private static Cache<String, String> createMacroSourceCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(MACRO_SOURCE_CACHE_NAME, null, new CacheSettingsBuilder().remote().build());
    }

    private static Cache<String, TimelinePlannerMacroManager.LinkStatus> createLinkStatusCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(LINK_STATUS_CACHE_NAME, null, new CacheSettingsBuilder().remote().expireAfterWrite(2L, TimeUnit.HOURS).build());
    }

    private static Cache<String, RoadmapPageLink> createPageLinkCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(PAGE_LINK_CACHE_NAME, null, new CacheSettingsBuilder().remote().expireAfterWrite(2L, TimeUnit.HOURS).build());
    }

    public Cache<String, byte[]> getImageCache() {
        return (Cache)this.imageCacheRef.get();
    }

    public Cache<String, String> getMarcoSourceCache() {
        return (Cache)this.macroSourceCacheRef.get();
    }

    public Cache<String, TimelinePlannerMacroManager.LinkStatus> getLinkStatusCache() {
        return (Cache)this.linkStatusCacheRef.get();
    }

    public Cache<String, RoadmapPageLink> getPageLinkCache() {
        return (Cache)this.pageLinkCacheRef.get();
    }
}

