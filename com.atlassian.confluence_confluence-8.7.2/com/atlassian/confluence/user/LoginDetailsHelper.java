/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.user;

import javax.servlet.http.HttpServletRequest;

public final class LoginDetailsHelper {
    public static final String DIRECT_LOGIN = "com.atlassian.confluence.login.direct";
    private static final String SSO_LOGIN = "com.atlassian.plugins.authentication.userLoggedInWithSso";

    private LoginDetailsHelper() {
    }

    public static boolean isDirectLogin(HttpServletRequest request) {
        Boolean directObj = (Boolean)request.getAttribute(DIRECT_LOGIN);
        return Boolean.TRUE.equals(directObj);
    }

    public static boolean isSsoLogin(HttpServletRequest request) {
        Boolean ssoLoginObj = (Boolean)request.getSession().getAttribute(SSO_LOGIN);
        return Boolean.TRUE.equals(ssoLoginObj);
    }
}

