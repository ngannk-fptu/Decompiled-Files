/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import org.hibernate.CacheMode;

public enum CacheModeType {
    GET(CacheMode.GET),
    IGNORE(CacheMode.IGNORE),
    NORMAL(CacheMode.NORMAL),
    PUT(CacheMode.PUT),
    REFRESH(CacheMode.REFRESH);

    private final CacheMode cacheMode;

    private CacheModeType(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public CacheMode getCacheMode() {
        return this.cacheMode;
    }

    public static CacheModeType fromCacheMode(CacheMode cacheMode) {
        if (null == cacheMode) {
            return null;
        }
        switch (cacheMode) {
            case NORMAL: {
                return NORMAL;
            }
            case GET: {
                return GET;
            }
            case PUT: {
                return PUT;
            }
            case REFRESH: {
                return REFRESH;
            }
            case IGNORE: {
                return IGNORE;
            }
        }
        throw new IllegalArgumentException("Unrecognized CacheMode : " + (Object)((Object)cacheMode));
    }
}

