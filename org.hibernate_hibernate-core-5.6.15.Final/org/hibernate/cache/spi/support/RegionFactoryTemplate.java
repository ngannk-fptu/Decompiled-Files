/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.AbstractRegionFactory;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.support.DomainDataRegionTemplate;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.QueryResultsRegionTemplate;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.cache.spi.support.TimestampsRegionTemplate;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public abstract class RegionFactoryTemplate
extends AbstractRegionFactory {
    @Override
    public DomainDataRegion buildDomainDataRegion(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        this.verifyStarted();
        return new DomainDataRegionTemplate(regionConfig, this, this.createDomainDataStorageAccess(regionConfig, buildingContext), this.getImplicitCacheKeysFactory(), buildingContext);
    }

    protected CacheKeysFactory getImplicitCacheKeysFactory() {
        return DefaultCacheKeysFactory.INSTANCE;
    }

    protected DomainDataStorageAccess createDomainDataStorageAccess(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        throw new UnsupportedOperationException("Not implemented by caching provider");
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, SessionFactoryImplementor sessionFactory) {
        this.verifyStarted();
        return new QueryResultsRegionTemplate(regionName, this, this.createQueryResultsRegionStorageAccess(regionName, sessionFactory));
    }

    protected abstract StorageAccess createQueryResultsRegionStorageAccess(String var1, SessionFactoryImplementor var2);

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, SessionFactoryImplementor sessionFactory) {
        this.verifyStarted();
        return new TimestampsRegionTemplate(regionName, this, this.createTimestampsRegionStorageAccess(regionName, sessionFactory));
    }

    protected abstract StorageAccess createTimestampsRegionStorageAccess(String var1, SessionFactoryImplementor var2);
}

