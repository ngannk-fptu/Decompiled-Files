/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.DefaultCacheSettings;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

@PublicApi
public class CacheSettingsBuilder {
    private Long expireAfterAccess;
    private Long expireAfterWrite;
    private Boolean flushable;
    private Boolean local;
    private Integer maxEntries;
    private Boolean replicateAsynchronously;
    private Boolean replicateViaCopy;
    private Boolean statisticsEnabled;

    public CacheSettingsBuilder() {
    }

    public CacheSettingsBuilder(CacheSettings settings) {
        this.expireAfterAccess = settings.getExpireAfterAccess();
        this.expireAfterWrite = settings.getExpireAfterWrite();
        this.flushable = settings.getFlushable();
        this.local = settings.getLocal();
        this.maxEntries = settings.getMaxEntries();
        this.replicateAsynchronously = settings.getReplicateAsynchronously();
        this.replicateViaCopy = settings.getReplicateViaCopy();
        this.statisticsEnabled = settings.getStatisticsEnabled();
    }

    @Nonnull
    public CacheSettings build() {
        return new DefaultCacheSettings(this.expireAfterAccess, this.expireAfterWrite, this.flushable, this.local, this.maxEntries, this.replicateAsynchronously, this.replicateViaCopy, this.statisticsEnabled);
    }

    @Nonnull
    public CacheSettingsBuilder expireAfterAccess(long expireAfter, @Nonnull TimeUnit timeUnit) {
        this.expireAfterAccess = timeUnit.toMillis(expireAfter);
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder expireAfterWrite(long expireAfter, @Nonnull TimeUnit timeUnit) {
        this.expireAfterWrite = timeUnit.toMillis(expireAfter);
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder flushable() {
        this.flushable = true;
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder unflushable() {
        this.flushable = false;
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder maxEntries(int maxEntries) {
        if (0 >= maxEntries) {
            throw new IllegalArgumentException("maxEntries must be greater than zero, passed: " + maxEntries);
        }
        this.maxEntries = maxEntries;
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder replicateAsynchronously() {
        this.replicateAsynchronously = true;
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder replicateSynchronously() {
        this.replicateAsynchronously = false;
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder replicateViaCopy() {
        this.replicateViaCopy = true;
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder replicateViaInvalidation() {
        this.replicateViaCopy = false;
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder local() {
        this.local = true;
        return this;
    }

    @Nonnull
    public CacheSettingsBuilder remote() {
        this.local = false;
        return this;
    }

    public CacheSettingsBuilder statisticsEnabled() {
        this.statisticsEnabled = true;
        return this;
    }

    public CacheSettingsBuilder statisticsDisabled() {
        this.statisticsEnabled = false;
        return this;
    }
}

