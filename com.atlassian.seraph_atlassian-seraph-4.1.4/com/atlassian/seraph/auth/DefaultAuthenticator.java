/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.auth;

import com.atlassian.seraph.auth.AbstractAuthenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.auth.LoginReason;
import com.atlassian.seraph.auth.RoleMapper;
import com.atlassian.seraph.auth.SessionInvalidator;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard;
import com.atlassian.seraph.interceptor.LogoutInterceptor;
import com.atlassian.seraph.service.rememberme.RememberMeService;
import com.atlassian.seraph.util.RedirectUtils;
import com.atlassian.seraph.util.SecurityUtils;
import java.io.IOException;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultAuthenticator
extends AbstractAuthenticator {
    @Deprecated
    public static final String LOGGED_IN_KEY = "seraph_defaultauthenticator_user";
    public static final String LOGGED_IN_USER_ID_KEY = "seraph_defaultauthenticator_user_id";
    public static final String LOGGED_OUT_KEY = "seraph_defaultauthenticator_logged_out_user";
    private static final Logger log = LoggerFactory.getLogger(DefaultAuthenticator.class);
    private String basicAuthParameterName;

    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
        super.init(params, config);
        this.basicAuthParameterName = config.getAuthType();
    }

    @Override
    public boolean login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, final String userName, String password, boolean setRememberMeCookie) throws AuthenticatorException {
        String METHOD = "login : ";
        boolean dbg = log.isDebugEnabled();
        Principal principal = new Principal(){

            @Override
            public String getName() {
                return userName;
            }
        };
        boolean authenticated = this.authenticate(principal, password);
        if (dbg) {
            log.debug("login : '" + userName + "' has " + (authenticated ? "been" : "not been") + " authenticated");
        }
        if (authenticated) {
            Principal user = this.getUser(userName);
            if (this.authoriseUserAndEstablishSession(httpServletRequest, httpServletResponse, user)) {
                if (setRememberMeCookie && httpServletResponse != null) {
                    this.getRememberMeService().addRememberMeCookie(httpServletRequest, httpServletResponse, userName);
                }
                return true;
            }
            LoginReason.AUTHORISATION_FAILED.stampRequestResponse(httpServletRequest, httpServletResponse);
        } else {
            log.info("login : '" + userName + "' could not be authenticated with the given password");
        }
        if (httpServletResponse != null) {
            log.warn("login : '" + userName + "' tried to login but they do not have USE permission or weren't found. Deleting remember me cookie.");
            this.getRememberMeService().removeRememberMeCookie(httpServletRequest, httpServletResponse);
        }
        return false;
    }

    @Override
    public boolean logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticatorException {
        String METHOD = "logout : ";
        boolean dbg = log.isDebugEnabled();
        if (dbg) {
            log.debug("logout : Calling interceptors and clearing remember me cookie");
        }
        List<LogoutInterceptor> interceptors = this.getLogoutInterceptors();
        for (LogoutInterceptor interceptor : interceptors) {
            interceptor.beforeLogout(httpServletRequest, httpServletResponse);
        }
        this.removePrincipalFromSessionContext(httpServletRequest);
        LoginReason.OUT.stampRequestResponse(httpServletRequest, httpServletResponse);
        if (httpServletResponse != null) {
            this.getRememberMeService().removeRememberMeCookie(httpServletRequest, httpServletResponse);
        }
        Iterator<LogoutInterceptor> iterator = interceptors.iterator();
        while (iterator.hasNext()) {
            LogoutInterceptor element;
            LogoutInterceptor interceptor = element = iterator.next();
            interceptor.afterLogout(httpServletRequest, httpServletResponse);
        }
        return true;
    }

    protected boolean authoriseUserAndEstablishSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Principal principal) {
        boolean principalAlreadyInSessionContext = this.isPrincipalAlreadyInSessionContext(httpServletRequest, principal);
        this.putPrincipalInSessionContext(httpServletRequest, null);
        boolean canLogin = this.isAuthorised(httpServletRequest, principal);
        if (log.isDebugEnabled()) {
            String prefix = "authoriseUser : '" + principal.getName() + "' ";
            log.debug(prefix + (canLogin ? "can" : "CANNOT") + " login according to the RoleMapper");
        }
        if (canLogin) {
            SecurityConfig theConfig;
            if (!principalAlreadyInSessionContext && (theConfig = this.getConfig()) != null && theConfig.isInvalidateSessionOnLogin()) {
                this.invalidateSession(httpServletRequest);
            }
            this.putPrincipalInSessionContext(httpServletRequest, principal);
            return true;
        }
        return false;
    }

    protected boolean isAuthorised(HttpServletRequest httpServletRequest, Principal principal) {
        return this.getRoleMapper().canLogin(principal, httpServletRequest);
    }

    protected void putPrincipalInSessionContext(HttpServletRequest httpServletRequest, Principal principal) {
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute(LOGGED_IN_KEY, (Object)principal);
        httpSession.setAttribute(LOGGED_IN_USER_ID_KEY, (Object)(principal != null ? principal.getName() : null));
        httpSession.setAttribute(LOGGED_OUT_KEY, null);
    }

    protected void removePrincipalFromSessionContext(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute(LOGGED_IN_KEY, null);
        httpSession.setAttribute(LOGGED_IN_USER_ID_KEY, null);
        httpSession.setAttribute(LOGGED_OUT_KEY, (Object)Boolean.TRUE);
    }

    protected boolean isPrincipalAlreadyInSessionContext(HttpServletRequest httpServletRequest, Principal principal) {
        if (principal == null) {
            return false;
        }
        String currentUsername = this.getUsernameFromSession(httpServletRequest);
        return StringUtils.equals((CharSequence)currentUsername, (CharSequence)principal.getName());
    }

    protected RoleMapper getRoleMapper() {
        return SecurityConfigFactory.getInstance().getRoleMapper();
    }

    protected abstract Principal getUser(String var1);

    protected abstract boolean authenticate(Principal var1, String var2) throws AuthenticatorException;

    @Override
    public Principal getUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Principal basicAuthUser;
        Principal cookieUser;
        Principal sessionUser;
        String METHOD = "getUser : ";
        boolean dbg = log.isDebugEnabled();
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null && (sessionUser = this.getUserFromSession(httpServletRequest)) != null) {
            LoginReason.OK.stampRequestResponse(httpServletRequest, httpServletResponse);
            return sessionUser;
        }
        if (!LoginReason.OUT.isStamped(httpServletRequest) && (cookieUser = this.getUserFromCookie(httpServletRequest, httpServletResponse)) != null) {
            return cookieUser;
        }
        if (RedirectUtils.isBasicAuthentication(httpServletRequest, this.basicAuthParameterName) && (basicAuthUser = this.getUserFromBasicAuthentication(httpServletRequest, httpServletResponse)) != null) {
            return basicAuthUser;
        }
        if (dbg) {
            log.debug("getUser : User not found in either Session, Cookie or Basic Auth.");
        }
        return null;
    }

    protected Principal refreshPrincipalObtainedFromSession(HttpServletRequest httpServletRequest, Principal principal) {
        Principal freshPrincipal = principal;
        if (principal != null && principal.getName() != null) {
            freshPrincipal = this.getUser(principal.getName());
            this.putPrincipalInSessionContext(httpServletRequest, freshPrincipal);
        }
        return freshPrincipal;
    }

    protected Principal getPrincipalFromSession(HttpServletRequest httpServletRequest, String username) {
        if (StringUtils.isNotBlank((CharSequence)username)) {
            Principal principalObject = this.getUser(username);
            this.putPrincipalInSessionContext(httpServletRequest, principalObject);
            return principalObject;
        }
        return null;
    }

    protected Principal getUserFromSession(HttpServletRequest httpServletRequest) {
        String METHOD = "getUserFromSession : ";
        boolean dbg = log.isDebugEnabled();
        try {
            if (httpServletRequest.getSession().getAttribute(LOGGED_OUT_KEY) != null) {
                if (dbg) {
                    log.debug("getUserFromSession : Session found; user has already logged out. eg has LOGGED_OUT_KEY in session");
                }
                return null;
            }
            String username = this.getUsernameFromSession(httpServletRequest);
            if (dbg) {
                if (StringUtils.isBlank((CharSequence)username)) {
                    log.debug("getUserFromSession : Session found; BUT it has no Principal in it");
                } else {
                    log.debug("getUserFromSession : Session found; '" + username + "' is present");
                }
            }
            return this.getPrincipalFromSession(httpServletRequest, username);
        }
        catch (Exception e) {
            log.warn("getUserFromSession : Exception when retrieving user from session: " + e, (Throwable)e);
            return null;
        }
    }

    private String getUsernameFromSession(HttpServletRequest httpServletRequest) {
        Principal principal = (Principal)httpServletRequest.getSession().getAttribute(LOGGED_IN_KEY);
        String username = (String)httpServletRequest.getSession().getAttribute(LOGGED_IN_USER_ID_KEY);
        return (String)ObjectUtils.firstNonNull((Object[])new String[]{username, principal != null ? principal.getName() : null});
    }

    protected Principal getUserFromCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Principal principal;
        String METHOD = "getUserFromCookie : ";
        boolean dbg = log.isDebugEnabled();
        String userName = this.getRememberMeService().getRememberMeCookieAuthenticatedUsername(httpServletRequest, httpServletResponse);
        if (dbg) {
            log.debug("getUserFromCookie : Got username : '" + userName + "' from cookie, attempting to authenticate user is known");
        }
        if (StringUtils.isNotBlank((CharSequence)userName) && (principal = this.getUser(userName)) != null) {
            ElevatedSecurityGuard securityGuard = this.getElevatedSecurityGuard();
            if (!securityGuard.performElevatedSecurityCheck(httpServletRequest, userName)) {
                if (dbg) {
                    log.debug("getUserFromCookie : '" + userName + "' failed elevated security check");
                }
                LoginReason.AUTHENTICATION_DENIED.stampRequestResponse(httpServletRequest, httpServletResponse);
                securityGuard.onFailedLoginAttempt(httpServletRequest, userName);
                return null;
            }
            if (this.authoriseUserAndEstablishSession(httpServletRequest, httpServletResponse, principal)) {
                if (dbg) {
                    log.debug("getUserFromCookie : Authenticated '" + userName + "' via Remember Me Cookie");
                }
                LoginReason.OK.stampRequestResponse(httpServletRequest, httpServletResponse);
                securityGuard.onSuccessfulLoginAttempt(httpServletRequest, userName);
                return principal;
            }
            if (dbg) {
                log.debug("getUserFromCookie : '" + userName + "' failed authorisation security check");
            }
            LoginReason.AUTHORISATION_FAILED.stampRequestResponse(httpServletRequest, httpServletResponse);
            securityGuard.onFailedLoginAttempt(httpServletRequest, userName);
        }
        return null;
    }

    protected Principal getUserFromBasicAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String METHOD = "getUserFromSession : ";
        boolean dbg = log.isDebugEnabled();
        String header = httpServletRequest.getHeader("Authorization");
        LoginReason reason = LoginReason.OK;
        if (SecurityUtils.isBasicAuthorizationHeader(header)) {
            if (dbg) {
                log.debug("getUserFromSession : Looking in Basic Auth headers");
            }
            SecurityUtils.UserPassCredentials creds = SecurityUtils.decodeBasicAuthorizationCredentials(header);
            ElevatedSecurityGuard securityGuard = this.getElevatedSecurityGuard();
            if (!securityGuard.performElevatedSecurityCheck(httpServletRequest, creds.getUsername())) {
                if (dbg) {
                    log.debug("getUserFromSession : '" + creds.getUsername() + "' failed elevated security check");
                }
                reason = LoginReason.AUTHENTICATION_DENIED.stampRequestResponse(httpServletRequest, httpServletResponse);
                securityGuard.onFailedLoginAttempt(httpServletRequest, creds.getUsername());
            } else {
                if (dbg) {
                    log.debug("getUserFromSession : '" + creds.getUsername() + "' does not require elevated security check.  Attempting authentication...");
                }
                try {
                    boolean loggedin = this.login(httpServletRequest, httpServletResponse, creds.getUsername(), creds.getPassword(), false);
                    if (loggedin) {
                        reason = LoginReason.OK.stampRequestResponse(httpServletRequest, httpServletResponse);
                        securityGuard.onSuccessfulLoginAttempt(httpServletRequest, creds.getUsername());
                        if (dbg) {
                            log.debug("getUserFromSession : Authenticated '" + creds.getUsername() + "' via Basic Auth");
                        }
                        return this.getUser(creds.getUsername());
                    }
                    reason = LoginReason.AUTHENTICATED_FAILED.stampRequestResponse(httpServletRequest, httpServletResponse);
                    securityGuard.onFailedLoginAttempt(httpServletRequest, creds.getUsername());
                }
                catch (AuthenticatorException e) {
                    log.warn("getUserFromSession : Exception trying to login '" + creds.getUsername() + "' via Basic Auth:" + e, (Throwable)e);
                }
            }
            if (httpServletResponse != null) {
                try {
                    httpServletResponse.sendError(401, "Basic Authentication Failure - Reason : " + reason.toString());
                }
                catch (IOException e) {
                    log.warn("getUserFromSession : Exception trying to send Basic Auth failed error: " + e, (Throwable)e);
                }
            }
            return null;
        }
        if (httpServletResponse != null) {
            httpServletResponse.setStatus(401);
            httpServletResponse.setHeader("WWW-Authenticate", "Basic realm=\"protected-area\"");
        }
        return null;
    }

    public String getAuthType() {
        return this.basicAuthParameterName;
    }

    protected List<LogoutInterceptor> getLogoutInterceptors() {
        return this.getConfig().getInterceptors(LogoutInterceptor.class);
    }

    protected ElevatedSecurityGuard getElevatedSecurityGuard() {
        return this.getConfig().getElevatedSecurityGuard();
    }

    protected RememberMeService getRememberMeService() {
        return this.getConfig().getRememberMeService();
    }

    private void invalidateSession(HttpServletRequest httpServletRequest) {
        SessionInvalidator si = new SessionInvalidator(this.getConfig().getInvalidateSessionExcludeList());
        si.invalidateSession(httpServletRequest);
    }
}

