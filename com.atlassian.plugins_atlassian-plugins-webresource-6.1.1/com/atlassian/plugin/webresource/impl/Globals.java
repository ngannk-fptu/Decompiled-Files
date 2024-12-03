/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.PluginEventManager
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.cache.filecache.Cache;
import com.atlassian.plugin.cache.filecache.impl.FileCacheImpl;
import com.atlassian.plugin.cache.filecache.impl.PassThroughCache;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.impl.support.ResettableLazyReferenceWithVersionCheck;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.impl.support.UrlCache;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class Globals {
    private final Config config;
    private final EventPublisher eventPublisher;
    private final PluginEventManager pluginEventManager;
    private final ResettableLazyReferenceWithVersionCheck<Snapshot> cachedSnapshot;
    private final List<StateChangeCallback> stateChangeCallbacks;
    private final Router router;
    private Cache contentCache;
    private Cache temporaryIncrementalCache;
    private UrlCache urlCache;

    public Globals(final Config config, @Nullable EventPublisher eventPublisher, PluginEventManager pluginEventManager) {
        this.config = config;
        this.eventPublisher = eventPublisher;
        this.pluginEventManager = pluginEventManager;
        this.stateChangeCallbacks = new ArrayList<StateChangeCallback>();
        this.buildAndSetContentCache();
        this.buildAndSetTemporaryIncrementalCache();
        this.buildAndSetUrlGenerationCache();
        this.router = new Router(this);
        this.cachedSnapshot = new ResettableLazyReferenceWithVersionCheck<Snapshot>(){

            @Override
            protected Snapshot create() {
                Snapshot snapshot = config.getWebResourcesWithoutCache();
                if (config.isGlobalMinificationEnabled()) {
                    config.runResourceCompilation(snapshot);
                }
                return snapshot;
            }

            @Override
            protected int getVersion() {
                return config.partialHashCode();
            }
        };
        this.onStateChange(this.cachedSnapshot::reset);
    }

    public Config getConfig() {
        return this.config;
    }

    public Router getRouter() {
        return this.router;
    }

    public Cache getContentCache() {
        return this.contentCache;
    }

    public void onStateChange(StateChangeCallback callback) {
        this.stateChangeCallbacks.add(callback);
    }

    public void triggerStateChange() {
        for (StateChangeCallback callback : this.stateChangeCallbacks) {
            callback.apply();
        }
    }

    private void buildAndSetContentCache() {
        if (this.contentCache != null) {
            throw new RuntimeException("content cache already set!");
        }
        if (this.config.isContentCacheEnabled()) {
            try {
                this.contentCache = new FileCacheImpl(this.config.getCacheDirectory(), this.config.getContentCacheSize());
                this.onStateChange(this.contentCache::clear);
            }
            catch (Exception e) {
                Support.LOGGER.error("Could not create file cache object, will startup with filecaching disabled, please investigate the cause and correct it.", (Throwable)e);
                this.contentCache = new PassThroughCache();
            }
        } else {
            this.contentCache = new PassThroughCache();
        }
    }

    private void buildAndSetUrlGenerationCache() {
        if (this.urlCache != null) {
            throw new RuntimeException("url cache already set!");
        }
        this.urlCache = this.config.isUrlCachingEnabled() ? new UrlCache.Impl(this.config.getUrlCacheSize()) : new UrlCache.PassThrough();
        this.onStateChange(this.urlCache::clear);
    }

    public UrlCache getUrlCache() {
        return this.urlCache;
    }

    public void buildAndSetTemporaryIncrementalCache() {
        if (this.temporaryIncrementalCache != null) {
            throw new RuntimeException("temporary incremental cache already set!");
        }
        if (this.config.isIncrementalCacheEnabled()) {
            try {
                this.temporaryIncrementalCache = new FileCacheImpl(this.config.getCacheDirectory(), this.config.getIncrementalCacheSize());
                this.onStateChange(this.temporaryIncrementalCache::clear);
            }
            catch (Exception e) {
                Support.LOGGER.error("Could not create file cache object, will startup with filecaching disabled, please investigate the cause and correct it.", (Throwable)e);
                this.temporaryIncrementalCache = new PassThroughCache();
            }
        } else {
            this.temporaryIncrementalCache = new PassThroughCache();
        }
    }

    public Cache getTemporaryIncrementalCache() {
        return this.temporaryIncrementalCache;
    }

    public Snapshot getSnapshot() {
        return this.cachedSnapshot.get();
    }

    public PluginEventManager getPluginEventManager() {
        return this.pluginEventManager;
    }

    @Nullable
    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    static interface StateChangeCallback {
        public void apply();
    }
}

