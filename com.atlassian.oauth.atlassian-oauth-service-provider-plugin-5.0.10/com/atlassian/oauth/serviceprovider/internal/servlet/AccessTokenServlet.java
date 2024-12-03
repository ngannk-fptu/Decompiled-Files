/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.Clock
 *  com.atlassian.oauth.serviceprovider.InvalidTokenException
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Authorization
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Version
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  net.oauth.OAuth
 *  net.oauth.OAuthMessage
 *  net.oauth.OAuthProblemException
 *  net.oauth.OAuthValidator
 *  net.oauth.server.OAuthServlet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.oauth.serviceprovider.internal.servlet;

import com.atlassian.oauth.serviceprovider.Clock;
import com.atlassian.oauth.serviceprovider.InvalidTokenException;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.internal.OAuthConverter;
import com.atlassian.oauth.serviceprovider.internal.TokenFactory;
import com.atlassian.oauth.serviceprovider.internal.servlet.OAuthProblemUtils;
import com.atlassian.oauth.serviceprovider.internal.servlet.TransactionalServlet;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.server.OAuthServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class AccessTokenServlet
extends TransactionalServlet {
    private final Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private final TokenFactory factory;
    private final OAuthValidator validator;
    private final ApplicationProperties applicationProperties;
    private final OAuthConverter converter;
    private final ServiceProviderTokenStore tokenStore;
    private final Clock clock;

    public AccessTokenServlet(@Qualifier(value="tokenStore") ServiceProviderTokenStore tokenStore, TokenFactory factory, OAuthValidator validator, ApplicationProperties applicationProperties, OAuthConverter converter, TransactionTemplate transactionTemplate, Clock clock) {
        super(transactionTemplate);
        this.tokenStore = Objects.requireNonNull(tokenStore, "store");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.converter = Objects.requireNonNull(converter, "converter");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public void doPostInTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServiceProviderToken accessToken;
        try {
            ServiceProviderToken token;
            OAuthMessage requestMessage = OAuthServlet.getMessage((HttpServletRequest)request, null);
            requestMessage.requireParameters(new String[]{"oauth_token"});
            try {
                token = this.tokenStore.get(requestMessage.getToken());
            }
            catch (InvalidTokenException e) {
                throw new OAuthProblemException("token_rejected");
            }
            if (token == null) {
                throw new OAuthProblemException("token_rejected");
            }
            if (token.isRequestToken()) {
                this.checkRequestToken(requestMessage, token);
            } else {
                this.checkAccessToken(requestMessage, token);
            }
            try {
                this.validator.validateMessage(requestMessage, this.converter.toOAuthAccessor(token));
            }
            catch (OAuthProblemException ope) {
                OAuthProblemUtils.logOAuthProblem(requestMessage, ope, this.log);
                throw ope;
            }
            accessToken = this.tokenStore.put(this.factory.generateAccessToken(token));
            this.tokenStore.removeAndNotify(token.getToken());
        }
        catch (Exception e) {
            OAuthServlet.handleException((HttpServletResponse)response, (Exception)e, (String)this.applicationProperties.getBaseUrl(), (boolean)true);
            return;
        }
        response.setContentType("text/plain");
        ServletOutputStream out = response.getOutputStream();
        OAuth.formEncode((Iterable)OAuth.newList((String[])new String[]{"oauth_token", accessToken.getToken(), "oauth_token_secret", accessToken.getTokenSecret(), "oauth_expires_in", Long.toString(accessToken.getTimeToLive() / 1000L), "oauth_session_handle", accessToken.getSession().getHandle(), "oauth_authorization_expires_in", Long.toString(accessToken.getSession().getTimeToLive() / 1000L)}), (OutputStream)out);
    }

    private void checkRequestToken(OAuthMessage requestMessage, ServiceProviderToken token) throws Exception {
        if (token.hasExpired(this.clock)) {
            throw new OAuthProblemException("token_expired");
        }
        if (token.getAuthorization() == ServiceProviderToken.Authorization.NONE) {
            throw new OAuthProblemException("permission_unknown");
        }
        if (token.getAuthorization() == ServiceProviderToken.Authorization.DENIED) {
            throw new OAuthProblemException("permission_denied");
        }
        if (!token.getConsumer().getKey().equals(requestMessage.getConsumerKey())) {
            throw new OAuthProblemException("token_rejected");
        }
        if (ServiceProviderToken.Version.V_1_0_A.equals((Object)token.getVersion())) {
            requestMessage.requireParameters(new String[]{"oauth_verifier"});
            if (!token.getVerifier().equals(requestMessage.getParameter("oauth_verifier"))) {
                throw new OAuthProblemException("token_rejected");
            }
        }
    }

    private void checkAccessToken(OAuthMessage requestMessage, ServiceProviderToken token) throws Exception {
        if (token.getSession() == null) {
            throw new OAuthProblemException("token_rejected");
        }
        requestMessage.requireParameters(new String[]{"oauth_session_handle"});
        if (!token.getSession().getHandle().equals(requestMessage.getParameter("oauth_session_handle"))) {
            throw new OAuthProblemException("token_rejected");
        }
        if (token.getSession().hasExpired(this.clock)) {
            throw new OAuthProblemException("permission_denied");
        }
    }
}

