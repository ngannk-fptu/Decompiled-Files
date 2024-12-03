/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.support.AbstractDomainDataRegion;
import org.hibernate.cache.spi.support.CollectionNonStrictReadWriteAccess;
import org.hibernate.cache.spi.support.CollectionReadOnlyAccess;
import org.hibernate.cache.spi.support.CollectionReadWriteAccess;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.EntityNonStrictReadWriteAccess;
import org.hibernate.cache.spi.support.EntityReadOnlyAccess;
import org.hibernate.cache.spi.support.EntityReadWriteAccess;
import org.hibernate.cache.spi.support.NaturalIdNonStrictReadWriteAccess;
import org.hibernate.cache.spi.support.NaturalIdReadOnlyAccess;
import org.hibernate.cache.spi.support.NaturalIdReadWriteAccess;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.jboss.logging.Logger;

public class DomainDataRegionTemplate
extends AbstractDomainDataRegion {
    private static final Logger log = Logger.getLogger(DomainDataRegionTemplate.class);
    private final DomainDataStorageAccess storageAccess;

    public DomainDataRegionTemplate(DomainDataRegionConfig regionConfig, RegionFactory regionFactory, DomainDataStorageAccess storageAccess, CacheKeysFactory defaultKeysFactory, DomainDataRegionBuildingContext buildingContext) {
        super(regionConfig, regionFactory, defaultKeysFactory, buildingContext);
        this.storageAccess = storageAccess;
        this.completeInstantiation(regionConfig, buildingContext);
    }

    public DomainDataStorageAccess getCacheStorageAccess() {
        return this.storageAccess;
    }

    @Override
    public EntityDataAccess generateEntityAccess(EntityDataCachingConfig entityAccessConfig) {
        NavigableRole namedEntityRole = entityAccessConfig.getNavigableRole();
        AccessType accessType = entityAccessConfig.getAccessType();
        log.debugf("Generating entity cache access [%s] : %s", (Object)accessType.getExternalName(), (Object)namedEntityRole);
        switch (accessType) {
            case READ_ONLY: {
                return this.generateReadOnlyEntityAccess(entityAccessConfig);
            }
            case READ_WRITE: {
                return this.generateReadWriteEntityAccess(entityAccessConfig);
            }
            case NONSTRICT_READ_WRITE: {
                return this.generateNonStrictReadWriteEntityAccess(entityAccessConfig);
            }
            case TRANSACTIONAL: {
                return this.generateTransactionalEntityDataAccess(entityAccessConfig);
            }
        }
        throw new IllegalArgumentException("Unrecognized cache AccessType - " + (Object)((Object)accessType));
    }

    protected EntityDataAccess generateReadOnlyEntityAccess(EntityDataCachingConfig accessConfig) {
        return new EntityReadOnlyAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    protected EntityDataAccess generateReadWriteEntityAccess(EntityDataCachingConfig accessConfig) {
        return new EntityReadWriteAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    protected EntityDataAccess generateNonStrictReadWriteEntityAccess(EntityDataCachingConfig accessConfig) {
        return new EntityNonStrictReadWriteAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    protected EntityDataAccess generateTransactionalEntityDataAccess(EntityDataCachingConfig entityAccessConfig) {
        throw this.generateTransactionalNotSupportedException();
    }

    private UnsupportedOperationException generateTransactionalNotSupportedException() {
        return new UnsupportedOperationException("Cache provider [" + this.getRegionFactory() + "] does not support `" + AccessType.TRANSACTIONAL.getExternalName() + "` access");
    }

    @Override
    public NaturalIdDataAccess generateNaturalIdAccess(NaturalIdDataCachingConfig accessConfig) {
        NavigableRole namedEntityRole = accessConfig.getNavigableRole();
        AccessType accessType = accessConfig.getAccessType();
        log.debugf("Generating entity natural-id access [%s] : %s", (Object)accessType.getExternalName(), (Object)namedEntityRole);
        switch (accessType) {
            case READ_ONLY: {
                return this.generateReadOnlyNaturalIdAccess(accessConfig);
            }
            case READ_WRITE: {
                return this.generateReadWriteNaturalIdAccess(accessConfig);
            }
            case NONSTRICT_READ_WRITE: {
                return this.generateNonStrictReadWriteNaturalIdAccess(accessConfig);
            }
            case TRANSACTIONAL: {
                return this.generateTransactionalNaturalIdDataAccess(accessConfig);
            }
        }
        throw new IllegalArgumentException("Unrecognized cache AccessType - " + (Object)((Object)accessType));
    }

    protected NaturalIdDataAccess generateReadOnlyNaturalIdAccess(NaturalIdDataCachingConfig accessConfig) {
        return new NaturalIdReadOnlyAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    protected NaturalIdDataAccess generateReadWriteNaturalIdAccess(NaturalIdDataCachingConfig accessConfig) {
        return new NaturalIdReadWriteAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    protected NaturalIdDataAccess generateNonStrictReadWriteNaturalIdAccess(NaturalIdDataCachingConfig accessConfig) {
        return new NaturalIdNonStrictReadWriteAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    protected NaturalIdDataAccess generateTransactionalNaturalIdDataAccess(NaturalIdDataCachingConfig accessConfig) {
        throw this.generateTransactionalNotSupportedException();
    }

    @Override
    public CollectionDataAccess generateCollectionAccess(CollectionDataCachingConfig accessConfig) {
        NavigableRole namedCollectionRole = accessConfig.getNavigableRole();
        log.debugf("Generating collection cache access : %s", (Object)namedCollectionRole);
        switch (accessConfig.getAccessType()) {
            case READ_ONLY: {
                return this.generateReadOnlyCollectionAccess(accessConfig);
            }
            case READ_WRITE: {
                return this.generateReadWriteCollectionAccess(accessConfig);
            }
            case NONSTRICT_READ_WRITE: {
                return this.generateNonStrictReadWriteCollectionAccess(accessConfig);
            }
            case TRANSACTIONAL: {
                return this.generateTransactionalCollectionDataAccess(accessConfig);
            }
        }
        throw new IllegalArgumentException("Unrecognized cache AccessType - " + (Object)((Object)accessConfig.getAccessType()));
    }

    private CollectionDataAccess generateReadOnlyCollectionAccess(CollectionDataCachingConfig accessConfig) {
        return new CollectionReadOnlyAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    private CollectionDataAccess generateReadWriteCollectionAccess(CollectionDataCachingConfig accessConfig) {
        return new CollectionReadWriteAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    private CollectionDataAccess generateNonStrictReadWriteCollectionAccess(CollectionDataCachingConfig accessConfig) {
        return new CollectionNonStrictReadWriteAccess(this, this.getEffectiveKeysFactory(), this.getCacheStorageAccess(), accessConfig);
    }

    protected CollectionDataAccess generateTransactionalCollectionDataAccess(CollectionDataCachingConfig accessConfig) {
        throw this.generateTransactionalNotSupportedException();
    }
}

