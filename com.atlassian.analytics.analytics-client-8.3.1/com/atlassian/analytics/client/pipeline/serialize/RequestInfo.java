/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.analytics.client.pipeline.serialize;

import com.google.common.annotations.VisibleForTesting;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RequestInfo {
    @VisibleForTesting
    static final String X_FORWARDED_FOR_FIELD = "X-FORWARDED-FOR";
    @VisibleForTesting
    static final String ATL_PATH_COOKIE_NAME = "__atl_path";
    private String sourceIp;
    private String atlPath;
    private String B3TraceId;

    private RequestInfo(String sourceIp, String atlPath, String B3TraceId) {
        this.sourceIp = sourceIp;
        this.atlPath = atlPath;
        this.B3TraceId = B3TraceId;
    }

    public String getSourceIp() {
        return this.sourceIp;
    }

    public String getAtlPath() {
        return this.atlPath;
    }

    public String getB3TraceId() {
        return this.B3TraceId;
    }

    public static RequestInfo fromRequest(HttpServletRequest request) {
        return new RequestInfo(RequestInfo.extractSourceIP(request), RequestInfo.extractAtlPath(request), RequestInfo.extractB3TraceIdFinal(request));
    }

    static String extractB3TraceIdFinal(HttpServletRequest httpRequest) {
        if (httpRequest == null) {
            return null;
        }
        Object traceId = httpRequest.getAttribute("B3-TraceId");
        if (traceId instanceof String) {
            return (String)traceId;
        }
        return null;
    }

    @VisibleForTesting
    static String extractSourceIP(HttpServletRequest httpRequest) {
        if (httpRequest == null) {
            return null;
        }
        String ipAddress = httpRequest.getHeader(X_FORWARDED_FOR_FIELD);
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            return ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    @VisibleForTesting
    static String extractAtlPath(HttpServletRequest httpRequest) {
        Cookie[] cookies;
        if (httpRequest != null && (cookies = httpRequest.getCookies()) != null) {
            for (Cookie cookie : cookies) {
                if (!ATL_PATH_COOKIE_NAME.equals(cookie.getName())) continue;
                return cookie.getValue();
            }
        }
        return null;
    }
}

