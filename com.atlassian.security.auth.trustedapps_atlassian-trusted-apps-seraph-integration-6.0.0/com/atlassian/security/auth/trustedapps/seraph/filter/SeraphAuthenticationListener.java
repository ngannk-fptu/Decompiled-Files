/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.filter.AuthenticationListener
 *  com.atlassian.security.auth.trustedapps.filter.Authenticator$Result
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.security.auth.trustedapps.seraph.filter;

import com.atlassian.security.auth.trustedapps.filter.AuthenticationListener;
import com.atlassian.security.auth.trustedapps.filter.Authenticator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SeraphAuthenticationListener
implements AuthenticationListener {
    public void authenticationSuccess(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response) {
        request.getSession().setAttribute("seraph_defaultauthenticator_user", (Object)result.getUser());
        request.getSession().setAttribute("seraph_defaultauthenticator_logged_out_user", null);
        request.setAttribute("os_authstatus", (Object)"success");
    }

    public void authenticationFailure(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response) {
    }

    public void authenticationError(Authenticator.Result result, HttpServletRequest request, HttpServletResponse response) {
    }

    public void authenticationNotAttempted(HttpServletRequest request, HttpServletResponse response) {
    }
}

