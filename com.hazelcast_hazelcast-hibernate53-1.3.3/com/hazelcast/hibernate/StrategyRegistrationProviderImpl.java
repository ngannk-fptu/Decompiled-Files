/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl
 *  org.hibernate.boot.registry.selector.StrategyRegistration
 *  org.hibernate.boot.registry.selector.StrategyRegistrationProvider
 *  org.hibernate.cache.spi.RegionFactory
 */
package com.hazelcast.hibernate;

import com.hazelcast.hibernate.HazelcastCacheRegionFactory;
import com.hazelcast.hibernate.HazelcastLocalCacheRegionFactory;
import java.util.ArrayList;
import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
import org.hibernate.boot.registry.selector.StrategyRegistration;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.cache.spi.RegionFactory;

public class StrategyRegistrationProviderImpl
implements StrategyRegistrationProvider {
    public Iterable<StrategyRegistration> getStrategyRegistrations() {
        ArrayList<StrategyRegistration> strategyRegistrations = new ArrayList<StrategyRegistration>();
        strategyRegistrations.add((StrategyRegistration)new SimpleStrategyRegistrationImpl(RegionFactory.class, HazelcastLocalCacheRegionFactory.class, new String[]{"hazelcast-local", HazelcastLocalCacheRegionFactory.class.getName(), HazelcastLocalCacheRegionFactory.class.getSimpleName()}));
        strategyRegistrations.add((StrategyRegistration)new SimpleStrategyRegistrationImpl(RegionFactory.class, HazelcastCacheRegionFactory.class, new String[]{"hazelcast", HazelcastCacheRegionFactory.class.getName(), HazelcastCacheRegionFactory.class.getSimpleName()}));
        return strategyRegistrations;
    }
}

