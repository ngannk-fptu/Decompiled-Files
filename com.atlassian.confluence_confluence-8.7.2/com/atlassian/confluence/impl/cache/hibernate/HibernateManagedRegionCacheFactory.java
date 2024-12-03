/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.cache.ManagedCache
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 *  org.hibernate.cache.spi.Region
 *  org.hibernate.cache.spi.access.CachedDomainDataAccess
 *  org.hibernate.engine.spi.CacheImplementor
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.metamodel.model.domain.NavigableRole
 *  org.hibernate.stat.CacheRegionStatistics
 */
package com.atlassian.confluence.impl.cache.hibernate;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.impl.cache.hibernate.HibernateManagedCacheSupplier;
import com.atlassian.confluence.impl.cache.hibernate.ManagedHazelcastHibernateRegionCache;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.access.CachedDomainDataAccess;
import org.hibernate.engine.spi.CacheImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.stat.CacheRegionStatistics;

public final class HibernateManagedRegionCacheFactory
implements HibernateManagedCacheSupplier {
    private final SessionFactoryImplementor sessionFactory;
    private final CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider;

    public HibernateManagedRegionCacheFactory(SessionFactoryImplementor sessionFactory, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        this.cacheSettingsDefaultsProvider = Objects.requireNonNull(cacheSettingsDefaultsProvider);
    }

    @Override
    public Collection<ManagedCache> getAllManagedCaches() {
        return this.getHibernateCache().getCacheRegionNames().stream().flatMap(regionName -> this.getManagedCache((String)regionName).toStream()).collect(Collectors.toList());
    }

    @Override
    public Option<ManagedCache> getManagedCache(String name) {
        return this.getRegionAndAccessStrategy(name).map(region -> this.createManagedHibernateRegionCache(name, (Region)region.left(), (CachedDomainDataAccess)region.right()));
    }

    private ManagedHazelcastHibernateRegionCache createManagedHibernateRegionCache(String regionName, Region region, CachedDomainDataAccess domainDataAccess) {
        return new ManagedHazelcastHibernateRegionCache(regionName, this.getCacheStatistics(regionName), domainDataAccess, this.cacheSettings(region));
    }

    private CacheSettings cacheSettings(Region region) {
        return new CacheSettingsBuilder(this.cacheSettingsDefaultsProvider.getDefaults(region.getName())).replicateAsynchronously().replicateViaInvalidation().build();
    }

    private CacheRegionStatistics getCacheStatistics(String name) {
        return this.sessionFactory.getStatistics().getCacheRegionStatistics(name);
    }

    private CacheImplementor getHibernateCache() {
        return this.sessionFactory.getCache();
    }

    private Option<Pair<Region, CachedDomainDataAccess>> getRegionAndAccessStrategy(String name) {
        NavigableRole rootEntityRole = new NavigableRole(name);
        return this.getNaturalIdRegionAndAccessStrategy(rootEntityRole).orElse(() -> this.getEntityRegionAndAccessStrategy(rootEntityRole)).orElse(() -> this.getCollectionRegionAndAccessStrategy(rootEntityRole));
    }

    private Option<Pair<Region, CachedDomainDataAccess>> getEntityRegionAndAccessStrategy(NavigableRole rootEntityRole) {
        return Option.option((Object)this.sessionFactory.getCache().getEntityRegionAccess(rootEntityRole)).map(accessStrategy -> Pair.pair((Object)accessStrategy.getRegion(), (Object)accessStrategy));
    }

    private Option<Pair<Region, CachedDomainDataAccess>> getNaturalIdRegionAndAccessStrategy(NavigableRole rootEntityRole) {
        return Option.option((Object)this.sessionFactory.getCache().getNaturalIdCacheRegionAccessStrategy(rootEntityRole)).map(accessStrategy -> Pair.pair((Object)accessStrategy.getRegion(), (Object)accessStrategy));
    }

    private Option<Pair<Region, CachedDomainDataAccess>> getCollectionRegionAndAccessStrategy(NavigableRole rootEntityRole) {
        return Option.option((Object)this.sessionFactory.getCache().getCollectionRegionAccess(rootEntityRole)).map(accessStrategy -> Pair.pair((Object)accessStrategy.getRegion(), (Object)accessStrategy));
    }
}

