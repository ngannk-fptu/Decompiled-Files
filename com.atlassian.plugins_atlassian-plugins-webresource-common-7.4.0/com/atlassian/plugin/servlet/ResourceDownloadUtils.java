/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugin.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceDownloadUtils {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final long ONE_YEAR_SECONDS = 31536000L;
    private static final long ONE_YEAR_MILLISECONDS = 31536000000L;

    public static void addCachingHeaders(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String ... cacheControls) {
        boolean cacheDisabledByQueryParam = "false".equals(httpServletRequest.getParameter("cache"));
        if (Boolean.getBoolean("atlassian.disable.caches")) {
            httpServletResponse.setDateHeader("Expires", 0L);
            httpServletResponse.setHeader(CACHE_CONTROL, "no-cache, must-revalidate");
        } else if (!cacheDisabledByQueryParam) {
            httpServletResponse.setDateHeader("Expires", System.currentTimeMillis() + 31536000000L);
            httpServletResponse.setHeader(CACHE_CONTROL, "max-age=31536000");
            for (String cacheControl : cacheControls) {
                httpServletResponse.addHeader(CACHE_CONTROL, cacheControl);
            }
        }
    }

    public static void addPublicCachingHeaders(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ResourceDownloadUtils.addCachingHeaders(httpServletRequest, httpServletResponse, "public");
    }

    public static void addPrivateCachingHeaders(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ResourceDownloadUtils.addCachingHeaders(httpServletRequest, httpServletResponse, "private");
    }
}

