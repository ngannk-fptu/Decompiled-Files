/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.sal.core.auth;

import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SeraphAuthenticationListener
implements AuthenticationListener {
    private static final String ALREADY_FILTERED = "loginfilter.already.filtered";

    public void authenticationSuccess(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response) {
        request.getSession().setAttribute("seraph_defaultauthenticator_user", (Object)result.getPrincipal());
        request.getSession().setAttribute("seraph_defaultauthenticator_logged_out_user", null);
        request.setAttribute("os_authstatus", (Object)"success");
        request.setAttribute(ALREADY_FILTERED, (Object)Boolean.TRUE);
    }

    public void authenticationError(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response) {
    }

    public void authenticationFailure(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response) {
    }

    public void authenticationNotAttempted(HttpServletRequest request, HttpServletResponse response) {
    }
}

