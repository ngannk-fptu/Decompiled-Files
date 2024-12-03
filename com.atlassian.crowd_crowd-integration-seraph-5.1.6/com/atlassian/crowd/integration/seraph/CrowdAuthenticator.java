/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  com.atlassian.crowd.integration.AuthenticationState
 *  com.atlassian.crowd.integration.http.CacheAwareCrowdHttpAuthenticator
 *  com.atlassian.crowd.integration.http.CrowdHttpAuthenticator
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.service.AuthenticatorUserCache
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.auth.DefaultAuthenticator
 *  com.atlassian.seraph.auth.LoginReason
 *  com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard
 *  com.atlassian.seraph.util.RedirectUtils
 *  com.google.common.base.Optional
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.integration.seraph;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.integration.AuthenticationState;
import com.atlassian.crowd.integration.http.CacheAwareCrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.service.AuthenticatorUserCache;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.atlassian.seraph.auth.LoginReason;
import com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard;
import com.atlassian.seraph.util.RedirectUtils;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.security.Principal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CrowdAuthenticator
extends DefaultAuthenticator {
    private static final String SESSION_TOKEN_KEY = CrowdAuthenticator.class.getName() + "#SESSION_TOKEN_KEY";
    public static final String PASSWORD_RESET_REQUIRED_HEADER = "X-Seraph-PasswordResetRequired";
    protected static final Logger logger = LoggerFactory.getLogger(CrowdAuthenticator.class);
    private static final String CORRECT_PASSWORD = "c";
    private static final String INCORRECT_PASSWORD = "i";
    private static final Boolean DISABLE_USER_AUTHENTICATED_NOTIFICATIONS = Boolean.valueOf("crowd.integration.seraph.user.authenticated.notification.skip");
    private static final Integer USER_AUTHENTICATED_REFRESH_EXPIRATION_SECONDS = Integer.getInteger("crowd.integration.seraph.user.authenticated.notification.expiration", 60);
    private final Cache<String, Optional<String>> userAuthenticatedSentCache = CacheBuilder.newBuilder().expireAfterWrite((long)USER_AUTHENTICATED_REFRESH_EXPIRATION_SECONDS.intValue(), TimeUnit.SECONDS).build();
    private final CrowdHttpAuthenticator crowdHttpAuthenticator;
    private final Supplier<CrowdService> crowdServiceSupplier;

    public CrowdAuthenticator(CrowdHttpAuthenticator crowdHttpAuthenticator, Supplier<CrowdService> crowdServiceSupplier) {
        this.crowdServiceSupplier = crowdServiceSupplier;
        this.crowdHttpAuthenticator = new CacheAwareCrowdHttpAuthenticator(crowdHttpAuthenticator, new AuthenticatorUserCache(){

            public void fetchInCache(String username) throws UserNotFoundException, InvalidAuthenticationException, com.atlassian.crowd.exception.OperationFailedException {
                CrowdAuthenticator.this.fetchUserInCache(username);
            }
        });
    }

    protected void fetchUserInCache(String username) throws UserNotFoundException, InvalidAuthenticationException, com.atlassian.crowd.exception.OperationFailedException {
        this.getUser(username);
    }

    protected boolean authenticate(Principal user, String password) {
        return CORRECT_PASSWORD.equals(password);
    }

    public boolean login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean cookie) throws AuthenticatorException {
        boolean authenticated;
        try {
            this.logout(request, response);
            request.setAttribute(LoginReason.REQUEST_ATTR_NAME, null);
            logger.debug("Authenticating user with Crowd");
            this.crowdServiceSupplier.get().authenticate(username, password);
            logger.debug("Establishing SSO session");
            this.crowdHttpAuthenticator.authenticate(request, response, username, password);
            authenticated = true;
        }
        catch (ExpiredCredentialException ece) {
            logger.info("Credentials have expired or were reset by an administrator", (Throwable)ece);
            authenticated = false;
            response.addHeader(PASSWORD_RESET_REQUIRED_HEADER, "true");
        }
        catch (Exception e) {
            logger.info(e.getMessage(), (Throwable)e);
            authenticated = false;
        }
        String fakePassword = authenticated ? CORRECT_PASSWORD : INCORRECT_PASSWORD;
        logger.debug("Updating user session for Seraph");
        authenticated = super.login(request, response, username, fakePassword, cookie);
        return authenticated;
    }

    public boolean logout(HttpServletRequest request, HttpServletResponse response) throws AuthenticatorException {
        try {
            logger.debug("Logging off from Crowd");
            this.crowdHttpAuthenticator.logout(request, response);
            logger.debug("Invalidating user in Crowd-Seraph specific session variables");
            this.logoutUser(request);
        }
        catch (Exception e) {
            logger.info(e.getMessage(), (Throwable)e);
        }
        logger.debug("Invalidating user in Seraph specific session variables");
        return super.logout(request, response);
    }

    @Deprecated
    protected boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) {
        return this.checkAuthenticated(request, response).isAuthenticated();
    }

    protected AuthenticationState checkAuthenticated(HttpServletRequest request, HttpServletResponse response) {
        Principal basicAuthUser;
        AuthenticationState authenticationState;
        AuthenticationState authenticationState2 = authenticationState = this.isTrustedAppsRequest(request) ? AuthenticationState.authenticated() : AuthenticationState.unauthenticated();
        if (!authenticationState.isAuthenticated()) {
            try {
                authenticationState = this.crowdHttpAuthenticator.checkAuthenticated(request, response);
                if (authenticationState.isAuthenticated() && logger.isDebugEnabled()) {
                    logger.debug("User IS authenticated via the Crowd session-token");
                } else if (logger.isDebugEnabled()) {
                    logger.debug("User is NOT authenticated via the Crowd session-token");
                }
            }
            catch (Exception e) {
                logger.info("Error while attempting to check if user isAuthenticated with Crowd", (Throwable)e);
            }
        }
        if (!authenticationState.isAuthenticated()) {
            authenticationState = this.checkRememberMeLoginToCrowd(request, response);
            if (authenticationState.isAuthenticated() && logger.isDebugEnabled()) {
                logger.debug("Authenticated via remember-me cookie");
            } else if (logger.isDebugEnabled()) {
                logger.debug("Failed to authenticate via remember-me cookie");
            }
        }
        if (!authenticationState.isAuthenticated() && RedirectUtils.isBasicAuthentication((HttpServletRequest)request, (String)this.getAuthType()) && (basicAuthUser = this.getUserFromBasicAuthentication(request, response)) != null) {
            authenticationState = AuthenticationState.authenticated((Principal)basicAuthUser);
        }
        if (!authenticationState.isAuthenticated()) {
            if (request.getSession(false) != null) {
                logger.debug("Request is not authenticated, logging out the user");
                try {
                    this.logoutUser(request);
                    if (response != null) {
                        super.logout(request, response);
                    }
                }
                catch (AuthenticatorException e) {
                    logger.error(e.getMessage(), (Throwable)e);
                }
            } else {
                logger.debug("Request is not authenticated and has no session.");
            }
            authenticationState = AuthenticationState.unauthenticated();
        }
        return authenticationState;
    }

    @Deprecated
    protected boolean rememberMeLoginToCrowd(HttpServletRequest request, HttpServletResponse response) {
        return this.checkRememberMeLoginToCrowd(request, response).isAuthenticated();
    }

    protected AuthenticationState checkRememberMeLoginToCrowd(HttpServletRequest request, HttpServletResponse response) {
        Principal cookieUser = this.getUserFromCookie(request, response);
        if (cookieUser == null) {
            return AuthenticationState.unauthenticated();
        }
        logger.debug("User successfully authenticated via remember-me cookie verification");
        try {
            com.atlassian.crowd.model.user.User user = this.crowdHttpAuthenticator.authenticateWithoutValidatingPassword(request, response, cookieUser.getName());
            return AuthenticationState.authenticated((Principal)user);
        }
        catch (Exception e) {
            logger.debug("Could not register remember-me cookie authenticated user with Crowd SSO: " + cookieUser.getName() + ", reason: " + e.getMessage(), (Throwable)e);
            this.removePrincipalFromSessionContext(request);
            return AuthenticationState.unauthenticated();
        }
    }

    protected abstract void logoutUser(HttpServletRequest var1);

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Principal getUser(HttpServletRequest request, HttpServletResponse response) {
        ElevatedSecurityGuard securityGuard = this.getElevatedSecurityGuard();
        Principal user = null;
        if (this.isTrustedAppsRequest(request)) {
            return this.getUserFromSession(request);
        }
        AuthenticationState authenticationState = this.checkAuthenticated(request, response);
        if (!authenticationState.isAuthenticated()) return user;
        String cookieToken = this.crowdHttpAuthenticator.getToken(request);
        if (cookieToken == null) {
            logger.error("Could not find cookieToken from authenticated request");
            return null;
        }
        Object sessionToken = request.getSession().getAttribute(SESSION_TOKEN_KEY);
        if (cookieToken.equals(sessionToken)) {
            user = this.getUserFromSession(request);
        }
        if (user == null) {
            try {
                Optional crowdUser = authenticationState.getAuthenticatedPrincipal();
                if (!crowdUser.isPresent()) {
                    crowdUser = Optional.fromNullable((Object)this.crowdHttpAuthenticator.getUser(request));
                }
                Optional<String> updatedUsername = this.triggerUserAuthenticatedNotification((Optional<Principal>)crowdUser);
                user = (Principal)updatedUsername.transform(arg_0 -> ((CrowdAuthenticator)this).getUser(arg_0)).orNull();
            }
            catch (Exception e) {
                logger.info(e.getMessage(), (Throwable)e);
            }
            if (user == null) return user;
            if (!this.authoriseUserAndEstablishSession(request, response, user)) return null;
            LoginReason.OK.stampRequestResponse(request, response);
            securityGuard.onSuccessfulLoginAttempt(request, user.getName());
            request.getSession().setAttribute(SESSION_TOKEN_KEY, (Object)cookieToken);
            return user;
        }
        LoginReason.OK.stampRequestResponse(request, response);
        return user;
    }

    private Optional<String> triggerUserAuthenticatedNotification(Optional<Principal> crowdUser) {
        Optional originalName = crowdUser.transform(Principal::getName);
        if (crowdUser.isPresent() && !DISABLE_USER_AUTHENTICATED_NOTIFICATIONS.booleanValue()) {
            String crowdUserName = ((Principal)crowdUser.get()).getName();
            try {
                return (Optional)this.userAuthenticatedSentCache.get((Object)crowdUserName, () -> {
                    try {
                        logger.debug("User session for {} established via SSO, notifying CrowdService", (Object)crowdUserName);
                        User updatedUser = this.crowdServiceSupplier.get().userAuthenticated(crowdUserName);
                        return Optional.of((Object)updatedUser.getName());
                    }
                    catch (InactiveAccountException e) {
                        logger.warn("User '{}' is inactive during CrowdService.userAuthenticated", (Object)crowdUserName, (Object)e);
                        return Optional.absent();
                    }
                    catch (com.atlassian.crowd.exception.runtime.UserNotFoundException e) {
                        logger.warn("User '{}' not found during CrowdService.userAuthenticated", (Object)crowdUserName, (Object)e);
                        return Optional.absent();
                    }
                    catch (OperationFailedException e) {
                        logger.warn("Error executing CrowdService.userAuthenticated for user '{}', falling back to local user", (Object)crowdUserName, (Object)e);
                        return originalName;
                    }
                });
            }
            catch (UncheckedExecutionException | ExecutionException e) {
                logger.warn("Error executing userAuthenticated for user '{}'", (Object)crowdUserName, (Object)e);
            }
        }
        return originalName;
    }

    private boolean isTrustedAppsRequest(HttpServletRequest request) {
        if ("success".equals(request.getAttribute("os_authstatus"))) {
            if (logger.isDebugEnabled()) {
                logger.debug("User IS authenticated via previous filter/trusted apps");
            }
            return true;
        }
        return false;
    }
}

