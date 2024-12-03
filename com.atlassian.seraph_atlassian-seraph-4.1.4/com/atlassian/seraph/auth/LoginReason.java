/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.seraph.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum LoginReason {
    AUTHENTICATION_DENIED,
    AUTHENTICATED_FAILED,
    AUTHORISATION_FAILED,
    OUT,
    OK;

    public static final String X_SERAPH_LOGIN_REASON = "X-Seraph-LoginReason";
    public static final String REQUEST_ATTR_NAME;

    public LoginReason stampRequestResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (httpServletRequest.getAttribute(REQUEST_ATTR_NAME) == null) {
            httpServletRequest.setAttribute(REQUEST_ATTR_NAME, (Object)this);
            if (httpServletResponse != null) {
                httpServletResponse.addHeader(X_SERAPH_LOGIN_REASON, this.toString());
            }
        }
        return this;
    }

    public boolean isStamped(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getAttribute(REQUEST_ATTR_NAME) == this;
    }

    static {
        REQUEST_ATTR_NAME = LoginReason.class.getName();
    }
}

