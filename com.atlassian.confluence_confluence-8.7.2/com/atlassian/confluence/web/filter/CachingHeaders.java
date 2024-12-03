/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.web.filter;

import javax.servlet.http.HttpServletResponse;

public interface CachingHeaders {
    public static final long LONG_TERM_EXPIRY_SECONDS = 315360000L;
    public static final long LONG_TERM_EXPIRY_MILLIS = 315360000000L;
    public static final long SHORT_TERM_EXPIRY_SECONDS = 600L;
    public static final long SHORT_TERM_EXPIRY_MILLIS = 600000L;
    public static final CachingHeaders PREVENT_CACHING = response -> {
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0L);
    };
    public static final CachingHeaders PREVENT_CACHING_IE_SSL = response -> {
        response.setHeader("Cache-Control", "private, max-age=0, must-revalidate");
        response.setDateHeader("Expires", 0L);
    };
    public static final CachingHeaders PRIVATE_SHORT_TERM = response -> {
        response.setHeader("Cache-Control", "private, must-revalidate, max-age=600");
        response.setDateHeader("Expires", 0L);
    };
    public static final CachingHeaders PRIVATE_LONG_TERM = response -> {
        response.setHeader("Cache-Control", "private, max-age=315360000");
        response.setDateHeader("Expires", 0L);
    };
    public static final CachingHeaders PUBLIC_SHORT_TERM = response -> {
        response.setHeader("Cache-Control", "public, must-revalidate, max-age=600");
        response.setDateHeader("Expires", System.currentTimeMillis() + 600000L);
    };
    public static final CachingHeaders PUBLIC_LONG_TERM = response -> {
        response.setHeader("Cache-Control", "public, max-age=315360000");
        response.setDateHeader("Expires", System.currentTimeMillis() + 315360000000L);
    };

    public void apply(HttpServletResponse var1);
}

