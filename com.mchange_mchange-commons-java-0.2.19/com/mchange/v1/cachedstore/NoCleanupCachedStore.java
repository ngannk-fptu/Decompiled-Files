/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.TweakableCachedStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class NoCleanupCachedStore
implements TweakableCachedStore {
    static final boolean DEBUG = true;
    protected Map cache = new HashMap();
    CachedStore.Manager manager;

    public NoCleanupCachedStore(CachedStore.Manager manager) {
        this.manager = manager;
    }

    @Override
    public Object find(Object object) throws CachedStoreException {
        try {
            Object object2 = this.getCachedValue(object);
            if ((object2 == null || this.manager.isDirty(object, object2)) && (object2 = this.manager.recreateFromKey(object)) != null) {
                this.setCachedValue(object, object2);
            }
            return object2;
        }
        catch (CachedStoreException cachedStoreException) {
            throw cachedStoreException;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new CachedStoreException(exception);
        }
    }

    @Override
    public Object getCachedValue(Object object) {
        return this.cache.get(object);
    }

    @Override
    public void removeFromCache(Object object) throws CachedStoreException {
        this.cache.remove(object);
    }

    @Override
    public void setCachedValue(Object object, Object object2) throws CachedStoreException {
        this.cache.put(object, object2);
    }

    @Override
    public Iterator cachedKeys() throws CachedStoreException {
        return this.cache.keySet().iterator();
    }

    @Override
    public void reset() {
        this.cache.clear();
    }
}

