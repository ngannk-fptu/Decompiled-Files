/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractEntityDataAccess;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class EntityTransactionalAccess
extends AbstractEntityDataAccess {
    public EntityTransactionalAccess(DomainDataRegion region, CacheKeysFactory keysFactory, DomainDataStorageAccess storageAccess, EntityDataCachingConfig accessConfig) {
        super(region, keysFactory, storageAccess);
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        this.getStorageAccess().putIntoCache(key, value, session);
        return true;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        return false;
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion) {
        this.getStorageAccess().putIntoCache(key, value, session);
        return true;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
        return false;
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.TRANSACTIONAL;
    }
}

