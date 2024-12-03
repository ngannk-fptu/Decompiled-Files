/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.cache;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.api.services.JiraCacheManager;
import com.atlassian.confluence.extra.jira.cache.CacheKey;
import com.atlassian.confluence.extra.jira.cache.CacheLoggingUtils;
import com.atlassian.confluence.extra.jira.cache.CompressingStringCache;
import com.atlassian.confluence.extra.jira.cache.JIMCache;
import com.atlassian.confluence.extra.jira.cache.JIMCacheProvider;
import com.atlassian.confluence.extra.jira.request.JiraChannelResponseHandler;
import com.atlassian.confluence.extra.jira.request.JiraStringResponseHandler;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJiraCacheManager
implements JiraCacheManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultJiraCacheManager.class);
    public static final String PARAM_CLEAR_CACHE = "clearCache";
    private final JIMCache<CompressingStringCache> responseCache;
    private JIMCache<JiraChannelResponseHandler> channelResponseCache;
    private JIMCache<JiraStringResponseHandler> stringResponseCache;
    private final Supplier<String> version;
    private final EventPublisher eventPublisher;
    private final JIMCacheProvider cacheProvider;

    public DefaultJiraCacheManager(JIMCacheProvider cacheProvider, PluginAccessor pluginAccessor, EventPublisher eventPublisher) {
        this.responseCache = cacheProvider.getResponseCache();
        this.eventPublisher = eventPublisher;
        this.cacheProvider = cacheProvider;
        this.version = Lazy.supplier(() -> pluginAccessor.getPlugin("confluence.extra.jira").getPluginInformation().getVersion());
    }

    @Override
    public void clearJiraIssuesCache(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean isAnonymous) {
        if (appLink == null) {
            return;
        }
        CacheKey mappedCacheKey = new CacheKey(url, appLink.getId().toString(), columns, false, forceAnonymous, false, true, (String)this.version.get());
        CacheKey unmappedCacheKey = new CacheKey(url, appLink.getId().toString(), columns, false, forceAnonymous, false, false, (String)this.version.get());
        if (this.channelResponseCache == null || this.stringResponseCache == null) {
            this.initializeCache();
        }
        DefaultJiraCacheManager.clean(mappedCacheKey, unmappedCacheKey, isAnonymous, this.responseCache);
        DefaultJiraCacheManager.clean(mappedCacheKey, unmappedCacheKey, isAnonymous, this.channelResponseCache);
        DefaultJiraCacheManager.clean(mappedCacheKey, unmappedCacheKey, isAnonymous, this.stringResponseCache);
    }

    private static <T> void clean(CacheKey mappedKey, CacheKey unmappedKey, boolean isAnonymous, JIMCache<T> cache) {
        JIMCache.fold(cache.get(mappedKey.toKey()), t -> {
            if (t.isPresent()) {
                JIMCache.fold(cache.remove(mappedKey.toKey()), (result, throwable) -> {
                    CacheLoggingUtils.log(log, throwable, true);
                    return null;
                });
            } else {
                boolean userIsMapped;
                boolean bl = userIsMapped = !isAnonymous && AuthenticatedUserThreadLocal.getUsername() != null;
                if (!userIsMapped) {
                    JIMCache.fold(cache.remove(unmappedKey.toKey()), (result, throwable) -> {
                        CacheLoggingUtils.log(log, throwable, true);
                        return null;
                    });
                }
            }
            return true;
        }, throwable -> {
            CacheLoggingUtils.log(log, throwable, false);
            return null;
        });
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) {
        this.initializeCache();
    }

    @Override
    public void initializeCache() {
        this.channelResponseCache = this.cacheProvider.getChannelResponseHandlersCache();
        this.stringResponseCache = this.cacheProvider.getStringResponseHandlersCache();
    }
}

