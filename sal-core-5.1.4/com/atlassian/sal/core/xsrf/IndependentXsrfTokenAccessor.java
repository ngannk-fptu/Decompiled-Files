/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  com.atlassian.security.random.SecureTokenGenerator
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.core.xsrf;

import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import com.atlassian.security.random.SecureTokenGenerator;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndependentXsrfTokenAccessor
implements XsrfTokenAccessor {
    private static final Logger log = LoggerFactory.getLogger(IndependentXsrfTokenAccessor.class);
    public static final String XSRF_COOKIE_KEY = "atl.xsrf.token";
    private final SecureTokenGenerator tokenGenerator = DefaultSecureTokenGenerator.getInstance();

    public String getXsrfToken(HttpServletRequest request, HttpServletResponse response, boolean create) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : request.getCookies()) {
                if (!cookie.getName().equals(XSRF_COOKIE_KEY)) continue;
                return cookie.getValue();
            }
        }
        if (create) {
            if (response.isCommitted()) {
                log.warn("Adding cookie to committed response, this will likely have no effect");
            }
            String token = this.tokenGenerator.generateToken();
            Cookie cookie = new Cookie(XSRF_COOKIE_KEY, token);
            if (request.isSecure()) {
                cookie.setSecure(true);
            }
            response.addCookie(cookie);
            return token;
        }
        return null;
    }
}

