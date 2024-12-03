/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStore;
import com.mchange.v1.cachedstore.CachedStoreException;
import java.util.Iterator;

public interface TweakableCachedStore
extends CachedStore {
    public Object getCachedValue(Object var1) throws CachedStoreException;

    public void removeFromCache(Object var1) throws CachedStoreException;

    public void setCachedValue(Object var1, Object var2) throws CachedStoreException;

    public Iterator cachedKeys() throws CachedStoreException;
}

