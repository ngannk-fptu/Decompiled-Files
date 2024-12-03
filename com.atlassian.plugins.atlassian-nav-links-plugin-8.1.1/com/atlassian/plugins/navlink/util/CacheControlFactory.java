/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.CacheControl
 */
package com.atlassian.plugins.navlink.util;

import javax.ws.rs.core.CacheControl;

public class CacheControlFactory {
    private static final int TEN_MINUTES = 600;
    public static final int MAX_AGE_IN_SECONDS = Integer.getInteger("navlink.cachecontrol.maxage", 600);
    private static final int TWENTY_FOUR_HOURS = 86400;
    public static final int STALE_WHILE_REVALIDATE_IN_SECONDS = Integer.getInteger("navlink.cachecontrol.stalewhilerevalidate", 86400);
    public static final int STALE_IF_ERROR_IN_SECONDS = Integer.getInteger("navlink.cachecontrol.stateiferror", 86400);

    public static CacheControl withConfiguredMaxAgeAndStaleContentExtension() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_IN_SECONDS);
        cacheControl.getCacheExtension().put("stale-while-revalidate", Integer.toString(STALE_WHILE_REVALIDATE_IN_SECONDS));
        cacheControl.getCacheExtension().put("stale-if-error", Integer.toString(STALE_IF_ERROR_IN_SECONDS));
        return cacheControl;
    }

    public static CacheControl withNoCache() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        return cacheControl;
    }
}

