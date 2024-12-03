/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.ManagedCache
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.impl.cache.hibernate;

import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.impl.cache.DelegatingCacheManager;
import com.atlassian.confluence.impl.cache.hibernate.HibernateManagedCacheSupplier;
import io.atlassian.fugue.Option;
import java.util.Collection;

abstract class HibernateManagedCacheAwareCacheManager
extends DelegatingCacheManager {
    private final HibernateManagedCacheSupplier hibernateManagedRegionCacheLookup;

    public HibernateManagedCacheAwareCacheManager(HibernateManagedCacheSupplier hibernateManagedRegionCacheLookup) {
        this.hibernateManagedRegionCacheLookup = hibernateManagedRegionCacheLookup;
    }

    @Override
    public Collection<ManagedCache> getManagedCaches() {
        return this.hibernateManagedRegionCacheLookup.getManagedCaches(this.getDelegate().getManagedCaches());
    }

    @Override
    public ManagedCache getManagedCache(String name) {
        return (ManagedCache)this.hibernateManagedRegionCacheLookup.getManagedCache(name).orElse(() -> {
            this.getDelegate().getCache(name);
            return Option.option((Object)this.getDelegate().getManagedCache(name));
        }).getOrNull();
    }
}

