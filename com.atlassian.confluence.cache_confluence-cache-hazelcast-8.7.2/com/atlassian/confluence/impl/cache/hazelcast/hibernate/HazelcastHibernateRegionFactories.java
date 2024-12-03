/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.hibernate.HazelcastLocalCacheRegionFactory
 *  org.hibernate.cache.spi.RegionFactory
 */
package com.atlassian.confluence.impl.cache.hazelcast.hibernate;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.confluence.impl.cache.hazelcast.hibernate.LocalRegionCacheHazelcastInstanceProxyFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.HazelcastLocalCacheRegionFactory;
import java.util.function.Supplier;
import org.hibernate.cache.spi.RegionFactory;

@Internal
public final class HazelcastHibernateRegionFactories {
    public static RegionFactory createRegionFactory(Supplier<HazelcastInstance> hazelcastSupplier, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        HazelcastInstance proxiedHazelcastInstance = new LocalRegionCacheHazelcastInstanceProxyFactory(cacheSettingsDefaultsProvider).createProxy(hazelcastSupplier.get());
        return new HazelcastLocalCacheRegionFactory(proxiedHazelcastInstance);
    }

    private HazelcastHibernateRegionFactories() {
    }
}

