/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CacheFlushException;
import com.mchange.v1.cachedstore.CachedStoreError;
import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.TweakableCachedStore;
import com.mchange.v1.cachedstore.WritableCachedStore;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class SimpleWritableCachedStore
implements WritableCachedStore {
    private static final Object REMOVE_TOKEN = new Object();
    TweakableCachedStore readOnlyCache;
    WritableCachedStore.Manager manager;
    HashMap writeCache = new HashMap();
    Set failedWrites = null;

    SimpleWritableCachedStore(TweakableCachedStore tweakableCachedStore, WritableCachedStore.Manager manager) {
        this.readOnlyCache = tweakableCachedStore;
        this.manager = manager;
    }

    @Override
    public Object find(Object object) throws CachedStoreException {
        Object object2 = this.writeCache.get(object);
        if (object2 == null) {
            object2 = this.readOnlyCache.find(object);
        }
        return object2 == REMOVE_TOKEN ? null : object2;
    }

    @Override
    public void write(Object object, Object object2) {
        this.writeCache.put(object, object2);
    }

    @Override
    public void remove(Object object) {
        this.write(object, REMOVE_TOKEN);
    }

    @Override
    public void flushWrites() throws CacheFlushException {
        HashMap hashMap = (HashMap)this.writeCache.clone();
        for (Object k : hashMap.keySet()) {
            Object v = hashMap.get(k);
            try {
                if (v == REMOVE_TOKEN) {
                    this.manager.removeFromStorage(k);
                } else {
                    this.manager.writeToStorage(k, v);
                }
                try {
                    this.readOnlyCache.setCachedValue(k, v);
                    this.writeCache.remove(k);
                    if (this.failedWrites == null) continue;
                    this.failedWrites.remove(k);
                    if (this.failedWrites.size() != 0) continue;
                    this.failedWrites = null;
                }
                catch (CachedStoreException cachedStoreException) {
                    throw new CachedStoreError("SimpleWritableCachedStore: Internal cache is broken!");
                }
            }
            catch (Exception exception) {
                if (this.failedWrites == null) {
                    this.failedWrites = new HashSet();
                }
                this.failedWrites.add(k);
            }
        }
        if (this.failedWrites != null) {
            throw new CacheFlushException("Some keys failed to write!");
        }
    }

    @Override
    public Set getFailedWrites() {
        return this.failedWrites == null ? null : Collections.unmodifiableSet(this.failedWrites);
    }

    @Override
    public void clearPendingWrites() {
        this.writeCache.clear();
        this.failedWrites = null;
    }

    @Override
    public void reset() throws CachedStoreException {
        this.writeCache.clear();
        this.readOnlyCache.reset();
        this.failedWrites = null;
    }

    @Override
    public void sync() throws CachedStoreException {
        this.flushWrites();
        this.reset();
    }
}

