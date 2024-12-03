/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HashChangedHandler {
    private String etag;

    public HashChangedHandler(int hashCode) {
        this.modified(hashCode);
    }

    public boolean checkRequest(HttpServletRequest request, HttpServletResponse response) {
        return HashChangedHandler.checkRequest(request, response, this.etag);
    }

    private void modified(int hashCode) {
        this.etag = HashChangedHandler.calculateEtag(hashCode);
    }

    private static String calculateEtag(int hashCode) {
        return "\"" + hashCode + "\"";
    }

    public static boolean checkRequest(HttpServletRequest request, HttpServletResponse response, int hashCode) {
        return HashChangedHandler.checkRequest(request, response, HashChangedHandler.calculateEtag(hashCode));
    }

    private static boolean checkRequest(HttpServletRequest request, HttpServletResponse response, String etagString) {
        if ("true".equals(System.getProperty("atlassian.disable.caches", "false"))) {
            return false;
        }
        response.setHeader("ETag", etagString);
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (HashChangedHandler.noConditionalGetHeadersFound(ifModifiedSince, ifNoneMatch) || !HashChangedHandler.etagMatches(ifNoneMatch, etagString)) {
            return false;
        }
        response.setStatus(304);
        return true;
    }

    private static boolean etagMatches(String ifNoneMatch, String etagString) {
        return ifNoneMatch != null && ifNoneMatch.equals(etagString);
    }

    private static boolean noConditionalGetHeadersFound(long ifModifiedSince, String ifNoneMatch) {
        return ifModifiedSince == -1L && ifNoneMatch == null;
    }
}

