/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.api.xsrf;

import javax.servlet.http.HttpServletRequest;

public final class XsrfHeaderValidator {
    private static final String TOKEN_VALUE = "no-check";
    public static final String TOKEN_HEADER = "X-Atlassian-Token";

    public boolean requestHasValidXsrfHeader(HttpServletRequest request) {
        return this.isValidHeaderValue(request.getHeader(TOKEN_HEADER));
    }

    public boolean isValidHeaderValue(String headerValue) {
        if (headerValue == null) {
            return false;
        }
        return headerValue.equalsIgnoreCase(TOKEN_VALUE);
    }
}

