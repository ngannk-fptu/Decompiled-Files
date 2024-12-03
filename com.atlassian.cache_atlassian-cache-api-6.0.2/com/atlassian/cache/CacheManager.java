/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.ManagedCache;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
public interface CacheManager
extends CacheFactory {
    @Deprecated
    @Nonnull
    public Collection<Cache<?, ?>> getCaches();

    @Nonnull
    public Collection<ManagedCache> getManagedCaches();

    public void flushCaches();

    @Nullable
    public ManagedCache getManagedCache(@Nonnull String var1);

    public void shutdown();
}

