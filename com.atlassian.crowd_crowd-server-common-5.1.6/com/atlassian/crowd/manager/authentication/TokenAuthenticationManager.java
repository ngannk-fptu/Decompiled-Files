/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidTokenException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationAccessDeniedException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext
 *  com.atlassian.crowd.model.authentication.UserAuthenticationContext
 *  com.atlassian.crowd.model.authentication.ValidationFactor
 *  com.atlassian.crowd.model.token.Token
 *  com.atlassian.crowd.model.token.TokenLifetime
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.authentication;

import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidTokenException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.application.ApplicationAccessDeniedException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.authentication.ApplicationAuthenticationContext;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import com.atlassian.crowd.model.token.Token;
import com.atlassian.crowd.model.token.TokenLifetime;
import com.atlassian.crowd.model.user.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public interface TokenAuthenticationManager {
    public Token authenticateApplication(Application var1, ApplicationAuthenticationContext var2, TokenLifetime var3) throws InvalidAuthenticationException;

    public Token authenticateApplicationWithoutValidatingPassword(Application var1, ApplicationAuthenticationContext var2, TokenLifetime var3) throws InvalidAuthenticationException;

    public Token authenticateUser(Application var1, UserAuthenticationContext var2, TokenLifetime var3) throws InvalidAuthenticationException, OperationFailedException, InactiveAccountException, ApplicationAccessDeniedException, ExpiredCredentialException;

    public Token authenticateUserWithoutValidatingPassword(Application var1, UserAuthenticationContext var2) throws InvalidAuthenticationException, OperationFailedException, InactiveAccountException, ApplicationAccessDeniedException;

    public Token validateApplicationToken(String var1, ValidationFactor[] var2) throws InvalidTokenException;

    public Token validateUserToken(Application var1, String var2, ValidationFactor[] var3) throws InvalidTokenException, ApplicationAccessDeniedException, OperationFailedException;

    public Optional<Token> invalidateToken(String var1);

    public void invalidateAllTokens();

    public void removeExpiredTokens();

    public User findUserByToken(Token var1, Application var2) throws InvalidTokenException, OperationFailedException;

    public Token findUserTokenByKey(String var1, Application var2) throws InvalidTokenException, ApplicationAccessDeniedException, OperationFailedException;

    public List<Application> findAuthorisedApplications(User var1, String var2) throws OperationFailedException, DirectoryNotFoundException, ApplicationNotFoundException;

    public void invalidateTokensForUser(String var1, @Nullable String var2, String var3) throws UserNotFoundException, ApplicationNotFoundException;

    public Date getTokenExpiryTime(Token var1);
}

