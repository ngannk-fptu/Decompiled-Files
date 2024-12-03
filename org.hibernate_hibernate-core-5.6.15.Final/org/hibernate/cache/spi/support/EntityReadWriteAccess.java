/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import java.util.Comparator;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.hibernate.cache.spi.support.AccessedDataClassification;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class EntityReadWriteAccess
extends AbstractReadWriteAccess
implements EntityDataAccess {
    private final CacheKeysFactory keysFactory;
    private final Comparator versionComparator;

    public EntityReadWriteAccess(DomainDataRegion domainDataRegion, CacheKeysFactory keysFactory, DomainDataStorageAccess storageAccess, EntityDataCachingConfig entityAccessConfig) {
        super(domainDataRegion, storageAccess);
        this.keysFactory = keysFactory;
        this.versionComparator = entityAccessConfig.getVersionComparatorAccess() == null ? null : entityAccessConfig.getVersionComparatorAccess().get();
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.READ_WRITE;
    }

    @Override
    protected AccessedDataClassification getAccessedDataClassification() {
        return AccessedDataClassification.ENTITY;
    }

    @Override
    protected Comparator getVersionComparator() {
        return this.versionComparator;
    }

    @Override
    public Object generateCacheKey(Object id, EntityPersister rootEntityDescriptor, SessionFactoryImplementor factory, String tenantIdentifier) {
        return this.keysFactory.createEntityKey(id, rootEntityDescriptor, factory, tenantIdentifier);
    }

    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return this.keysFactory.getEntityId(cacheKey);
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        try {
            this.writeLock().lock();
            AbstractReadWriteAccess.Lockable item = (AbstractReadWriteAccess.Lockable)this.getStorageAccess().getFromCache(key, session);
            if (item == null) {
                this.getStorageAccess().putIntoCache(key, new AbstractReadWriteAccess.Item(value, version, this.getRegion().getRegionFactory().nextTimestamp()), session);
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.writeLock().unlock();
        }
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion) {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
        try {
            this.writeLock().lock();
            AbstractReadWriteAccess.Lockable item = (AbstractReadWriteAccess.Lockable)this.getStorageAccess().getFromCache(key, session);
            if (item != null && item.isUnlockable(lock)) {
                AbstractReadWriteAccess.SoftLockImpl lockItem = (AbstractReadWriteAccess.SoftLockImpl)item;
                if (lockItem.wasLockedConcurrently()) {
                    this.decrementLock(session, key, lockItem);
                    boolean bl = false;
                    return bl;
                }
                this.getStorageAccess().putIntoCache(key, new AbstractReadWriteAccess.Item(value, currentVersion, this.getRegion().getRegionFactory().nextTimestamp()), session);
                boolean bl = true;
                return bl;
            }
            this.handleLockExpiry(session, key, item);
            boolean bl = false;
            return bl;
        }
        finally {
            this.writeLock().unlock();
        }
    }

    @Override
    public SoftLock lockRegion() {
        return null;
    }
}

