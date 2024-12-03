/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.CachedStoreUtils;
import com.mchange.v1.cachedstore.TweakableCachedStore;
import com.mchange.v1.util.IteratorUtils;
import java.util.Iterator;

class NoCacheCachedStore
implements TweakableCachedStore {
    CachedStore.Manager mgr;

    NoCacheCachedStore(CachedStore.Manager manager) {
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
    public Object getCachedValue(Object object) {
        return null;
    }

    @Override
    public void removeFromCache(Object object) {
    }

    @Override
    public void setCachedValue(Object object, Object object2) {
    }

    @Override
    public Iterator cachedKeys() {
        return IteratorUtils.EMPTY_ITERATOR;
    }
}

