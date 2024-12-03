/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationAccessDeniedException
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.CrowdException
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidTokenException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.authentication.CookieConfiguration
 *  com.atlassian.crowd.model.user.User
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.integration.http;

import com.atlassian.crowd.exception.ApplicationAccessDeniedException;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.CrowdException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidTokenException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.integration.AuthenticationState;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.NoOpTokenLockProvider;
import com.atlassian.crowd.integration.http.TokenLockProvider;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.model.authentication.CookieConfiguration;
import com.atlassian.crowd.model.authentication.Session;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;
import java.security.Principal;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrowdHttpAuthenticatorImpl
implements CrowdHttpAuthenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrowdHttpAuthenticator.class);
    private final CrowdClient client;
    private final ClientProperties clientProperties;
    private final CrowdHttpTokenHelper tokenHelper;
    private final TokenLockProvider tokenLockProvider;

    public CrowdHttpAuthenticatorImpl(CrowdClient client, ClientProperties clientProperties, CrowdHttpTokenHelper tokenHelper) {
        this(client, clientProperties, tokenHelper, new NoOpTokenLockProvider());
    }

    public CrowdHttpAuthenticatorImpl(CrowdClient client, ClientProperties clientProperties, CrowdHttpTokenHelper tokenHelper, TokenLockProvider tokenLockProvider) {
        this.client = client;
        this.clientProperties = clientProperties;
        this.tokenHelper = tokenHelper;
        this.tokenLockProvider = tokenLockProvider;
    }

    @Override
    public User getUser(HttpServletRequest request) throws InvalidTokenException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        String ssoToken = this.tokenHelper.getCrowdToken(request, this.getCookieTokenKey());
        if (ssoToken != null) {
            return this.client.findUserFromSSOToken(ssoToken);
        }
        LOGGER.debug("Could not find user from token.");
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public User authenticate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws InvalidTokenException, ApplicationAccessDeniedException, ExpiredCredentialException, InactiveAccountException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserAuthenticationContext userAuthenticationContext = this.tokenHelper.getUserAuthenticationContext(request, username, password, this.clientProperties);
        CookieConfiguration cookieConfig = this.client.getCookieConfiguration();
        String ssoToken = null;
        try {
            ssoToken = this.client.authenticateSSOUser(userAuthenticationContext);
            this.tokenHelper.setCrowdToken(request, response, ssoToken, this.clientProperties, cookieConfig);
        }
        finally {
            if (ssoToken == null) {
                this.tokenHelper.removeCrowdToken(request, response, this.clientProperties, cookieConfig);
            }
        }
        return this.client.findUserFromSSOToken(ssoToken);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public User authenticateWithoutValidatingPassword(HttpServletRequest request, HttpServletResponse response, String username) throws InvalidTokenException, ApplicationAccessDeniedException, InactiveAccountException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        UserAuthenticationContext userAuthenticationContext = this.tokenHelper.getUserAuthenticationContext(request, username, null, this.clientProperties);
        CookieConfiguration cookieConfig = this.client.getCookieConfiguration();
        String ssoToken = null;
        try {
            ssoToken = this.client.authenticateSSOUserWithoutValidatingPassword(userAuthenticationContext);
            this.tokenHelper.setCrowdToken(request, response, ssoToken, this.clientProperties, cookieConfig);
        }
        finally {
            if (ssoToken == null) {
                this.tokenHelper.removeCrowdToken(request, response, this.clientProperties, cookieConfig);
            }
        }
        return this.client.findUserFromSSOToken(ssoToken);
    }

    @Override
    @Deprecated
    public boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) throws OperationFailedException {
        return this.checkAuthenticated(request, response).isAuthenticated();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AuthenticationState checkAuthenticated(HttpServletRequest request, HttpServletResponse response) throws OperationFailedException {
        if (!this.shouldRevalidateSession(request)) {
            return AuthenticationState.authenticated();
        }
        String token = this.getToken(request);
        if (token == null) {
            LOGGER.debug("Non authenticated request, unable to find a valid Crowd token.");
            return AuthenticationState.unauthenticated();
        }
        Lock lockForToken = this.tokenLockProvider.getLock(token);
        lockForToken.lock();
        try {
            if (!this.shouldRevalidateSession(request)) {
                AuthenticationState authenticationState = AuthenticationState.authenticated();
                return authenticationState;
            }
            Session crowdSession = this.client.validateSSOAuthenticationAndGetSession(token, this.tokenHelper.getValidationFactorExtractor().getValidationFactors(request));
            CookieConfiguration cookieConfig = this.client.getCookieConfiguration();
            this.tokenHelper.setCrowdToken(request, response, token, this.clientProperties, cookieConfig);
            Principal principal = crowdSession.getUser();
            AuthenticationState authenticationState = AuthenticationState.authenticated(principal);
            return authenticationState;
        }
        catch (ApplicationPermissionException | InvalidAuthenticationException | InvalidTokenException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage(), e);
            }
            AuthenticationState authenticationState = AuthenticationState.unauthenticated();
            return authenticationState;
        }
        finally {
            lockForToken.unlock();
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException {
        CookieConfiguration cookieConfig = this.client.getCookieConfiguration();
        String ssoToken = this.tokenHelper.getCrowdToken(request, this.getCookieTokenKey(cookieConfig));
        if (ssoToken != null && !ssoToken.isEmpty()) {
            this.client.invalidateSSOToken(ssoToken);
        }
        this.tokenHelper.removeCrowdToken(request, response, this.clientProperties, cookieConfig);
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return this.tokenHelper.getCrowdToken(request, this.getCookieTokenKey());
    }

    private String getCookieTokenKey(CookieConfiguration config) {
        return this.clientProperties.getCookieTokenKey(config.getName());
    }

    private String getCookieTokenKey() {
        String configuredKey = this.clientProperties.getCookieTokenKey(null);
        if (configuredKey != null) {
            return configuredKey;
        }
        try {
            return this.client.getCookieConfiguration().getName();
        }
        catch (CrowdException e) {
            LOGGER.info("Failed to get cookie configuration from remote Crowd", (Throwable)e);
            return this.clientProperties.getCookieTokenKey();
        }
        catch (ApplicationPermissionException e) {
            LOGGER.info("Failed to get cookie configuration from remote Crowd", (Throwable)e);
            return this.clientProperties.getCookieTokenKey();
        }
    }

    private boolean shouldRevalidateSession(HttpServletRequest request) {
        Date lastValidation;
        HttpSession session = request.getSession(false);
        if (session == null || this.clientProperties.getSessionValidationInterval() == 0L) {
            return true;
        }
        try {
            lastValidation = (Date)session.getAttribute(this.clientProperties.getSessionLastValidation());
        }
        catch (IllegalStateException e) {
            return true;
        }
        if (lastValidation != null) {
            long timeSpread = lastValidation.getTime() + TimeUnit.MINUTES.toMillis(this.clientProperties.getSessionValidationInterval());
            return timeSpread <= System.currentTimeMillis();
        }
        return true;
    }
}

