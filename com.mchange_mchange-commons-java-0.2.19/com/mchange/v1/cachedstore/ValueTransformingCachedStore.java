/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.NoCleanupCachedStore;

abstract class ValueTransformingCachedStore
extends NoCleanupCachedStore {
    protected ValueTransformingCachedStore(CachedStore.Manager manager) {
        super(manager);
    }

    @Override
    public Object getCachedValue(Object object) {
        return this.toUserValue(this.cache.get(object));
    }

    @Override
    public void removeFromCache(Object object) throws CachedStoreException {
        this.cache.remove(object);
    }

    @Override
    public void setCachedValue(Object object, Object object2) throws CachedStoreException {
        this.cache.put(object, this.toCacheValue(object2));
    }

    protected Object toUserValue(Object object) {
        return object;
    }

    protected Object toCacheValue(Object object) {
        return object;
    }
}

