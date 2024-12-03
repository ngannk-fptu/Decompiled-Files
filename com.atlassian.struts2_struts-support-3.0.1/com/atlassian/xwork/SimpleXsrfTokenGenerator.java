/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.xwork;

import com.atlassian.security.random.DefaultSecureTokenGenerator;
import com.atlassian.xwork.XsrfTokenGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SimpleXsrfTokenGenerator
implements XsrfTokenGenerator {
    public static final String TOKEN_SESSION_KEY = "atlassian.xsrf.token";

    @Override
    public String getToken(HttpServletRequest request, boolean create) {
        HttpSession session = request.getSession();
        String token = (String)session.getAttribute(TOKEN_SESSION_KEY);
        if (create && token == null) {
            token = this.createToken();
            session.setAttribute(TOKEN_SESSION_KEY, (Object)token);
        }
        return token;
    }

    @Override
    public String generateToken(HttpServletRequest request) {
        return this.getToken(request, true);
    }

    @Override
    public String getXsrfTokenName() {
        return "atl_token";
    }

    @Override
    public boolean validateToken(HttpServletRequest request, String token) {
        return token != null && token.equals(request.getSession(true).getAttribute(TOKEN_SESSION_KEY));
    }

    private String createToken() {
        return DefaultSecureTokenGenerator.getInstance().generateToken();
    }
}

