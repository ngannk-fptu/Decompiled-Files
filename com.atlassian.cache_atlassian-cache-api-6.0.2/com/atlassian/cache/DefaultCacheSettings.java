/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheSettings;
import java.io.Serializable;
import javax.annotation.Nonnull;

@Internal
class DefaultCacheSettings
implements CacheSettings,
Serializable {
    private static final long serialVersionUID = 1L;
    private final Long expireAfterAccess;
    private final Long expireAfterWrite;
    private final Boolean flushable;
    private final Boolean local;
    private final Integer maxEntries;
    private final Boolean replicateAsynchronously;
    private final Boolean replicateViaCopy;
    private final Boolean statisticsEnabled;

    DefaultCacheSettings(Long expireAfterAccess, Long expireAfterWrite, Boolean flushable, Boolean local, Integer maxEntries, Boolean replicateAsynchronously, Boolean replicateViaCopy, Boolean statisticsEnabled) {
        this.expireAfterAccess = expireAfterAccess;
        this.expireAfterWrite = expireAfterWrite;
        this.flushable = flushable;
        this.local = local;
        this.maxEntries = maxEntries;
        this.replicateAsynchronously = replicateAsynchronously;
        this.replicateViaCopy = replicateViaCopy;
        this.statisticsEnabled = statisticsEnabled;
    }

    @Override
    @Nonnull
    public CacheSettings override(@Nonnull CacheSettings overrides) {
        return new DefaultCacheSettings(DefaultCacheSettings.notNullElse(overrides.getExpireAfterAccess(), this.expireAfterAccess), DefaultCacheSettings.notNullElse(overrides.getExpireAfterWrite(), this.expireAfterWrite), DefaultCacheSettings.notNullElse(overrides.getFlushable(), this.flushable), DefaultCacheSettings.notNullElse(overrides.getLocal(), this.local), DefaultCacheSettings.notNullElse(overrides.getMaxEntries(), this.maxEntries), DefaultCacheSettings.notNullElse(overrides.getReplicateAsynchronously(), this.replicateAsynchronously), DefaultCacheSettings.notNullElse(overrides.getReplicateViaCopy(), this.replicateViaCopy), DefaultCacheSettings.notNullElse(overrides.getStatisticsEnabled(), this.statisticsEnabled));
    }

    @Override
    public Long getExpireAfterAccess() {
        return this.expireAfterAccess;
    }

    @Override
    public long getExpireAfterAccess(long defaultValue) {
        return null == this.getExpireAfterAccess() ? defaultValue : this.getExpireAfterAccess();
    }

    @Override
    public Long getExpireAfterWrite() {
        return this.expireAfterWrite;
    }

    @Override
    public long getExpireAfterWrite(long defaultValue) {
        return null == this.getExpireAfterWrite() ? defaultValue : this.getExpireAfterWrite();
    }

    @Override
    public Boolean getFlushable() {
        return this.flushable;
    }

    @Override
    public boolean getFlushable(boolean defaultValue) {
        return null == this.getFlushable() ? defaultValue : this.getFlushable();
    }

    @Override
    public Boolean getLocal() {
        return this.local;
    }

    @Override
    public boolean getLocal(boolean defaultValue) {
        return null == this.getLocal() ? defaultValue : this.getLocal();
    }

    @Override
    public Integer getMaxEntries() {
        return this.maxEntries;
    }

    @Override
    public int getMaxEntries(int defaultValue) {
        return null == this.getMaxEntries() ? defaultValue : this.getMaxEntries();
    }

    @Override
    public Boolean getReplicateAsynchronously() {
        return this.replicateAsynchronously;
    }

    @Override
    public boolean getReplicateAsynchronously(boolean defaultValue) {
        return null == this.getReplicateAsynchronously() ? defaultValue : this.getReplicateAsynchronously();
    }

    @Override
    public Boolean getReplicateViaCopy() {
        return this.replicateViaCopy;
    }

    @Override
    public boolean getReplicateViaCopy(boolean defaultValue) {
        return null == this.getReplicateViaCopy() ? defaultValue : this.getReplicateViaCopy();
    }

    @Override
    public Boolean getStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    @Override
    public boolean getStatisticsEnabled(boolean defaultValue) {
        return null == this.getStatisticsEnabled() ? defaultValue : this.getStatisticsEnabled();
    }

    private static <T> T notNullElse(T value, T def) {
        return null != value ? value : def;
    }
}

