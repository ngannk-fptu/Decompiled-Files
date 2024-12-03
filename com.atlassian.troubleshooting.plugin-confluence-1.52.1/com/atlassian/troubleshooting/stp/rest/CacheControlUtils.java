/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.CacheControl
 */
package com.atlassian.troubleshooting.stp.rest;

import javax.ws.rs.core.CacheControl;

public class CacheControlUtils {
    public static final CacheControl NO_CACHE = new CacheControl();

    static {
        NO_CACHE.setNoCache(true);
        NO_CACHE.setNoStore(true);
    }
}

