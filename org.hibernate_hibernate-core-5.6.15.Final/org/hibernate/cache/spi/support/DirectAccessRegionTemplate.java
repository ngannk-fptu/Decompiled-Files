/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.spi.DirectAccessRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.support.AbstractRegion;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public abstract class DirectAccessRegionTemplate
extends AbstractRegion
implements DirectAccessRegion {
    private final StorageAccess storageAccess;

    public DirectAccessRegionTemplate(String name, RegionFactory regionFactory, StorageAccess storageAccess) {
        super(name, regionFactory);
        this.storageAccess = storageAccess;
    }

    public StorageAccess getStorageAccess() {
        return this.storageAccess;
    }

    @Override
    public Object getFromCache(Object key, SharedSessionContractImplementor session) {
        return this.getStorageAccess().getFromCache(key, session);
    }

    @Override
    public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
        this.getStorageAccess().putIntoCache(key, value, session);
    }

    @Override
    public void clear() {
        this.getStorageAccess().evictData();
    }

    @Override
    public void destroy() {
        this.getStorageAccess().release();
    }
}

