/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl
 *  org.hibernate.boot.registry.selector.StrategyRegistration
 *  org.hibernate.boot.registry.selector.StrategyRegistrationProvider
 *  org.hibernate.cache.spi.RegionFactory
 */
package org.hibernate.cache.jcache.internal;

import java.util.Collections;
import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
import org.hibernate.boot.registry.selector.StrategyRegistration;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.hibernate.cache.spi.RegionFactory;

public final class StrategyRegistrationProviderImpl
implements StrategyRegistrationProvider {
    public Iterable<StrategyRegistration> getStrategyRegistrations() {
        SimpleStrategyRegistrationImpl simpleStrategyRegistration = new SimpleStrategyRegistrationImpl(RegionFactory.class, JCacheRegionFactory.class, new String[]{"jcache", JCacheRegionFactory.class.getName(), JCacheRegionFactory.class.getSimpleName(), "org.hibernate.cache.jcache.JCacheRegionFactory"});
        return Collections.singleton(simpleStrategyRegistration);
    }
}

