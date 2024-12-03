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
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
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
import com.atlassian.crowd.integration.AuthenticationState;
import com.atlassian.crowd.model.user.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CrowdHttpAuthenticator {
    @Nullable
    public User getUser(HttpServletRequest var1) throws InvalidTokenException, ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    @Nonnull
    public User authenticate(HttpServletRequest var1, HttpServletResponse var2, String var3, String var4) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, ApplicationAccessDeniedException, ExpiredCredentialException, InactiveAccountException, InvalidTokenException;

    @Nonnull
    public User authenticateWithoutValidatingPassword(HttpServletRequest var1, HttpServletResponse var2, String var3) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException, ApplicationAccessDeniedException, InactiveAccountException, InvalidTokenException;

    @Deprecated
    public boolean isAuthenticated(HttpServletRequest var1, HttpServletResponse var2) throws OperationFailedException;

    @Nonnull
    public AuthenticationState checkAuthenticated(HttpServletRequest var1, HttpServletResponse var2) throws OperationFailedException;

    public void logout(HttpServletRequest var1, HttpServletResponse var2) throws ApplicationPermissionException, InvalidAuthenticationException, OperationFailedException;

    @Nullable
    public String getToken(HttpServletRequest var1);
}

