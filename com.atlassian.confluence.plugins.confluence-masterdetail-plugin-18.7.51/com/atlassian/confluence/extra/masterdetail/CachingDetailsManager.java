/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.Supplier
 *  com.atlassian.confluence.event.events.content.Contented
 *  com.atlassian.confluence.event.events.types.ConfluenceEntityUpdated
 *  com.atlassian.confluence.event.events.types.Removed
 *  com.atlassian.confluence.event.events.types.Trashed
 *  com.atlassian.confluence.event.events.types.Updated
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.types.ConfluenceEntityUpdated;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.extra.masterdetail.CachingDetails;
import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CachingDetailsManager
implements InitializingBean,
DisposableBean {
    private static final String CACHE_NAME = CachingDetailsManager.class.getName();
    private final Cache<String, CachingDetails> cache;
    private final EventListenerRegistrar eventListenerRegistrar;

    @Autowired
    public CachingDetailsManager(@ComponentImport CacheManager cacheFactory, @ComponentImport @Qualifier(value="eventListenerRegistrar") EventListenerRegistrar eventListenerRegistrar) {
        this.eventListenerRegistrar = eventListenerRegistrar;
        CacheSettings cacheSettings = new CacheSettingsBuilder().maxEntries(20000).remote().replicateViaInvalidation().build();
        this.cache = cacheFactory.getCache(CACHE_NAME, null, cacheSettings);
    }

    public ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> get(long pageId, Supplier<ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>>> loader) {
        CachingDetails cachingDetails = (CachingDetails)this.cache.get((Object)Long.toString(pageId), () -> new CachingDetails((ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>>)((ImmutableMap)loader.get())));
        if (cachingDetails == null) {
            return null;
        }
        return cachingDetails.getValue();
    }

    @EventListener
    public void onContentUpdated(Updated event) {
        this.invalidateCacheOnContentEvent(event);
    }

    @EventListener
    public void onContentRemoved(Removed event) {
        this.invalidateCacheOnContentEvent(event);
    }

    @EventListener
    public void onContentTrashed(Trashed event) {
        this.invalidateCacheOnContentEvent(event);
    }

    @EventListener
    public void onConfluenceEntityUpdated(ConfluenceEntityUpdated event) {
        this.invalidateCacheOnContentEvent(event);
    }

    @EventListener
    public void onPluginEnable(PluginEnabledEvent event) {
        if (event.getPlugin().getKey().equals("confluence.extra.masterdetail")) {
            this.clearCache();
        }
    }

    private void invalidateCacheOnContentEvent(Object event) {
        if (event instanceof Contented) {
            this.cache.remove((Object)((Contented)event).getContent().getIdAsString());
        }
    }

    public void clearCache() {
        this.cache.removeAll();
    }

    public void destroy() {
        this.eventListenerRegistrar.unregister((Object)this);
        this.clearCache();
    }

    public void afterPropertiesSet() {
        this.eventListenerRegistrar.register((Object)this);
        this.clearCache();
    }
}

