/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.core.filters;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletContextThreadLocal {
    private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal();
    private static final ThreadLocal<HttpServletResponse> response = new ThreadLocal();

    public static ServletContext getContext() {
        return ServletContextThreadLocal.getRequest().getSession().getServletContext();
    }

    public static HttpServletRequest getRequest() {
        return request.get();
    }

    public static void setRequest(HttpServletRequest httpRequest) {
        request.set(httpRequest);
    }

    static void setResponse(HttpServletResponse httpResponse) {
        response.set(httpResponse);
    }

    public static HttpServletResponse getResponse() {
        return response.get();
    }
}

