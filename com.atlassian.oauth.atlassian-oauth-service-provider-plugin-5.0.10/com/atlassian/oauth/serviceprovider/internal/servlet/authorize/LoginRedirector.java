/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.authorize;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginRedirector {
    public boolean isLoggedIn(HttpServletRequest var1);

    public void redirectToLogin(HttpServletRequest var1, HttpServletResponse var2) throws IOException;
}

