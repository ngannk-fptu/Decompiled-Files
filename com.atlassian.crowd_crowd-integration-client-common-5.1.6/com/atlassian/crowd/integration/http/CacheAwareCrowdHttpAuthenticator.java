/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationAccessDeniedException
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidTokenException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.user.User
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.crowd.integration.http;

import com.atlassian.crowd.exception.ApplicationAccessDeniedException;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidTokenException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.integration.AuthenticationState;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.AuthenticatorUserCache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheAwareCrowdHttpAuthenticator
implements CrowdHttpAuthenticator {
    private final CrowdHttpAuthenticator delegate;
    private final AuthenticatorUserCache userCache;

    public CacheAwareCrowdHttpAuthenticator(CrowdHttpAuthenticator delegate, AuthenticatorUserCache userCache) {
        this.delegate = delegate;
        this.userCache = userCache;
    }

    @Override
    public User getUser(HttpServletRequest request) throws InvalidTokenException, InvalidAuthenticationException, ApplicationPermissionException, OperationFailedException {
        User user = this.delegate.getUser(request);
        this.ensureUserExistsInCache(user.getName());
        return user;
    }

    @Override
    public User authenticate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws InvalidTokenException, ApplicationAccessDeniedException, InvalidAuthenticationException, ExpiredCredentialException, ApplicationPermissionException, InactiveAccountException, OperationFailedException {
        User user = this.delegate.authenticate(request, response, username, password);
        this.ensureUserExistsInCache(user.getName());
        return user;
    }

    @Override
    public User authenticateWithoutValidatingPassword(HttpServletRequest request, HttpServletResponse response, String username) throws InvalidAuthenticationException, OperationFailedException, InvalidTokenException, ApplicationAccessDeniedException, ApplicationPermissionException, InactiveAccountException {
        User user = this.delegate.authenticateWithoutValidatingPassword(request, response, username);
        this.ensureUserExistsInCache(user.getName());
        return user;
    }

    private void ensureUserExistsInCache(String name) throws InvalidAuthenticationException, OperationFailedException {
        try {
            this.userCache.fetchInCache(name);
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Deprecated
    public boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) throws OperationFailedException {
        return this.delegate.isAuthenticated(request, response);
    }

    @Override
    public AuthenticationState checkAuthenticated(HttpServletRequest request, HttpServletResponse response) throws OperationFailedException {
        return this.delegate.checkAuthenticated(request, response);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws InvalidAuthenticationException, ApplicationPermissionException, OperationFailedException {
        this.delegate.logout(request, response);
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return this.delegate.getToken(request);
    }
}

