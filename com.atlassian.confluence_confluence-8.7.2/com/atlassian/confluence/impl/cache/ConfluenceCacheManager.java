/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.Supplier
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.impl.cache.ClusterCacheFlushEvent;
import com.atlassian.confluence.impl.cache.DelegatingCacheManager;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
class ConfluenceCacheManager
extends DelegatingCacheManager {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceCacheManager.class);
    private final CacheManager delegate;
    private final EventPublisher eventPublisher;

    public ConfluenceCacheManager(CacheManager delegate, EventPublisher eventPublisher) {
        this.delegate = delegate;
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    protected CacheManager getDelegate() {
        return this.delegate;
    }

    @Override
    public void flushCaches() {
        log.warn("Flushing all caches");
        super.flushCaches();
        this.eventPublisher.publish((Object)new ClusterCacheFlushEvent(this));
    }

    @Override
    @Nonnull
    public <V> CachedReference<V> getCachedReference(Class<?> owningClass, String name, Supplier<V> supplier, CacheSettings settings) {
        return this.getDelegate().getCachedReference(owningClass, name, supplier, ConfluenceCacheManager.overrideCachedReferenceSettings(settings));
    }

    @Override
    @Nonnull
    public <V> CachedReference<V> getCachedReference(String name, Supplier<V> supplier, CacheSettings settings) {
        return this.getDelegate().getCachedReference(name, supplier, ConfluenceCacheManager.overrideCachedReferenceSettings(settings));
    }

    private static CacheSettings overrideCachedReferenceSettings(CacheSettings settings) {
        return settings.override(new CacheSettingsBuilder().maxEntries(1).build());
    }
}

