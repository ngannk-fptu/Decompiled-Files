/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.ManagedCache
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.sf.ehcache.Ehcache
 */
package com.atlassian.cache.ehcache;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.ManagedCache;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.sf.ehcache.Ehcache;

@ParametersAreNonnullByDefault
abstract class ManagedCacheSupport
implements ManagedCache {
    @Nonnull
    private final Ehcache delegate;
    @Nonnull
    private final CacheSettings settings;

    public ManagedCacheSupport(Ehcache delegate, CacheSettings settings) {
        this.delegate = (Ehcache)Preconditions.checkNotNull((Object)delegate, (Object)"Ehcache delegate cannot be null");
        this.settings = (CacheSettings)Preconditions.checkNotNull((Object)settings, (Object)"CacheSettings cannot be null");
    }

    @Nonnull
    public final String getName() {
        return this.delegate.getName();
    }

    public final boolean isFlushable() {
        return this.settings.getFlushable(true);
    }

    public final boolean isLocal() {
        return this.settings.getLocal(false);
    }

    public final boolean isReplicateAsynchronously() {
        return this.settings.getReplicateAsynchronously(false);
    }

    public final boolean isReplicateViaCopy() {
        return this.settings.getReplicateViaCopy(false);
    }

    @Nonnull
    public final Integer currentMaxEntries() {
        return (int)this.delegate.getCacheConfiguration().getMaxEntriesLocalHeap();
    }

    public final boolean updateMaxEntries(int newValue) {
        this.delegate.getCacheConfiguration().setMaxEntriesLocalHeap((long)newValue);
        return true;
    }

    @Nonnull
    public final Long currentExpireAfterAccessMillis() {
        return TimeUnit.MILLISECONDS.convert(this.delegate.getCacheConfiguration().getTimeToIdleSeconds(), TimeUnit.SECONDS);
    }

    public final boolean updateExpireAfterAccess(long expireAfter, @Nonnull TimeUnit timeUnit) {
        this.delegate.getCacheConfiguration().setTimeToIdleSeconds(timeUnit.toSeconds(expireAfter));
        return true;
    }

    @Nonnull
    public final Long currentExpireAfterWriteMillis() {
        return TimeUnit.MILLISECONDS.convert(this.delegate.getCacheConfiguration().getTimeToLiveSeconds(), TimeUnit.SECONDS);
    }

    public final boolean updateExpireAfterWrite(long expireAfter, @Nonnull TimeUnit timeUnit) {
        this.delegate.getCacheConfiguration().setTimeToLiveSeconds(timeUnit.toSeconds(expireAfter));
        return true;
    }

    public boolean isStatisticsEnabled() {
        return this.settings.getStatisticsEnabled(true);
    }

    public void setStatistics(boolean enabled) {
        throw new UnsupportedOperationException("setStatistics() not implemented");
    }
}

