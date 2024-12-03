/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.support.CollectionTransactionAccess;
import org.hibernate.cache.spi.support.DomainDataRegionTemplate;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.EntityTransactionalAccess;
import org.hibernate.cache.spi.support.NaturalIdTransactionalAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;

public class DomainDataRegionImpl
extends DomainDataRegionTemplate {
    public DomainDataRegionImpl(DomainDataRegionConfig regionConfig, RegionFactoryTemplate regionFactory, DomainDataStorageAccess domainDataStorageAccess, CacheKeysFactory defaultKeysFactory, DomainDataRegionBuildingContext buildingContext) {
        super(regionConfig, regionFactory, domainDataStorageAccess, defaultKeysFactory, buildingContext);
    }

    @Override
    protected EntityDataAccess generateTransactionalEntityDataAccess(EntityDataCachingConfig entityAccessConfig) {
        return new EntityTransactionalAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), entityAccessConfig);
    }

    @Override
    protected NaturalIdDataAccess generateTransactionalNaturalIdDataAccess(NaturalIdDataCachingConfig accessConfig) {
        return new NaturalIdTransactionalAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    @Override
    protected CollectionDataAccess generateTransactionalCollectionDataAccess(CollectionDataCachingConfig accessConfig) {
        return new CollectionTransactionAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }
}

