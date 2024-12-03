/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.crowd.web;

import javax.servlet.http.HttpServletRequest;

public final class BaseUrlLocator {
    public static String getBaseUrlFrom(HttpServletRequest request) {
        StringBuilder baseUrl = new StringBuilder(32);
        baseUrl.append(request.getScheme());
        baseUrl.append("://");
        baseUrl.append(request.getServerName());
        if (!BaseUrlLocator.isStandardPort(request.getScheme(), request.getServerPort())) {
            baseUrl.append(":");
            baseUrl.append(request.getServerPort());
        }
        baseUrl.append(request.getContextPath());
        return baseUrl.toString();
    }

    private static boolean isStandardPort(String scheme, int port) {
        if (scheme.equalsIgnoreCase("http") && port == 80) {
            return true;
        }
        return scheme.equalsIgnoreCase("https") && port == 443;
    }
}

