/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.oauth2.provider.api.xsrf.OAuth2XsrfTokenGenerator
 *  com.atlassian.oauth2.provider.api.xsrf.exeption.XsrfSessionException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.xsrf;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.oauth2.provider.api.xsrf.OAuth2XsrfTokenGenerator;
import com.atlassian.oauth2.provider.api.xsrf.exeption.XsrfSessionException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultOAuth2XsrfTokenGenerator
implements OAuth2XsrfTokenGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DefaultOAuth2XsrfTokenGenerator.class);
    @VisibleForTesting
    static final String TOKEN_SESSION_KEY = "atlassian.oauth2.xsrf.token";
    static final String INVALID_SESSION_ERROR = "oauth2.xsrf.invalid.session";
    static final String OAUTH2_XSRF_PARAM_NAME = "atl_token";
    private final I18nResolver i18nResolver;

    public DefaultOAuth2XsrfTokenGenerator(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public String generateToken(HttpServletRequest request) {
        try {
            return this.getToken(request);
        }
        catch (XsrfSessionException e) {
            return null;
        }
    }

    public String getXsrfTokenName() {
        return OAUTH2_XSRF_PARAM_NAME;
    }

    public boolean validateToken(HttpServletRequest request) throws XsrfSessionException {
        String token = request.getParameter(OAUTH2_XSRF_PARAM_NAME);
        return token != null && token.equals(this.getTokenSavedInSession(request));
    }

    private Object getTokenSavedInSession(HttpServletRequest request) throws XsrfSessionException {
        HttpSession session = this.validateSession(request);
        Object tokenSavedInSession = session.getAttribute(TOKEN_SESSION_KEY);
        session.removeAttribute(TOKEN_SESSION_KEY);
        return tokenSavedInSession;
    }

    public HttpSession validateSession(HttpServletRequest request) throws XsrfSessionException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new XsrfSessionException(this.i18nResolver.getText(INVALID_SESSION_ERROR));
        }
        return session;
    }

    private String createToken() {
        return DefaultSecureTokenGenerator.getInstance().generateToken();
    }

    private String getToken(HttpServletRequest request) throws XsrfSessionException {
        HttpSession session = this.validateSession(request);
        String token = (String)session.getAttribute(TOKEN_SESSION_KEY);
        if (token == null) {
            token = this.createToken();
            session.setAttribute(TOKEN_SESSION_KEY, (Object)token);
            logger.debug("Unable to resolve [{}] from session. Generating a custom token.", (Object)TOKEN_SESSION_KEY);
        }
        return token;
    }
}

