/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.CacheControl
 */
package com.atlassian.crowd.plugin.rest.service.util;

public class CacheControl {
    private static final int ONE_YEAR = 31536000;
    public static final javax.ws.rs.core.CacheControl NO_CACHE = new javax.ws.rs.core.CacheControl();
    public static final javax.ws.rs.core.CacheControl CACHE_FOREVER;

    static {
        NO_CACHE.setNoStore(true);
        NO_CACHE.setNoCache(true);
        CACHE_FOREVER = new javax.ws.rs.core.CacheControl();
        CACHE_FOREVER.setPrivate(false);
        CACHE_FOREVER.setMaxAge(31536000);
    }
}

