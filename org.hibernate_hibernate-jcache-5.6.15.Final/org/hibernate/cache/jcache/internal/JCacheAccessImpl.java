/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  org.hibernate.cache.spi.support.DomainDataStorageAccess
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package org.hibernate.cache.jcache.internal;

import javax.cache.Cache;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class JCacheAccessImpl
implements DomainDataStorageAccess {
    private final Cache underlyingCache;

    public JCacheAccessImpl(Cache underlyingCache) {
        this.underlyingCache = underlyingCache;
    }

    public Cache getUnderlyingCache() {
        return this.underlyingCache;
    }

    public boolean contains(Object key) {
        return this.underlyingCache.containsKey(key);
    }

    public Object getFromCache(Object key, SharedSessionContractImplementor session) {
        return this.underlyingCache.get(key);
    }

    public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
        this.underlyingCache.put(key, value);
    }

    public void removeFromCache(Object key, SharedSessionContractImplementor session) {
        this.underlyingCache.remove(key);
    }

    public void evictData(Object key) {
        this.underlyingCache.remove(key);
    }

    public void clearCache(SharedSessionContractImplementor session) {
        this.underlyingCache.clear();
    }

    public void evictData() {
        this.underlyingCache.clear();
    }

    public void release() {
        this.underlyingCache.close();
    }
}

