/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import java.util.Comparator;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.hibernate.cache.spi.support.AccessedDataClassification;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class NaturalIdReadWriteAccess
extends AbstractReadWriteAccess
implements NaturalIdDataAccess {
    private final CacheKeysFactory keysFactory;

    public NaturalIdReadWriteAccess(DomainDataRegion region, CacheKeysFactory keysFactory, DomainDataStorageAccess storageAccess, NaturalIdDataCachingConfig naturalIdDataCachingConfig) {
        super(region, storageAccess);
        this.keysFactory = keysFactory;
    }

    @Override
    protected AccessedDataClassification getAccessedDataClassification() {
        return AccessedDataClassification.NATURAL_ID;
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.READ_WRITE;
    }

    @Override
    protected Comparator getVersionComparator() {
        return null;
    }

    @Override
    public Object generateCacheKey(Object[] naturalIdValues, EntityPersister rootEntityDescriptor, SharedSessionContractImplementor session) {
        return this.keysFactory.createNaturalIdKey(naturalIdValues, rootEntityDescriptor, session);
    }

    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return this.keysFactory.getNaturalIdValues(cacheKey);
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value) {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) {
        try {
            this.writeLock().lock();
            AbstractReadWriteAccess.Lockable item = (AbstractReadWriteAccess.Lockable)this.getStorageAccess().getFromCache(key, session);
            if (item == null) {
                this.getStorageAccess().putIntoCache(key, new AbstractReadWriteAccess.Item(value, null, this.getRegion().getRegionFactory().nextTimestamp()), session);
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
    public boolean update(SharedSessionContractImplementor session, Object key, Object value) {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) {
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
                this.getStorageAccess().putIntoCache(key, new AbstractReadWriteAccess.Item(value, null, this.getRegion().getRegionFactory().nextTimestamp()), session);
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
}

