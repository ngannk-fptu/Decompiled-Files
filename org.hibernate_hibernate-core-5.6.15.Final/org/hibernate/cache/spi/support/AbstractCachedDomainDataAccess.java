/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.access.CachedDomainDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractDomainDataRegion;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.jboss.logging.Logger;

public abstract class AbstractCachedDomainDataAccess
implements CachedDomainDataAccess,
AbstractDomainDataRegion.Destructible {
    private static final Logger log = Logger.getLogger(AbstractCachedDomainDataAccess.class);
    private final DomainDataRegion region;
    private final DomainDataStorageAccess storageAccess;
    private static final SoftLock REGION_LOCK = new SoftLock(){};

    protected AbstractCachedDomainDataAccess(DomainDataRegion region, DomainDataStorageAccess storageAccess) {
        this.region = region;
        this.storageAccess = storageAccess;
    }

    @Override
    public DomainDataRegion getRegion() {
        return this.region;
    }

    protected DomainDataStorageAccess getStorageAccess() {
        return this.storageAccess;
    }

    protected void clearCache() {
        log.debugf("Clearing cache data map [region=`%s`]", (Object)this.region.getName());
        this.getStorageAccess().evictData();
    }

    @Override
    public boolean contains(Object key) {
        return this.getStorageAccess().contains(key);
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key) {
        return this.getStorageAccess().getFromCache(key, session);
    }

    @Override
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        this.getStorageAccess().putFromLoad(key, value, session);
        return true;
    }

    @Override
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, Object version, boolean minimalPutOverride) {
        return this.putFromLoad(session, key, value, version);
    }

    @Override
    public SoftLock lockRegion() {
        return REGION_LOCK;
    }

    @Override
    public void unlockRegion(SoftLock lock) {
        this.evictAll();
    }

    @Override
    public void remove(SharedSessionContractImplementor session, Object key) {
        this.getStorageAccess().removeFromCache(key, session);
    }

    @Override
    public void removeAll(SharedSessionContractImplementor session) {
        this.getStorageAccess().clearCache(session);
    }

    @Override
    public void evict(Object key) {
        this.getStorageAccess().evictData(key);
    }

    @Override
    public void evictAll() {
        this.getStorageAccess().evictData();
    }

    @Override
    public void destroy() {
        this.getStorageAccess().release();
    }
}

