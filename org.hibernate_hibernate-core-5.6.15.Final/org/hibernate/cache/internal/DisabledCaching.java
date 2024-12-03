/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cache.spi.QueryResultsCache;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsCache;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.NavigableRole;

public class DisabledCaching
implements CacheImplementor {
    private final SessionFactoryImplementor sessionFactory;
    private final RegionFactory regionFactory;

    public DisabledCaching(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.regionFactory = sessionFactory.getServiceRegistry().getService(RegionFactory.class);
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public RegionFactory getRegionFactory() {
        return this.regionFactory;
    }

    @Override
    public void prime(Set<DomainDataRegionConfig> cacheRegionConfigs) {
    }

    @Override
    public boolean containsEntity(Class entityClass, Serializable identifier) {
        return false;
    }

    @Override
    public boolean containsEntity(String entityName, Serializable identifier) {
        return false;
    }

    @Override
    public void evictEntityData(Class entityClass, Serializable identifier) {
    }

    @Override
    public void evictEntityData(String entityName, Serializable identifier) {
    }

    @Override
    public void evictEntityData(Class entityClass) {
    }

    @Override
    public void evictEntityData(String entityName) {
    }

    @Override
    public void evictEntityData() {
    }

    @Override
    public void evictNaturalIdData(Class entityClass) {
    }

    @Override
    public void evictNaturalIdData(String entityName) {
    }

    @Override
    public void evictNaturalIdData() {
    }

    @Override
    public boolean containsCollection(String role, Serializable ownerIdentifier) {
        return false;
    }

    @Override
    public void evictCollectionData(String role, Serializable ownerIdentifier) {
    }

    @Override
    public void evictCollectionData(String role) {
    }

    @Override
    public void evictCollectionData() {
    }

    @Override
    public boolean containsQuery(String regionName) {
        return false;
    }

    @Override
    public void evictDefaultQueryRegion() {
    }

    @Override
    public void evictQueryRegion(String regionName) {
    }

    @Override
    public void evictQueryRegions() {
    }

    @Override
    public void evictRegion(String regionName) {
    }

    @Override
    public Region getRegion(String fullRegionName) {
        return null;
    }

    @Override
    public TimestampsCache getTimestampsCache() {
        return null;
    }

    @Override
    public QueryResultsCache getDefaultQueryResultsCache() {
        return null;
    }

    @Override
    public QueryResultsCache getQueryResultsCache(String regionName) {
        return null;
    }

    @Override
    public QueryResultsCache getQueryResultsCacheStrictly(String regionName) {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public String[] getSecondLevelCacheRegionNames() {
        return new String[0];
    }

    @Override
    public Set<String> getCacheRegionNames() {
        return null;
    }

    @Override
    public EntityDataAccess getEntityRegionAccess(NavigableRole rootEntityName) {
        return null;
    }

    @Override
    public NaturalIdDataAccess getNaturalIdCacheRegionAccessStrategy(NavigableRole rootEntityName) {
        return null;
    }

    @Override
    public CollectionDataAccess getCollectionRegionAccess(NavigableRole collectionRole) {
        return null;
    }

    public boolean contains(Class cls, Object primaryKey) {
        return false;
    }

    public void evict(Class cls, Object primaryKey) {
    }

    public void evict(Class cls) {
    }

    public <T> T unwrap(Class<T> cls) {
        return (T)this;
    }

    @Override
    public Set<NaturalIdDataAccess> getNaturalIdAccessesInRegion(String regionName) {
        return Collections.emptySet();
    }
}

