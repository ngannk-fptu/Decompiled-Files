/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.impl.cache.DelegatingCacheManager
 *  com.atlassian.confluence.impl.cache.hibernate.HibernateManagedCacheSupplier
 *  com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelistService
 *  com.atlassian.confluence.util.profiling.ConfluenceMonitoring
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Suppliers
 */
package com.atlassian.confluence.cache.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.hazelcast.HazelcastCacheManagerFactory;
import com.atlassian.confluence.impl.cache.DelegatingCacheManager;
import com.atlassian.confluence.impl.cache.hazelcast.HibernateManagedRegionCacheLookup;
import com.atlassian.confluence.impl.cache.hibernate.HibernateManagedCacheSupplier;
import com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelistService;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Suppliers;
import java.util.Collection;
import java.util.function.Supplier;

@Deprecated(forRemoval=true)
@Internal
public class ConfluenceHazelcastCacheManager
extends DelegatingCacheManager {
    private final Supplier<CacheManager> delegateRef;
    private final HibernateManagedCacheSupplier hibernateManagedRegionCacheLookup;

    public ConfluenceHazelcastCacheManager(HazelcastCacheManagerFactory delegateFactory, HibernateManagedCacheSupplier hibernateManagedRegionCacheLookup) {
        this.delegateRef = Suppliers.memoize(delegateFactory::create);
        this.hibernateManagedRegionCacheLookup = hibernateManagedRegionCacheLookup;
    }

    @Deprecated(forRemoval=true)
    public ConfluenceHazelcastCacheManager(HazelcastCacheManagerFactory delegateFactory, HibernateManagedRegionCacheLookup hibernateManagedRegionCacheLookup) {
        this(delegateFactory, (HibernateManagedCacheSupplier)hibernateManagedRegionCacheLookup);
    }

    @Deprecated(forRemoval=true)
    public ConfluenceHazelcastCacheManager(ConfluenceMonitoring confluenceMonitoring, HazelcastCacheManagerFactory delegateFactory, CacheOperationsWhitelistService operationsWhitelistService, HibernateManagedRegionCacheLookup hibernateManagedRegionCacheLookup) {
        this.delegateRef = Suppliers.memoize(delegateFactory::create);
        this.hibernateManagedRegionCacheLookup = hibernateManagedRegionCacheLookup;
    }

    protected CacheManager getDelegate() {
        return this.delegateRef.get();
    }

    public Collection<ManagedCache> getManagedCaches() {
        return this.hibernateManagedRegionCacheLookup.getManagedCaches(this.getDelegate().getManagedCaches());
    }

    public ManagedCache getManagedCache(String name) {
        return (ManagedCache)this.hibernateManagedRegionCacheLookup.getManagedCache(name).orElse(() -> {
            this.getDelegate().getCache(name);
            return Option.option((Object)this.getDelegate().getManagedCache(name));
        }).getOrNull();
    }
}

