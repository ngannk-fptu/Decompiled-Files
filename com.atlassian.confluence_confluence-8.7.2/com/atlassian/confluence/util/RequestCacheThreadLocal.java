/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import java.util.HashMap;
import java.util.Map;

public class RequestCacheThreadLocal {
    public static final String REMOTE_ADDRESS_KEY = "request.remote.address";
    public static final String X_FORWARDED_FOR_KEY = "x.forwarded.for";
    public static final String CONTEXT_PATH_KEY = "confluence.context.path";
    public static final String REQUEST_ID_KEY = "request.correlation.id";
    public static final String HEADER_MOBILE_APP_REQUEST_KEY = "header.mobile.app.request";
    private static ThreadLocal<Map> threadLocal = new ThreadLocal();

    public static Map getRequestCache() {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap());
        }
        return threadLocal.get();
    }

    public static void setRequestCache(Map requestCache) {
        threadLocal.set(requestCache);
    }

    public static void clearRequestCache() {
        RequestCacheThreadLocal.setRequestCache(null);
    }

    public static String getRemoteAddress() {
        return RequestCacheThreadLocal.getRequestCache() != null ? RequestCacheThreadLocal.getRequestCache().get(REMOTE_ADDRESS_KEY) : null;
    }

    public static String getXForwardedFor() {
        return RequestCacheThreadLocal.getRequestCache() != null ? RequestCacheThreadLocal.getRequestCache().get(X_FORWARDED_FOR_KEY) : null;
    }

    public static String getContextPath() {
        return RequestCacheThreadLocal.getRequestCache() != null ? RequestCacheThreadLocal.getRequestCache().get(CONTEXT_PATH_KEY) : null;
    }

    public static String getRequestCorrelationId() {
        return RequestCacheThreadLocal.getRequestCache() != null ? RequestCacheThreadLocal.getRequestCache().get(REQUEST_ID_KEY) : "";
    }

    public static String getMobileAppRequestHeader() {
        return RequestCacheThreadLocal.getRequestCache() != null ? RequestCacheThreadLocal.getRequestCache().get(HEADER_MOBILE_APP_REQUEST_KEY) : null;
    }
}

