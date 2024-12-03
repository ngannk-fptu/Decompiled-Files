/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.serviceprovider.Clock
 *  com.atlassian.oauth.serviceprovider.InvalidTokenException
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.oauth.util.RequestAnnotations
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.sal.api.auth.Authenticator
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Error
 *  com.atlassian.sal.api.auth.Authenticator$Result$Failure
 *  com.atlassian.sal.api.auth.Authenticator$Result$Success
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  net.oauth.OAuthAccessor
 *  net.oauth.OAuthConsumer
 *  net.oauth.OAuthException
 *  net.oauth.OAuthMessage
 *  net.oauth.OAuthProblemException
 *  net.oauth.OAuthValidator
 *  net.oauth.server.OAuthServlet
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.serviceprovider.Clock;
import com.atlassian.oauth.serviceprovider.InvalidTokenException;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.internal.OAuthConverter;
import com.atlassian.oauth.serviceprovider.internal.OAuthProblem;
import com.atlassian.oauth.serviceprovider.internal.servlet.OAuthProblemUtils;
import com.atlassian.oauth.serviceprovider.internal.servlet.OAuthRequestUtils;
import com.atlassian.oauth.util.RequestAnnotations;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.server.OAuthServlet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="authenticator")
public class AuthenticatorImpl
implements Authenticator {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatorImpl.class);
    private static final String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
    private static final String XOAUTH_REQUESTOR_ID = "xoauth_requestor_id";
    @Deprecated
    private static final String REMOTEAPP_REQUESTOR_ID = "user_id";
    private final ServiceProviderTokenStore store;
    private final OAuthValidator validator;
    private final OAuthConverter converter;
    private final AuthenticationController authenticationController;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationProperties applicationProperties;
    private final Clock clock;
    private final ServiceProviderConsumerStore serviceProviderConsumerStore;
    private final UserManager userManager;

    @Autowired
    public AuthenticatorImpl(@Qualifier(value="tokenStore") ServiceProviderTokenStore store, OAuthValidator validator, OAuthConverter converter, AuthenticationController authenticationController, TransactionTemplate transactionTemplate, ApplicationProperties applicationProperties, Clock clock, ServiceProviderConsumerStore serviceProviderConsumerStore, UserManager userManager) {
        this.store = Objects.requireNonNull(store, "store");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.converter = Objects.requireNonNull(converter, "converter");
        this.authenticationController = Objects.requireNonNull(authenticationController, "authenticationController");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.serviceProviderConsumerStore = Objects.requireNonNull(serviceProviderConsumerStore, "serviceProviderConsumerStore");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    public Authenticator.Result authenticate(HttpServletRequest request, HttpServletResponse response) {
        if (OAuthRequestUtils.is2LOAuthAccessAttempt(request)) {
            return this.authenticate2LORequest(request, response);
        }
        if (OAuthRequestUtils.is3LOAuthAccessAttempt(request)) {
            return this.authenticate3LORequest(request, response);
        }
        throw new IllegalArgumentException("This Authenticator only works with OAuth requests");
    }

    private Consumer validateConsumer(OAuthMessage message) throws IOException, OAuthException {
        String consumerKey = message.getConsumerKey();
        Consumer consumer = this.serviceProviderConsumerStore.get(consumerKey);
        if (consumer == null) {
            LOG.info("Unknown consumer key:'{}' supplied in OAuth request" + consumerKey);
            throw new OAuthProblemException("consumer_key_unknown");
        }
        return consumer;
    }

    void validate2LOMessage(OAuthMessage message, Consumer consumer) throws OAuthException, IOException, URISyntaxException {
        OAuthConsumer oauthConsumer = this.converter.toOAuthConsumer(consumer);
        oauthConsumer.setProperty("RSA-SHA1.PublicKey", (Object)consumer.getPublicKey().getEncoded());
        OAuthAccessor oauthAccessor = new OAuthAccessor(oauthConsumer);
        this.printMessageToDebug(message);
        this.validator.validateMessage(message, oauthAccessor);
    }

    private void printMessageToDebug(OAuthMessage message) throws IOException {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder("Validating incoming OAuth request:\n");
        sb.append("\turl: ").append(message.URL).append("\n");
        sb.append("\tmethod: ").append(message.method).append("\n");
        for (Map.Entry entry : message.getParameters()) {
            sb.append("\t").append((String)entry.getKey()).append(": ").append((String)entry.getValue()).append("\n");
        }
        LOG.debug(sb.toString());
    }

    void validate3LOMessage(OAuthMessage message, ServiceProviderToken token) throws OAuthException, IOException, URISyntaxException {
        this.printMessageToDebug(message);
        this.validator.validateMessage(message, this.converter.toOAuthAccessor(token));
    }

    public Authenticator.Result authenticate3LORequest(HttpServletRequest request, HttpServletResponse response) {
        Consumer consumer;
        ServiceProviderToken token;
        String tokenStr;
        OAuthMessage message = OAuthServlet.getMessage((HttpServletRequest)request, (String)this.getLogicalUri(request));
        try {
            tokenStr = message.getToken();
        }
        catch (IOException e) {
            LOG.error("3-Legged-OAuth Failed to read token from request", (Throwable)e);
            this.sendError(response, 500, message);
            return new Authenticator.Result.Error((Message)new OAuthProblem.UnreadableToken(e));
        }
        try {
            try {
                token = this.getToken(tokenStr);
            }
            catch (InvalidTokenException e) {
                LOG.debug(String.format("3-Legged-OAuth Consumer provided token [%s] rejected by ServiceProviderTokenStore", tokenStr), (Throwable)e);
                throw new OAuthProblemException("token_rejected");
            }
            if (token == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("3-Legged-OAuth token rejected. Service Provider Token, for Consumer provided token [%s], is null", tokenStr));
                }
                throw new OAuthProblemException("token_rejected");
            }
            if (!token.isAccessToken()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("3-Legged-OAuth token rejected. Service Provider Token, for Consumer provided token [%s], is NOT an access token.", tokenStr));
                }
                throw new OAuthProblemException("token_rejected");
            }
            if (!token.getConsumer().getKey().equals(message.getConsumerKey())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("3-Legged-OAuth token rejected. Service Provider Token, for Consumer provided token [%s], consumer key [%s] does not match request consumer key [%s]", tokenStr, token.getConsumer().getKey(), message.getConsumerKey()));
                }
                throw new OAuthProblemException("token_rejected");
            }
            if (token.hasExpired(this.clock)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("3-Legged-OAuth token rejected. Token has expired. Token creation time [%d] time to live [%d] clock (contains logging delay) [%d]", token.getCreationTime(), token.getTimeToLive(), this.clock.timeInMilliseconds()));
                }
                throw new OAuthProblemException("token_expired");
            }
            this.validate3LOMessage(message, token);
            consumer = this.validateConsumer(message);
            if (!consumer.getThreeLOAllowed()) {
                LOG.info("3-Legged-OAuth request has been attempted but 3-Legged-OAuth is not enabled for consumer:'{}'." + consumer.getKey());
                throw new OAuthProblemException("permission_denied");
            }
        }
        catch (OAuthProblemException ope) {
            return this.handleOAuthProblemException(response, message, tokenStr, ope);
        }
        catch (Exception e) {
            return this.handleException(response, message, e);
        }
        Principal user = token.getUser();
        RequestAnnotations.setOAuthConsumerKey((HttpServletRequest)request, (String)consumer.getKey());
        LOG.debug(String.format("3-Legged-OAuth successful. Request marked with consumer key set to [%s]", consumer.getKey()));
        return this.getUserLoginResult(request, response, message, consumer, user);
    }

    public Authenticator.Result authenticate2LORequest(HttpServletRequest request, HttpServletResponse response) {
        Principal user;
        Consumer consumer;
        OAuthMessage message = OAuthServlet.getMessage((HttpServletRequest)request, (String)this.getLogicalUri(request));
        try {
            consumer = this.validateConsumer(message);
            this.validate2LOMessage(message, consumer);
        }
        catch (OAuthProblemException ope) {
            return this.handleOAuthProblemException(response, message, null, ope);
        }
        catch (Exception e) {
            return this.handleException(response, message, e);
        }
        String userId = request.getParameter(XOAUTH_REQUESTOR_ID);
        LOG.debug("2-Legged-OAuth userId [{}] from request parameter [{}].", (Object)userId, (Object)XOAUTH_REQUESTOR_ID);
        if (userId == null) {
            userId = request.getParameter(REMOTEAPP_REQUESTOR_ID);
            LOG.debug("2-Legged-OAuth userId [{}] from request parameter [{}].", (Object)userId, (Object)REMOTEAPP_REQUESTOR_ID);
        }
        if (userId != null) {
            if (!consumer.getTwoLOImpersonationAllowed()) {
                LOG.info("2-Legged-OAuth with Impersonation request has been attempted but 2-Legged-OAuth with Impersonation is not enabled for consumer:'{}'. Cannot access resource as user '{}'", (Object)consumer.getName(), (Object)userId);
                this.sendError(response, 401, message);
                return new Authenticator.Result.Failure((Message)new OAuthProblem.PermissionDenied(userId));
            }
            user = this.userManager.resolve(userId);
            LOG.debug("2-Legged-OAuth userId [{}] resolved to [{}].", (Object)userId, (Object)(user != null ? user.getName() : "null"));
        } else {
            if (!consumer.getTwoLOAllowed()) {
                LOG.info("2-Legged-OAuth request has been attempted but 2-Legged-OAuth is not enabled for consumer:'{}'.", (Object)consumer.getName());
                this.sendError(response, 401, message);
                return new Authenticator.Result.Failure((Message)new OAuthProblem.PermissionDenied());
            }
            if (StringUtils.isBlank((CharSequence)consumer.getExecutingTwoLOUser())) {
                LOG.debug("No executing user assigned for 2LO requests");
                user = null;
            } else {
                LOG.debug("User assigned for 2LO requests is '" + consumer.getExecutingTwoLOUser() + "'");
                user = this.userManager.resolve(consumer.getExecutingTwoLOUser());
            }
        }
        RequestAnnotations.setOAuthConsumerKey((HttpServletRequest)request, (String)consumer.getKey());
        return this.getUserLoginResult(request, response, message, consumer, user);
    }

    private Authenticator.Result handleException(HttpServletResponse response, OAuthMessage message, Exception e) {
        LOG.error("Failed to validate OAuth message", (Throwable)e);
        this.sendError(response, 500, message);
        return new Authenticator.Result.Error((Message)new OAuthProblem.System(e));
    }

    private Authenticator.Result getUserLoginResult(HttpServletRequest request, HttpServletResponse response, OAuthMessage message, Consumer consumer, Principal user) {
        if (user != null && !this.authenticationController.canLogin(user, request)) {
            LOG.info("Access denied because user:'{}' cannot login", (Object)user.getName());
            this.sendError(response, 401, message);
            return new Authenticator.Result.Failure((Message)new OAuthProblem.PermissionDenied(user.getName()));
        }
        LOG.info("Authenticated app '{}' as user '{}' successfully", (Object)consumer.getKey(), (Object)(user == null ? "null" : user.getName()));
        return new Authenticator.Result.Success(user);
    }

    private Authenticator.Result handleOAuthProblemException(HttpServletResponse response, OAuthMessage message, String tokenStr, OAuthProblemException ope) {
        OAuthProblemUtils.logOAuthProblem(message, ope, LOG);
        try {
            OAuthServlet.handleException((HttpServletResponse)response, (Exception)((Object)ope), (String)this.applicationProperties.getBaseUrl());
        }
        catch (Exception e) {
            LOG.error("Failure reporting OAuth error to client", (Throwable)e);
        }
        if (ope.getProblem().equals("consumer_key_unknown")) {
            return new Authenticator.Result.Failure((Message)new OAuthProblem(OAuthProblem.Problem.valueOf(ope.getProblem().toUpperCase(Locale.ENGLISH))));
        }
        if (tokenStr != null) {
            return new Authenticator.Result.Failure((Message)new OAuthProblem(OAuthProblem.Problem.valueOf(ope.getProblem().toUpperCase(Locale.ENGLISH)), tokenStr));
        }
        return new Authenticator.Result.Failure((Message)new OAuthProblem(OAuthProblem.Problem.valueOf(ope.getProblem().toUpperCase(Locale.ENGLISH))));
    }

    private ServiceProviderToken getToken(String tokenStr) {
        return (ServiceProviderToken)this.transactionTemplate.execute(() -> this.store.get(tokenStr));
    }

    private String getLogicalUri(HttpServletRequest request) {
        String uriPathBeforeForwarding = (String)request.getAttribute(FORWARD_REQUEST_URI);
        if (uriPathBeforeForwarding == null) {
            return null;
        }
        URI newUri = URI.create(request.getRequestURL().toString());
        try {
            return new URI(newUri.getScheme(), newUri.getAuthority(), uriPathBeforeForwarding, newUri.getQuery(), newUri.getFragment()).toString();
        }
        catch (URISyntaxException e) {
            LOG.warn("forwarded request had invalid original URI path: " + uriPathBeforeForwarding);
            return null;
        }
    }

    private void sendError(HttpServletResponse response, int status, OAuthMessage message) {
        response.setStatus(status);
        try {
            response.addHeader("WWW-Authenticate", message.getAuthorizationHeader(this.applicationProperties.getBaseUrl()));
        }
        catch (IOException e) {
            LOG.error("Failure reporting OAuth error to client", (Throwable)e);
        }
    }
}

