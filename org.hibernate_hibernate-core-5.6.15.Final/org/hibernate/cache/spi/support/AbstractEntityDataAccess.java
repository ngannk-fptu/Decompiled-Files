/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractCachedDomainDataAccess;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public abstract class AbstractEntityDataAccess
extends AbstractCachedDomainDataAccess
implements EntityDataAccess {
    private final CacheKeysFactory cacheKeysFactory;

    public AbstractEntityDataAccess(DomainDataRegion region, CacheKeysFactory cacheKeysFactory, DomainDataStorageAccess storageAccess) {
        super(region, storageAccess);
        this.cacheKeysFactory = cacheKeysFactory;
    }

    @Override
    public Object generateCacheKey(Object id, EntityPersister rootEntityDescriptor, SessionFactoryImplementor factory, String tenantIdentifier) {
        return this.cacheKeysFactory.createEntityKey(id, rootEntityDescriptor, factory, tenantIdentifier);
    }

    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return this.cacheKeysFactory.getEntityId(cacheKey);
    }

    @Override
    public SoftLock lockRegion() {
        return null;
    }

    @Override
    public void unlockRegion(SoftLock lock) {
        this.clearCache();
    }

    @Override
    public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) {
        return null;
    }

    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
    }
}

