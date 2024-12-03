/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidEmailAddressException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.token.ExpirableUserToken
 */
package com.atlassian.crowd.manager.login;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidEmailAddressException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.manager.login.exception.InvalidResetPasswordTokenException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.token.ExpirableUserToken;
import java.time.Duration;
import java.util.Optional;

public interface ForgottenLoginManager {
    public static final Duration DEFAULT_TOKEN_EXPIRY = Duration.ofDays(1L);

    public void sendResetLink(Application var1, String var2, int var3) throws UserNotFoundException, InvalidEmailAddressException, ApplicationPermissionException;

    public boolean sendUsernames(Application var1, String var2) throws InvalidEmailAddressException;

    public void sendResetLink(long var1, String var3, int var4) throws DirectoryNotFoundException, UserNotFoundException, InvalidEmailAddressException, OperationFailedException;

    public boolean isValidResetToken(long var1, String var3, String var4);

    public void resetUserCredential(long var1, String var3, PasswordCredential var4, String var5) throws DirectoryNotFoundException, UserNotFoundException, InvalidResetPasswordTokenException, OperationFailedException, InvalidCredentialException, DirectoryPermissionException;

    public ExpirableUserToken createAndStoreResetToken(long var1, String var3, String var4, int var5);

    public boolean removeByDirectoryAndUsername(long var1, String var3);

    public boolean isUserActive(long var1, String var3);

    @ExperimentalApi
    public Optional<ExpirableUserToken> getToken(long var1, String var3);
}

