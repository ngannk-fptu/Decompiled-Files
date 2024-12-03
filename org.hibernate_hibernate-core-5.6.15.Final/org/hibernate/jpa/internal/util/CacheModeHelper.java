/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.CacheRetrieveMode
 *  javax.persistence.CacheStoreMode
 */
package org.hibernate.jpa.internal.util;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import org.hibernate.CacheMode;

public final class CacheModeHelper {
    public static final CacheMode DEFAULT_LEGACY_MODE = CacheMode.NORMAL;
    public static final CacheStoreMode DEFAULT_STORE_MODE = CacheStoreMode.USE;
    public static final CacheRetrieveMode DEFAULT_RETRIEVE_MODE = CacheRetrieveMode.USE;

    private CacheModeHelper() {
    }

    public static CacheMode interpretCacheMode(CacheStoreMode storeMode, CacheRetrieveMode retrieveMode) {
        if (storeMode == null) {
            storeMode = DEFAULT_STORE_MODE;
        }
        if (retrieveMode == null) {
            retrieveMode = DEFAULT_RETRIEVE_MODE;
        }
        boolean get = CacheRetrieveMode.USE == retrieveMode;
        switch (storeMode) {
            case USE: {
                return get ? CacheMode.NORMAL : CacheMode.PUT;
            }
            case REFRESH: {
                return CacheMode.REFRESH;
            }
            case BYPASS: {
                return get ? CacheMode.GET : CacheMode.IGNORE;
            }
        }
        throw new IllegalStateException("huh? :)");
    }

    public static CacheMode effectiveCacheMode(CacheStoreMode storeMode, CacheRetrieveMode retrieveMode) {
        if (storeMode == null && retrieveMode == null) {
            return null;
        }
        if (storeMode == null) {
            storeMode = DEFAULT_STORE_MODE;
        }
        if (retrieveMode == null) {
            retrieveMode = DEFAULT_RETRIEVE_MODE;
        }
        boolean get = CacheRetrieveMode.USE == retrieveMode;
        switch (storeMode) {
            case USE: {
                return get ? CacheMode.NORMAL : CacheMode.PUT;
            }
            case REFRESH: {
                return CacheMode.REFRESH;
            }
            case BYPASS: {
                return get ? CacheMode.GET : CacheMode.IGNORE;
            }
        }
        throw new IllegalStateException("huh? :)");
    }

    public static CacheStoreMode interpretCacheStoreMode(CacheMode cacheMode) {
        if (cacheMode == null) {
            cacheMode = DEFAULT_LEGACY_MODE;
        }
        if (CacheMode.REFRESH == cacheMode) {
            return CacheStoreMode.REFRESH;
        }
        if (CacheMode.NORMAL == cacheMode || CacheMode.PUT == cacheMode) {
            return CacheStoreMode.USE;
        }
        return CacheStoreMode.BYPASS;
    }

    public static CacheRetrieveMode interpretCacheRetrieveMode(CacheMode cacheMode) {
        if (cacheMode == null) {
            cacheMode = DEFAULT_LEGACY_MODE;
        }
        return CacheMode.NORMAL == cacheMode || CacheMode.GET == cacheMode ? CacheRetrieveMode.USE : CacheRetrieveMode.BYPASS;
    }
}

