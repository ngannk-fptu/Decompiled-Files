/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractEntityDataAccess;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class EntityNonStrictReadWriteAccess
extends AbstractEntityDataAccess {
    public EntityNonStrictReadWriteAccess(DomainDataRegion domainDataRegion, CacheKeysFactory keysFactory, DomainDataStorageAccess storageAccess, EntityDataCachingConfig entityAccessConfig) {
        super(domainDataRegion, keysFactory, storageAccess);
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.NONSTRICT_READ_WRITE;
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        return false;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        return false;
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion) {
        this.getStorageAccess().removeFromCache(key, session);
        return false;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
        this.unlockItem(session, key, lock);
        return false;
    }

    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
        this.getStorageAccess().removeFromCache(key, session);
    }

    @Override
    public void remove(SharedSessionContractImplementor session, Object key) {
        this.getStorageAccess().removeFromCache(key, session);
    }
}

