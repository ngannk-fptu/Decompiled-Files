/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.auth;

import com.atlassian.seraph.config.SecurityConfig;
import javax.servlet.http.HttpServletRequest;

public enum AuthType {
    NONE,
    COOKIE,
    BASIC,
    ANY;

    public static final String DEFAULT_ATTRIBUTE = "os_authTypeDefault";

    public static AuthType getAuthTypeInformation(HttpServletRequest request, SecurityConfig config) {
        String authTypeParamName = config.getAuthType();
        String authType = request.getParameter(authTypeParamName);
        if (authType == null) {
            authType = (String)request.getAttribute(DEFAULT_ATTRIBUTE);
        }
        if (authType == null) {
            return NONE;
        }
        try {
            return AuthType.valueOf(authType.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}

