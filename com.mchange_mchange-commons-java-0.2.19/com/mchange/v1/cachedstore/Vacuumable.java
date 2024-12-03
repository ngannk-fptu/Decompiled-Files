/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStoreException;

public interface Vacuumable {
    public void vacuum() throws CachedStoreException;
}

