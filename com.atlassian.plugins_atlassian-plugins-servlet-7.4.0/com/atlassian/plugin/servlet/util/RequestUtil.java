/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugin.servlet.util;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static String getServletPath(HttpServletRequest request) {
        String servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        return servletPath == null ? "" : servletPath;
    }

    public static String getPathInfo(HttpServletRequest request) {
        String pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
        if (pathInfo == null) {
            pathInfo = request.getPathInfo();
        }
        return pathInfo == null ? "" : pathInfo;
    }

    public static String getRequestURI(HttpServletRequest request) {
        String requestURI = (String)request.getAttribute("javax.servlet.include.request_uri");
        if (requestURI == null) {
            requestURI = request.getRequestURI();
        }
        return requestURI == null ? "" : requestURI;
    }
}

