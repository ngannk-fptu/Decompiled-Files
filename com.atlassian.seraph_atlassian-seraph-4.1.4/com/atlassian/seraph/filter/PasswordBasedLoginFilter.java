/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.filter;

import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.auth.LoginReason;
import com.atlassian.seraph.elevatedsecurity.ElevatedSecurityGuard;
import com.atlassian.seraph.filter.BaseLoginFilter;
import com.atlassian.seraph.interceptor.LoginInterceptor;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PasswordBasedLoginFilter
extends BaseLoginFilter {
    private static final Logger log = LoggerFactory.getLogger(PasswordBasedLoginFilter.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String METHOD = "login : ";
        boolean dbg = log.isDebugEnabled();
        String loginStatus = LOGIN_NOATTEMPT;
        UserPasswordPair userPair = this.extractUserPasswordPair(request);
        if (userPair == null || userPair.userName == null || userPair.password == null) {
            if (dbg) {
                log.debug("login : No user name or password was returned. No authentication attempt will be made.  User may still be found via a SecurityFilter later.");
            }
            return loginStatus;
        }
        if (dbg) {
            log.debug("login : '" + userPair.userName + "' and password provided - remember me : " + userPair.persistentLogin + " - attempting login request");
        }
        List<LoginInterceptor> interceptors = this.getSecurityConfig().getInterceptors(LoginInterceptor.class);
        this.runBeforeLoginInterceptors(request, response, userPair, interceptors);
        try {
            loginStatus = this.runAuthentication(request, response, userPair);
        }
        catch (AuthenticatorException ex) {
            if (dbg) {
                log.debug("login : An exception occurred authenticating : '" + userPair.userName + "'", (Throwable)ex);
            }
            loginStatus = "error";
            request.setAttribute("auth_error_type", (Object)ex.getErrorType());
        }
        finally {
            this.runAfterLoginInterceptors(request, response, loginStatus, userPair, interceptors);
        }
        return loginStatus;
    }

    private String runAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, UserPasswordPair userPair) throws AuthenticatorException {
        String METHOD = "runAuthentication : ";
        boolean dbg = log.isDebugEnabled();
        ElevatedSecurityGuard securityGuard = this.getElevatedSecurityGuard();
        if (!securityGuard.performElevatedSecurityCheck(httpServletRequest, userPair.userName)) {
            if (dbg) {
                log.debug("runAuthentication : '" + userPair.userName + "' failed elevated security check");
            }
            LoginReason.AUTHENTICATION_DENIED.stampRequestResponse(httpServletRequest, httpServletResponse);
            securityGuard.onFailedLoginAttempt(httpServletRequest, userPair.userName);
            return "failed";
        }
        if (dbg) {
            log.debug("runAuthentication : '" + userPair.userName + "' does not require elevated security check.  Attempting authentication...");
        }
        boolean loggedIn = this.getAuthenticator().login(httpServletRequest, httpServletResponse, userPair.userName, userPair.password, userPair.persistentLogin);
        if (dbg) {
            log.debug("runAuthentication : '" + userPair.userName + "' was " + (loggedIn ? "successfully" : "UNSUCCESSFULLY") + " authenticated");
        }
        if (loggedIn) {
            LoginReason.OK.stampRequestResponse(httpServletRequest, httpServletResponse);
            securityGuard.onSuccessfulLoginAttempt(httpServletRequest, userPair.userName);
        } else {
            LoginReason.AUTHENTICATED_FAILED.stampRequestResponse(httpServletRequest, httpServletResponse);
            securityGuard.onFailedLoginAttempt(httpServletRequest, userPair.userName);
        }
        return loggedIn ? "success" : "failed";
    }

    private void runBeforeLoginInterceptors(HttpServletRequest request, HttpServletResponse response, UserPasswordPair userPair, List<LoginInterceptor> interceptors) {
        for (LoginInterceptor loginInterceptor : interceptors) {
            loginInterceptor.beforeLogin(request, response, userPair.userName, userPair.password, userPair.persistentLogin);
        }
    }

    private void runAfterLoginInterceptors(HttpServletRequest request, HttpServletResponse response, String status, UserPasswordPair userPair, List<LoginInterceptor> interceptors) {
        for (LoginInterceptor loginInterceptor : interceptors) {
            loginInterceptor.afterLogin(request, response, userPair.userName, userPair.password, userPair.persistentLogin, status);
        }
    }

    protected abstract UserPasswordPair extractUserPasswordPair(HttpServletRequest var1);

    public static final class UserPasswordPair {
        final String userName;
        final String password;
        final boolean persistentLogin;

        public UserPasswordPair(String user, String password, boolean persistentLogin) {
            this.userName = user;
            this.password = password;
            this.persistentLogin = persistentLogin;
        }
    }
}

