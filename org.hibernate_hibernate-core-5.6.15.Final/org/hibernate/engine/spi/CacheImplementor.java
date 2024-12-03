/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

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

@Deprecated
public interface CacheImplementor
extends Service,
Cache,
Serializable {
    @Override
    public SessionFactoryImplementor getSessionFactory();

    public RegionFactory getRegionFactory();

    public void prime(Set<DomainDataRegionConfig> var1);

    public Region getRegion(String var1);

    public Set<String> getCacheRegionNames();

    public TimestampsCache getTimestampsCache();

    public QueryResultsCache getDefaultQueryResultsCache();

    public QueryResultsCache getQueryResultsCache(String var1);

    public QueryResultsCache getQueryResultsCacheStrictly(String var1);

    default public void evictQueries() throws HibernateException {
        QueryResultsCache cache = this.getDefaultQueryResultsCache();
        if (cache != null) {
            cache.clear();
        }
    }

    public void close();

    @Deprecated
    public String[] getSecondLevelCacheRegionNames();

    @Deprecated
    public EntityDataAccess getEntityRegionAccess(NavigableRole var1);

    @Deprecated
    public NaturalIdDataAccess getNaturalIdCacheRegionAccessStrategy(NavigableRole var1);

    @Deprecated
    public CollectionDataAccess getCollectionRegionAccess(NavigableRole var1);

    @Deprecated
    default public UpdateTimestampsCache getUpdateTimestampsCache() {
        return this.getTimestampsCache();
    }

    @Deprecated
    default public QueryCache getQueryCache() {
        return this.getDefaultQueryResultsCache();
    }

    @Deprecated
    default public QueryCache getDefaultQueryCache() {
        return this.getDefaultQueryResultsCache();
    }

    @Deprecated
    default public QueryCache getQueryCache(String regionName) throws HibernateException {
        return this.getQueryResultsCache(this.unqualifyRegionName(regionName));
    }

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

    @Deprecated
    default public Region getRegionByLegacyName(String legacyName) {
        return this.getRegion(this.unqualifyRegionName(legacyName));
    }

    @Deprecated
    public Set<NaturalIdDataAccess> getNaturalIdAccessesInRegion(String var1);
}

