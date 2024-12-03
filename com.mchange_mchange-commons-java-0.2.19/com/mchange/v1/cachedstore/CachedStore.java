/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStoreException;

public interface CachedStore {
    public Object find(Object var1) throws CachedStoreException;

    public void reset() throws CachedStoreException;

    public static interface Manager {
        public boolean isDirty(Object var1, Object var2) throws Exception;

        public Object recreateFromKey(Object var1) throws Exception;
    }
}

