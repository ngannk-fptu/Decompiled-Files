/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.NotificationContentCacheKey
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.confluence.notifications.content.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.NotificationContentCacheKey;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.impl.ContentCacheLoader;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DefaultCachedContentFinder
implements CachedContentFinder {
    @VisibleForTesting
    static final int APPROX_LOCALES_INSTALLED = 5;
    @VisibleForTesting
    static final String ANON_RENDER_CACHE_ENABLED = "notification.plugin.caching.enabled";
    static final String CACHE_NAME = DefaultCachedContentFinder.class.getName();
    private final ContentService contentService;
    private final Cache<NotificationContentCacheKey, Option<Content>> contentBodyCache;
    private final DarkFeatureManager darkFeatureManager;
    private final ContentCacheLoader contentCacheLoader;

    public DefaultCachedContentFinder(ContentService contentService, CacheManager cacheManager, ContentCacheLoader contentCacheLoader, DarkFeatureManager darkFeatureManager) {
        this.contentService = contentService;
        this.darkFeatureManager = darkFeatureManager;
        this.contentBodyCache = DefaultCachedContentFinder.createCache((CacheFactory)cacheManager);
        this.contentCacheLoader = contentCacheLoader;
    }

    private static Cache<NotificationContentCacheKey, Option<Content>> createCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().local().expireAfterWrite(60L, TimeUnit.SECONDS).maxEntries(Integer.getInteger("notifications.sender.thread.count", 3) * 5).build());
    }

    public Option<Content> getContent(UUID notificationId, ModuleCompleteKey key, Locale locale, ContentId contentId, Expansion ... expansions) {
        if (this.isCachingEnabled()) {
            Option permissionQuery = this.contentService.find(new Expansion[0]).withId(contentId).fetchOne();
            if (permissionQuery.isDefined()) {
                NotificationContentCacheKey cacheKey = new NotificationContentCacheKey(notificationId, key, locale, contentId, expansions);
                return (Option)this.contentBodyCache.get((Object)cacheKey, () -> this.contentCacheLoader.load(cacheKey));
            }
            return permissionQuery;
        }
        return this.contentService.find(expansions).withId(contentId).fetchOne();
    }

    public Expansion exportBody() {
        if (this.isCachingEnabled()) {
            return CommonContentExpansions.ANON_EXPORT_BODY;
        }
        return CommonContentExpansions.EXPORT_BODY;
    }

    public ContentRepresentation exportRepresentation() {
        if (this.isCachingEnabled()) {
            return ContentRepresentation.ANONYMOUS_EXPORT_VIEW;
        }
        return ContentRepresentation.EXPORT_VIEW;
    }

    private boolean isCachingEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers(ANON_RENDER_CACHE_ENABLED).orElse(false);
    }
}

