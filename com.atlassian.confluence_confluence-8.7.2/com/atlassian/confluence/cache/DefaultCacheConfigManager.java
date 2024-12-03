/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.cache.CacheConfigManager
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.cache;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.CacheConfigManager;
import com.atlassian.confluence.cache.CacheSettingsManager;
import com.atlassian.confluence.event.events.admin.MaxCacheSizeChangedEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DefaultCacheConfigManager
implements CacheConfigManager,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultCacheConfigManager.class);
    private final CacheSettingsManager cacheSettingsManager;
    private final CacheManager cacheManager;
    private final EventPublisher eventPublisher;

    public DefaultCacheConfigManager(CacheSettingsManager cacheSettingsManager, CacheManager cacheManager, EventPublisher eventPublisher) {
        this.cacheSettingsManager = cacheSettingsManager;
        this.cacheManager = cacheManager;
        this.eventPublisher = eventPublisher;
    }

    public void changeMaxCacheSize(String name, int newValue) {
        Preconditions.checkNotNull((Object)name);
        Preconditions.checkArgument((newValue >= 0 ? 1 : 0) != 0);
        log.info("Updating [{}] cache to have [{}] max entries", (Object)name, (Object)newValue);
        ManagedCache cache = this.cacheManager.getManagedCache(name);
        Preconditions.checkNotNull((Object)cache, (Object)("Attempted to update the max size of non-existent cache with name " + name));
        int currentValue = cache.currentMaxEntries();
        cache.updateMaxEntries(newValue);
        this.cacheSettingsManager.changeMaxEntries(name, newValue);
        boolean saveSuccessful = this.cacheSettingsManager.saveSettings();
        if (!saveSuccessful) {
            log.error("Saving settings failed. Not propagating changing max entries of [{}] to be [{}]", (Object)name, (Object)newValue);
        } else {
            this.updateRemoteNodes(name, currentValue, newValue);
        }
    }

    private void updateRemoteNodes(String name, int oldValue, int newValue) {
        log.info("Publishing event for cache [{}] to have [{}] max entries", (Object)name, (Object)newValue);
        this.eventPublisher.publish((Object)new MaxCacheSizeChangedEvent(this, name, oldValue, newValue));
    }

    @EventListener
    public void onMaxEntriesSettingChanged(ClusterEventWrapper clusterEventWrapper) {
        Event wrappedEvent = clusterEventWrapper.getEvent();
        if (wrappedEvent instanceof MaxCacheSizeChangedEvent) {
            MaxCacheSizeChangedEvent event = (MaxCacheSizeChangedEvent)wrappedEvent;
            ManagedCache cache = this.cacheManager.getManagedCache(event.getCacheName());
            String name = event.getCacheName();
            int newValue = event.getMaxCacheSize();
            log.info("Updating [{}] cache to have [{}] max entries", (Object)name, (Object)newValue);
            Preconditions.checkNotNull((Object)cache, (Object)("Attempted to update the max size of non-existent cache with name " + name));
            cache.updateMaxEntries(newValue);
            this.cacheSettingsManager.reloadSettings();
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

