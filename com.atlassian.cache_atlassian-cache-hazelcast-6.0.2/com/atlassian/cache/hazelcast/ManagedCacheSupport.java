/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.google.common.collect.ImmutableSortedMap
 *  com.hazelcast.config.MapConfig
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import com.google.common.collect.ImmutableSortedMap;
import com.hazelcast.config.MapConfig;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class ManagedCacheSupport
implements ManagedCache {
    private final String name;
    protected final HazelcastCacheManager cacheManager;

    public ManagedCacheSupport(String name, HazelcastCacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.name = name;
    }

    @Nullable
    public Long currentExpireAfterAccessMillis() {
        long maxIdle = TimeUnit.SECONDS.toMillis(this.getConfig().getMaxIdleSeconds());
        return maxIdle > 0L ? Long.valueOf(maxIdle) : null;
    }

    @Nullable
    public Long currentExpireAfterWriteMillis() {
        long timeToLive = TimeUnit.SECONDS.toMillis(this.getConfig().getTimeToLiveSeconds());
        return timeToLive > 0L ? Long.valueOf(timeToLive) : null;
    }

    @Nullable
    public Integer currentMaxEntries() {
        int maxSize = this.getConfig().getMaxSizeConfig().getSize();
        return maxSize > 0 ? Integer.valueOf(maxSize) : null;
    }

    public boolean isReplicateViaCopy() {
        return true;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public boolean isFlushable() {
        CacheSettings cacheSettings = this.getCacheSettings();
        return cacheSettings == null || cacheSettings.getFlushable(true);
    }

    public boolean isLocal() {
        return false;
    }

    public boolean isReplicateAsynchronously() {
        CacheSettings cacheSettings = this.getCacheSettings();
        return cacheSettings == null || cacheSettings.getReplicateAsynchronously(true);
    }

    public boolean updateExpireAfterAccess(long expireAfter, TimeUnit timeUnit) {
        CacheSettings newCacheSettings = new CacheSettingsBuilder(this.getCacheSettings()).expireAfterAccess(expireAfter, timeUnit).build();
        return this.cacheManager.updateCacheSettings(this.getHazelcastMapName(), newCacheSettings);
    }

    public boolean updateExpireAfterWrite(long expireAfter, TimeUnit timeUnit) {
        CacheSettings newCacheSettings = new CacheSettingsBuilder(this.getCacheSettings()).expireAfterWrite(expireAfter, timeUnit).build();
        return this.cacheManager.updateCacheSettings(this.getHazelcastMapName(), newCacheSettings);
    }

    public boolean updateMaxEntries(int newValue) {
        CacheSettings newCacheSettings = new CacheSettingsBuilder(this.getCacheSettings()).maxEntries(newValue).build();
        return this.cacheManager.updateCacheSettings(this.getHazelcastMapName(), newCacheSettings);
    }

    @Nonnull
    public SortedMap<CacheStatisticsKey, Supplier<Long>> getStatistics() {
        return ImmutableSortedMap.of();
    }

    public boolean isStatisticsEnabled() {
        return false;
    }

    public void setStatistics(boolean enabled) {
        throw new UnsupportedOperationException("setStatistics() not implemented");
    }

    @Nonnull
    protected abstract String getHazelcastMapName();

    @Nullable
    private MapConfig getConfig() {
        return this.cacheManager.getMapConfig(this.getHazelcastMapName());
    }

    @Nullable
    private CacheSettings getCacheSettings() {
        return this.cacheManager.getCacheSettings(this.getHazelcastMapName());
    }
}

