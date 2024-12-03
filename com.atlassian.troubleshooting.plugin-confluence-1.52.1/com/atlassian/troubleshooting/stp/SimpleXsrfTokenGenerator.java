/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.troubleshooting.stp;

import com.atlassian.security.random.DefaultSecureTokenGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SimpleXsrfTokenGenerator {
    public static final String TOKEN_SESSION_KEY = "atlassian.xsrf.token";

    public String generateToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = (String)session.getAttribute(TOKEN_SESSION_KEY);
        if (token == null) {
            token = this.createToken();
            session.setAttribute(TOKEN_SESSION_KEY, (Object)token);
        }
        return token;
    }

    public String getXsrfTokenName() {
        return "atl_token";
    }

    public boolean validateToken(HttpServletRequest request, String token) {
        return token != null && token.equals(request.getSession(true).getAttribute(TOKEN_SESSION_KEY));
    }

    private String createToken() {
        return DefaultSecureTokenGenerator.getInstance().generateToken();
    }
}

