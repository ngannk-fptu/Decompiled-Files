/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CacheFlushException;
import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import java.util.Set;

public interface WritableCachedStore
extends CachedStore {
    public void write(Object var1, Object var2) throws CachedStoreException;

    public void remove(Object var1) throws CachedStoreException;

    public void flushWrites() throws CacheFlushException;

    public Set getFailedWrites() throws CachedStoreException;

    public void clearPendingWrites() throws CachedStoreException;

    @Override
    public void reset() throws CachedStoreException;

    public void sync() throws CachedStoreException;

    public static interface Manager
    extends CachedStore.Manager {
        public void writeToStorage(Object var1, Object var2) throws Exception;

        public void removeFromStorage(Object var1) throws Exception;
    }
}

