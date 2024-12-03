/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.sal.api.auth;

import com.atlassian.sal.api.auth.Authenticator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationListener {
    public void authenticationSuccess(Authenticator.Result var1, HttpServletRequest var2, HttpServletResponse var3);

    public void authenticationFailure(Authenticator.Result var1, HttpServletRequest var2, HttpServletResponse var3);

    public void authenticationError(Authenticator.Result var1, HttpServletRequest var2, HttpServletResponse var3);

    public void authenticationNotAttempted(HttpServletRequest var1, HttpServletResponse var2);
}

