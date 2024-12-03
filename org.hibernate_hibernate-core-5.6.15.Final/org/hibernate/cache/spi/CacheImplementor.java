/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.io.Serializable;
import java.util.Locale;
import java.util.Set;
import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.QueryCache;
import org.hibernate.cache.spi.QueryResultsCache;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsCache;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.service.Service;

public interface CacheImplementor
extends Service,
Cache,
org.hibernate.engine.spi.CacheImplementor,
Serializable {
    @Override
    public SessionFactoryImplementor getSessionFactory();

    @Override
    public RegionFactory getRegionFactory();

    @Override
    public void prime(Set<DomainDataRegionConfig> var1);

    @Override
    public Region getRegion(String var1);

    @Override
    public Set<String> getCacheRegionNames();

    @Override
    public TimestampsCache getTimestampsCache();

    @Override
    public QueryResultsCache getDefaultQueryResultsCache();

    @Override
    public QueryResultsCache getQueryResultsCache(String var1);

    @Override
    public QueryResultsCache getQueryResultsCacheStrictly(String var1);

    @Override
    default public void evictQueries() throws HibernateException {
        QueryResultsCache cache = this.getDefaultQueryResultsCache();
        if (cache != null) {
            cache.clear();
        }
    }

    @Override
    public void close();

    @Override
    @Deprecated
    public String[] getSecondLevelCacheRegionNames();

    @Override
    @Deprecated
    public EntityDataAccess getEntityRegionAccess(NavigableRole var1);

    @Override
    @Deprecated
    public NaturalIdDataAccess getNaturalIdCacheRegionAccessStrategy(NavigableRole var1);

    @Override
    @Deprecated
    public CollectionDataAccess getCollectionRegionAccess(NavigableRole var1);

    @Override
    @Deprecated
    default public UpdateTimestampsCache getUpdateTimestampsCache() {
        return this.getTimestampsCache();
    }

    @Override
    @Deprecated
    default public QueryCache getQueryCache() {
        return this.getDefaultQueryResultsCache();
    }

    @Override
    @Deprecated
    default public QueryCache getDefaultQueryCache() {
        return this.getDefaultQueryResultsCache();
    }

    @Override
    @Deprecated
    default public QueryCache getQueryCache(String regionName) throws HibernateException {
        return this.getQueryResultsCache(this.unqualifyRegionName(regionName));
    }

    @Override
    @Deprecated
    default public String unqualifyRegionName(String name) {
        if (this.getSessionFactory().getSessionFactoryOptions().getCacheRegionPrefix() == null) {
            return name;
        }
        if (!name.startsWith(this.getSessionFactory().getSessionFactoryOptions().getCacheRegionPrefix())) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Legacy methods for accessing cache information expect a qualified (prefix) region name - but passed name [%s] was not qualified by the configured prefix [%s]", name, this.getSessionFactory().getSessionFactoryOptions().getCacheRegionPrefix()));
        }
        return name.substring(this.getSessionFactory().getSessionFactoryOptions().getCacheRegionPrefix().length() + 1);
    }

    @Override
    @Deprecated
    default public Region getRegionByLegacyName(String legacyName) {
        return this.getRegion(this.unqualifyRegionName(legacyName));
    }

    @Override
    @Deprecated
    public Set<NaturalIdDataAccess> getNaturalIdAccessesInRegion(String var1);
}

