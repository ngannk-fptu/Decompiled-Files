/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.core.rest.util;

public class ResourceUrlHandler {
    private static final String SERVLET_CONTEXT = "/plugins/servlet/applinks/";
    private static final String REST_CONTEXT = "/com/atlassian/applinks/core/rest/applinks/1.0/";
    private final String baseUrl;

    public ResourceUrlHandler(String baseUrl) {
        if (baseUrl == null || baseUrl.length() == 0) {
            throw new IllegalArgumentException("baseUrl must not be null or empty");
        }
        this.baseUrl = ResourceUrlHandler.trimTailingSlash(baseUrl);
    }

    public String rest(String context) {
        return this.baseUrl + REST_CONTEXT + ResourceUrlHandler.trimLeadingSlash(context);
    }

    public String servlet(String context) {
        return this.baseUrl + SERVLET_CONTEXT + ResourceUrlHandler.trimLeadingSlash(context);
    }

    private static String trimTailingSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    private static String trimLeadingSlash(String s) {
        return s.startsWith("/") ? s.substring(1, s.length()) : s;
    }
}

