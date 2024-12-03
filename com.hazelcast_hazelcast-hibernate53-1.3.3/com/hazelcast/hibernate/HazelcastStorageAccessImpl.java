/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.OperationTimeoutException
 *  com.hazelcast.logging.Logger
 *  org.hibernate.cache.CacheException
 *  org.hibernate.cache.spi.access.SoftLock
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.hazelcast.hibernate;

import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.hibernate.HazelcastStorageAccess;
import com.hazelcast.hibernate.RegionCache;
import com.hazelcast.logging.Logger;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class HazelcastStorageAccessImpl
implements HazelcastStorageAccess {
    private final RegionCache delegate;

    HazelcastStorageAccessImpl(RegionCache delegate) {
        this.delegate = delegate;
    }

    @Override
    public void afterUpdate(Object key, Object newValue, Object newVersion) {
        this.delegate.afterUpdate(key, newValue, newVersion);
    }

    public boolean contains(Object key) {
        return this.delegate.contains(key);
    }

    public void evictData() throws CacheException {
        try {
            this.delegate.evictData();
        }
        catch (OperationTimeoutException e) {
            Logger.getLogger(HazelcastStorageAccessImpl.class).finest((Throwable)e);
        }
    }

    public void evictData(Object key) throws CacheException {
        try {
            this.delegate.evictData(key);
        }
        catch (OperationTimeoutException e) {
            Logger.getLogger(HazelcastStorageAccessImpl.class).finest((Throwable)e);
        }
    }

    public Object getFromCache(Object key, SharedSessionContractImplementor session) throws CacheException {
        try {
            return this.delegate.get(key, this.nextTimestamp());
        }
        catch (OperationTimeoutException e) {
            return null;
        }
    }

    public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) throws CacheException {
        try {
            this.delegate.put(key, value, this.nextTimestamp(), null);
        }
        catch (OperationTimeoutException e) {
            Logger.getLogger(HazelcastStorageAccessImpl.class).finest((Throwable)e);
        }
    }

    public void release() {
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) {
        this.delegate.unlockItem(key, lock);
    }

    RegionCache getDelegate() {
        return this.delegate;
    }

    private long nextTimestamp() {
        return this.delegate.nextTimestamp();
    }
}

