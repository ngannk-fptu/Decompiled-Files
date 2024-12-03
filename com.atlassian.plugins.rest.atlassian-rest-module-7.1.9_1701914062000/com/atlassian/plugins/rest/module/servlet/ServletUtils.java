/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.rest.module.servlet;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
    private static final ThreadLocal<HttpServletRequest> HTTP_SERVLET_REQUEST_THREAD_LOCAL = new ThreadLocal();

    public static HttpServletRequest getHttpServletRequest() {
        return HTTP_SERVLET_REQUEST_THREAD_LOCAL.get();
    }

    public static void setHttpServletRequest(HttpServletRequest request) {
        HTTP_SERVLET_REQUEST_THREAD_LOCAL.set(request);
    }
}

