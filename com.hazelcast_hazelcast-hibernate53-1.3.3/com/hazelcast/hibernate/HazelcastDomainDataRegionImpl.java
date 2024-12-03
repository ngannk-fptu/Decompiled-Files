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
 *  org.hibernate.cache.spi.DomainDataRegion
 *  org.hibernate.cache.spi.access.AccessType
 *  org.hibernate.cache.spi.access.CollectionDataAccess
 *  org.hibernate.cache.spi.access.EntityDataAccess
 *  org.hibernate.cache.spi.access.NaturalIdDataAccess
 *  org.hibernate.cache.spi.access.SoftLock
 *  org.hibernate.cache.spi.support.CollectionReadWriteAccess
 *  org.hibernate.cache.spi.support.CollectionTransactionAccess
 *  org.hibernate.cache.spi.support.DomainDataRegionImpl
 *  org.hibernate.cache.spi.support.DomainDataStorageAccess
 *  org.hibernate.cache.spi.support.EntityReadWriteAccess
 *  org.hibernate.cache.spi.support.EntityTransactionalAccess
 *  org.hibernate.cache.spi.support.NaturalIdReadWriteAccess
 *  org.hibernate.cache.spi.support.NaturalIdTransactionalAccess
 *  org.hibernate.cache.spi.support.RegionFactoryTemplate
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.hazelcast.hibernate;

import com.hazelcast.hibernate.HazelcastStorageAccess;
import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.CollectionReadWriteAccess;
import org.hibernate.cache.spi.support.CollectionTransactionAccess;
import org.hibernate.cache.spi.support.DomainDataRegionImpl;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.EntityReadWriteAccess;
import org.hibernate.cache.spi.support.EntityTransactionalAccess;
import org.hibernate.cache.spi.support.NaturalIdReadWriteAccess;
import org.hibernate.cache.spi.support.NaturalIdTransactionalAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class HazelcastDomainDataRegionImpl
extends DomainDataRegionImpl {
    HazelcastDomainDataRegionImpl(DomainDataRegionConfig regionConfig, RegionFactoryTemplate regionFactory, DomainDataStorageAccess domainDataStorageAccess, CacheKeysFactory defaultKeysFactory, DomainDataRegionBuildingContext buildingContext) {
        super(regionConfig, regionFactory, domainDataStorageAccess, defaultKeysFactory, buildingContext);
    }

    public CollectionDataAccess generateCollectionAccess(CollectionDataCachingConfig accessConfig) {
        if (accessConfig.getAccessType() == AccessType.READ_WRITE) {
            return this.generateReadWriteCollectionAccess(accessConfig);
        }
        return super.generateCollectionAccess(accessConfig);
    }

    protected EntityDataAccess generateReadWriteEntityAccess(EntityDataCachingConfig accessConfig) {
        return new EntityReadWriteAccess((DomainDataRegion)this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig){

            public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
                boolean result = super.afterUpdate(session, key, value, currentVersion, previousVersion, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).afterUpdate(key, value, currentVersion);
                return result;
            }

            public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
                super.unlockItem(session, key, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).unlockItem(key, lock);
            }
        };
    }

    protected NaturalIdDataAccess generateReadWriteNaturalIdAccess(NaturalIdDataCachingConfig accessConfig) {
        return new NaturalIdReadWriteAccess((DomainDataRegion)this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig){

            public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) {
                boolean result = super.afterUpdate(session, key, value, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).afterUpdate(key, value, null);
                return result;
            }

            public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
                super.unlockItem(session, key, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).unlockItem(key, lock);
            }
        };
    }

    protected CollectionDataAccess generateTransactionalCollectionDataAccess(CollectionDataCachingConfig accessConfig) {
        return new CollectionTransactionAccess((DomainDataRegion)this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig){

            public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
                super.unlockItem(session, key, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).unlockItem(key, lock);
            }
        };
    }

    protected EntityDataAccess generateTransactionalEntityDataAccess(EntityDataCachingConfig accessConfig) {
        return new EntityTransactionalAccess((DomainDataRegion)this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig){

            public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
                boolean result = super.afterUpdate(session, key, value, currentVersion, previousVersion, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).afterUpdate(key, value, currentVersion);
                return result;
            }

            public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
                super.unlockItem(session, key, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).unlockItem(key, lock);
            }
        };
    }

    protected NaturalIdDataAccess generateTransactionalNaturalIdDataAccess(NaturalIdDataCachingConfig accessConfig) {
        return new NaturalIdTransactionalAccess((DomainDataRegion)this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig){

            public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) {
                boolean result = super.afterUpdate(session, key, value, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).afterUpdate(key, value, null);
                return result;
            }

            public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
                super.unlockItem(session, key, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).unlockItem(key, lock);
            }
        };
    }

    private CollectionDataAccess generateReadWriteCollectionAccess(CollectionDataCachingConfig accessConfig) {
        return new CollectionReadWriteAccess((DomainDataRegion)this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig){

            public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
                super.unlockItem(session, key, lock);
                ((HazelcastStorageAccess)this.getStorageAccess()).unlockItem(key, lock);
            }
        };
    }
}

