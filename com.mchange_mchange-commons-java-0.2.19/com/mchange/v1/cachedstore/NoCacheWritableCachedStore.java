/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.Autoflushing;
import com.mchange.v1.cachedstore.CacheFlushException;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.CachedStoreUtils;
import com.mchange.v1.cachedstore.WritableCachedStore;
import java.util.Collections;
import java.util.Set;

class NoCacheWritableCachedStore
implements WritableCachedStore,
Autoflushing {
    WritableCachedStore.Manager mgr;

    NoCacheWritableCachedStore(WritableCachedStore.Manager manager) {
        this.mgr = manager;
    }

    @Override
    public Object find(Object object) throws CachedStoreException {
        try {
            return this.mgr.recreateFromKey(object);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw CachedStoreUtils.toCachedStoreException(exception);
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public void write(Object object, Object object2) throws CachedStoreException {
        try {
            this.mgr.writeToStorage(object, object2);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw CachedStoreUtils.toCachedStoreException(exception);
        }
    }

    @Override
    public void remove(Object object) throws CachedStoreException {
        try {
            this.mgr.removeFromStorage(object);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw CachedStoreUtils.toCachedStoreException(exception);
        }
    }

    @Override
    public void flushWrites() throws CacheFlushException {
    }

    @Override
    public Set getFailedWrites() throws CachedStoreException {
        return Collections.EMPTY_SET;
    }

    @Override
    public void clearPendingWrites() throws CachedStoreException {
    }

    @Override
    public void sync() throws CachedStoreException {
    }
}

