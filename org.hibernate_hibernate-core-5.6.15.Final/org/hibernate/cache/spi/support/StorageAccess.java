/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface StorageAccess {
    public Object getFromCache(Object var1, SharedSessionContractImplementor var2);

    public void putIntoCache(Object var1, Object var2, SharedSessionContractImplementor var3);

    default public void removeFromCache(Object key, SharedSessionContractImplementor session) {
        this.evictData(key);
    }

    default public void clearCache(SharedSessionContractImplementor session) {
        this.evictData();
    }

    public boolean contains(Object var1);

    public void evictData();

    public void evictData(Object var1);

    public void release();
}

