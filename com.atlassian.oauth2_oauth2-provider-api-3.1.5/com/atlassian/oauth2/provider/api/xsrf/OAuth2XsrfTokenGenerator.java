/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.oauth2.provider.api.xsrf;

import com.atlassian.oauth2.provider.api.xsrf.exeption.XsrfSessionException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface OAuth2XsrfTokenGenerator {
    public String generateToken(HttpServletRequest var1);

    public String getXsrfTokenName();

    public boolean validateToken(HttpServletRequest var1) throws XsrfSessionException;

    public HttpSession validateSession(HttpServletRequest var1) throws XsrfSessionException;
}

