/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocalizedTextProvider;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class TokenHelper {
    public static final String TOKEN_NAMESPACE = "struts.tokens";
    public static final String DEFAULT_TOKEN_NAME = "token";
    public static final String TOKEN_NAME_FIELD = "struts.token.name";
    private static final Logger LOG = LogManager.getLogger(TokenHelper.class);
    private static final Random RANDOM = new SecureRandom();

    public static String setToken() {
        return TokenHelper.setToken(DEFAULT_TOKEN_NAME);
    }

    public static String setToken(String tokenName) {
        String token = TokenHelper.generateGUID();
        TokenHelper.setSessionToken(tokenName, token);
        return token;
    }

    public static void setSessionToken(String tokenName, String token) {
        Map<String, Object> session = ActionContext.getContext().getSession();
        try {
            session.put(TokenHelper.buildTokenSessionAttributeName(tokenName), token);
        }
        catch (IllegalStateException e) {
            String msg = "Error creating HttpSession due response is committed to client. You can use the CreateSessionInterceptor or create the HttpSession from your action before the result is rendered to the client: " + e.getMessage();
            LOG.error(msg, (Throwable)e);
            throw new IllegalArgumentException(msg);
        }
    }

    public static String buildTokenSessionAttributeName(String tokenName) {
        return "struts.tokens." + tokenName;
    }

    public static String getToken() {
        return TokenHelper.getToken(DEFAULT_TOKEN_NAME);
    }

    public static String getToken(String tokenName) {
        if (tokenName == null) {
            return null;
        }
        HttpParameters params = ActionContext.getContext().getParameters();
        Parameter parameter = params.get(tokenName);
        if (!parameter.isDefined()) {
            LOG.warn("Could not find token mapped to token name: {}", (Object)tokenName);
            return null;
        }
        return parameter.getValue();
    }

    public static String getTokenName() {
        HttpParameters params = ActionContext.getContext().getParameters();
        if (!params.contains(TOKEN_NAME_FIELD)) {
            LOG.warn("Could not find token name in params.");
            return null;
        }
        Parameter parameter = params.get(TOKEN_NAME_FIELD);
        if (!parameter.isDefined()) {
            LOG.warn("Got a null or empty token name.");
            return null;
        }
        return parameter.getValue();
    }

    public static boolean validToken() {
        String tokenSessionName;
        String tokenName = TokenHelper.getTokenName();
        if (tokenName == null) {
            LOG.debug("No token name found -> Invalid token ");
            return false;
        }
        String token = TokenHelper.getToken(tokenName);
        if (token == null) {
            LOG.debug("No token found for token name {} -> Invalid token ", (Object)tokenName);
            return false;
        }
        Map<String, Object> session = ActionContext.getContext().getSession();
        String sessionToken = (String)session.get(tokenSessionName = TokenHelper.buildTokenSessionAttributeName(tokenName));
        if (!token.equals(sessionToken)) {
            if (LOG.isWarnEnabled()) {
                LocalizedTextProvider localizedTextProvider = ActionContext.getContext().getContainer().getInstance(LocalizedTextProvider.class);
                LOG.warn(localizedTextProvider.findText(TokenHelper.class, "struts.internal.invalid.token", ActionContext.getContext().getLocale(), "Form token {0} does not match the session token {1}.", new Object[]{token, sessionToken}));
            }
            return false;
        }
        session.remove(tokenSessionName);
        return true;
    }

    public static String generateGUID() {
        return new BigInteger(165, RANDOM).toString(36).toUpperCase();
    }
}

