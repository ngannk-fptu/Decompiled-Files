/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import java.util.Map;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.NoCacheRegionFactoryAvailableException;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.internal.NoCachingTransactionSynchronizationImpl;
import org.hibernate.cache.spi.CacheTransactionSynchronization;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class NoCachingRegionFactory
implements RegionFactory {
    public static final NoCachingRegionFactory INSTANCE = new NoCachingRegionFactory();

    @Override
    public void start(SessionFactoryOptions settings, Map configValues) throws CacheException {
    }

    @Override
    public void stop() {
    }

    @Override
    public String qualify(String regionName) {
        return regionName;
    }

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return false;
    }

    @Override
    public AccessType getDefaultAccessType() {
        return null;
    }

    @Override
    public long nextTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public CacheTransactionSynchronization createTransactionContext(SharedSessionContractImplementor session) {
        return new NoCachingTransactionSynchronizationImpl(this);
    }

    @Override
    public DomainDataRegion buildDomainDataRegion(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        throw new NoCacheRegionFactoryAvailableException();
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, SessionFactoryImplementor sessionFactory) {
        throw new NoCacheRegionFactoryAvailableException();
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, SessionFactoryImplementor sessionFactory) {
        throw new NoCacheRegionFactoryAvailableException();
    }
}

