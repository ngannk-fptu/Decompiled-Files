/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.web.springmvc.xsrf;

import com.atlassian.plugin.web.springmvc.xsrf.XsrfTokenGenerator;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleXsrfTokenGenerator
implements XsrfTokenGenerator {
    public static final String TOKEN_SESSION_KEY = "atlassian.xsrf.token";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleXsrfTokenGenerator.class);

    @Override
    public String generateToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = (String)session.getAttribute(TOKEN_SESSION_KEY);
        if (token == null) {
            token = this.createToken();
            LOGGER.debug("New XSRF token generated: {}", (Object)token);
            session.setAttribute(TOKEN_SESSION_KEY, (Object)token);
        }
        return token;
    }

    @Override
    public String getXsrfTokenName() {
        return "atl_token";
    }

    @Override
    public boolean validateToken(HttpServletRequest request, String token) {
        boolean isValid;
        Object sessionToken = request.getSession(true).getAttribute(TOKEN_SESSION_KEY);
        boolean bl = isValid = token != null && token.equals(sessionToken);
        if (!isValid) {
            LOGGER.debug("XSRF check failed: requestToken='{}', sessionToken='{}'", (Object)token, sessionToken);
        }
        return isValid;
    }

    private String createToken() {
        return DefaultSecureTokenGenerator.getInstance().generateToken();
    }
}

