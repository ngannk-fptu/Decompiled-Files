/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.security.auth.trustedapps.filter;

import com.atlassian.security.auth.trustedapps.filter.Authenticator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
public interface AuthenticationListener {
    public void authenticationSuccess(Authenticator.Result var1, HttpServletRequest var2, HttpServletResponse var3);

    public void authenticationFailure(Authenticator.Result var1, HttpServletRequest var2, HttpServletResponse var3);

    public void authenticationError(Authenticator.Result var1, HttpServletRequest var2, HttpServletResponse var3);

    public void authenticationNotAttempted(HttpServletRequest var1, HttpServletResponse var2);
}

