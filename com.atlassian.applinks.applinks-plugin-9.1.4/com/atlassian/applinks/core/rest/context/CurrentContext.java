/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.api.core.HttpContext
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.applinks.core.rest.context;

import com.sun.jersey.api.core.HttpContext;
import javax.servlet.http.HttpServletRequest;

public class CurrentContext {
    private static final ThreadLocal<HttpContext> context = new ThreadLocal();
    private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal();

    public static HttpContext getContext() {
        return context.get();
    }

    public static void setContext(HttpContext httpContext) {
        context.set(httpContext);
    }

    public static HttpServletRequest getHttpServletRequest() {
        return request.get();
    }

    public static void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        request.set(httpServletRequest);
    }
}

