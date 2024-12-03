/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.ValueTransformingCachedStore;
import com.mchange.v1.util.WrapperIterator;
import java.util.Iterator;

abstract class KeyValueTransformingCachedStore
extends ValueTransformingCachedStore {
    protected KeyValueTransformingCachedStore(CachedStore.Manager manager) {
        super(manager);
    }

    @Override
    public Object getCachedValue(Object object) {
        return this.toUserValue(this.cache.get(this.toCacheFetchKey(object)));
    }

    public void clearCachedValue(Object object) throws CachedStoreException {
        this.cache.remove(this.toCacheFetchKey(object));
    }

    @Override
    public void setCachedValue(Object object, Object object2) throws CachedStoreException {
        this.cache.put(this.toCachePutKey(object), this.toCacheValue(object2));
    }

    @Override
    public Iterator cachedKeys() throws CachedStoreException {
        return new WrapperIterator(this.cache.keySet().iterator(), false){

            @Override
            public Object transformObject(Object object) {
                Object object2 = KeyValueTransformingCachedStore.this.toUserKey(object);
                return object2 == null ? SKIP_TOKEN : object2;
            }
        };
    }

    protected Object toUserKey(Object object) {
        return object;
    }

    protected Object toCacheFetchKey(Object object) {
        return object;
    }

    protected Object toCachePutKey(Object object) {
        return object;
    }
}

