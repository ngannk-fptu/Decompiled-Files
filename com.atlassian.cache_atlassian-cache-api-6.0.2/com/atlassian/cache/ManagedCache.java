/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.instrumentation.caches.CacheCollector
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.instrumentation.caches.CacheCollector;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
public interface ManagedCache {
    public void clear();

    @Nonnull
    public String getName();

    public boolean isFlushable();

    @Nullable
    public Integer currentMaxEntries();

    public boolean updateMaxEntries(int var1);

    @Nullable
    public Long currentExpireAfterAccessMillis();

    public boolean updateExpireAfterAccess(long var1, @Nonnull TimeUnit var3);

    @Nullable
    public Long currentExpireAfterWriteMillis();

    public boolean updateExpireAfterWrite(long var1, @Nonnull TimeUnit var3);

    public boolean isLocal();

    public boolean isReplicateAsynchronously();

    public boolean isReplicateViaCopy();

    public boolean isStatisticsEnabled();

    @Deprecated
    public void setStatistics(boolean var1);

    @Nonnull
    public SortedMap<CacheStatisticsKey, Supplier<Long>> getStatistics();

    @Nullable
    default public CacheCollector getCacheCollector() {
        return null;
    }
}

