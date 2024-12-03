/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.cache.cfg.spi.CollectionDataCachingConfig
 *  org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext
 *  org.hibernate.cache.cfg.spi.DomainDataRegionConfig
 *  org.hibernate.cache.cfg.spi.EntityDataCachingConfig
 *  org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig
 *  org.hibernate.cache.spi.CacheKeysFactory
 *  org.hibernate.cache.spi.SecondLevelCacheLogger
 *  org.hibernate.cache.spi.access.AccessType
 *  org.hibernate.cache.spi.access.CollectionDataAccess
 *  org.hibernate.cache.spi.access.EntityDataAccess
 *  org.hibernate.cache.spi.access.NaturalIdDataAccess
 *  org.hibernate.cache.spi.support.DomainDataRegionImpl
 *  org.hibernate.cache.spi.support.DomainDataStorageAccess
 *  org.hibernate.cache.spi.support.RegionFactoryTemplate
 */
package org.hibernate.cache.jcache.internal;

import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.SecondLevelCacheLogger;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.support.DomainDataRegionImpl;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;

public class JCacheDomainDataRegionImpl
extends DomainDataRegionImpl {
    public JCacheDomainDataRegionImpl(DomainDataRegionConfig regionConfig, RegionFactoryTemplate regionFactory, DomainDataStorageAccess domainDataStorageAccess, CacheKeysFactory defaultKeysFactory, DomainDataRegionBuildingContext buildingContext) {
        super(regionConfig, regionFactory, domainDataStorageAccess, defaultKeysFactory, buildingContext);
    }

    protected EntityDataAccess generateTransactionalEntityDataAccess(EntityDataCachingConfig entityAccessConfig) {
        SecondLevelCacheLogger.INSTANCE.nonStandardSupportForAccessType(this.getName(), AccessType.TRANSACTIONAL.getExternalName(), this.getRegionFactory().getClass().getSimpleName());
        return super.generateTransactionalEntityDataAccess(entityAccessConfig);
    }

    protected NaturalIdDataAccess generateTransactionalNaturalIdDataAccess(NaturalIdDataCachingConfig accessConfig) {
        SecondLevelCacheLogger.INSTANCE.nonStandardSupportForAccessType(this.getName(), AccessType.TRANSACTIONAL.getExternalName(), this.getRegionFactory().getClass().getSimpleName());
        return super.generateTransactionalNaturalIdDataAccess(accessConfig);
    }

    protected CollectionDataAccess generateTransactionalCollectionDataAccess(CollectionDataCachingConfig accessConfig) {
        SecondLevelCacheLogger.INSTANCE.nonStandardSupportForAccessType(this.getName(), AccessType.TRANSACTIONAL.getExternalName(), this.getRegionFactory().getClass().getSimpleName());
        return super.generateTransactionalCollectionDataAccess(accessConfig);
    }
}

