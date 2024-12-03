/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.event.events.user.SendUserInviteEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.notifications.NotificationSendResult;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.exception.runtime.UserNotFoundException;
import com.atlassian.user.User;

public interface SignupManager {
    public String getSignUpToken();

    public boolean canSignUpWith(String var1);

    public String refreshAndGetToken();

    public boolean isEmailSentOnInviteSignUp();

    public void setEmailSentOnInviteSignUp(boolean var1);

    public String restorePreviousToken();

    public String getSignupURL();

    public String getRelativeSignupURL();

    public boolean isPublicSignupPermitted();

    public NotificationSendResult sendInvites(SendUserInviteEvent var1);

    public void sendConfirmationEmail(String var1, User var2);

    public void sendWelcomeEmail(ConfluenceUser var1);

    public void setPublicSignupMode();

    public void setPrivateSignupMode();

    public void setDomainRestrictedSignupMode(String var1);

    public String getRestrictedDomains();

    public boolean isEmailOnRestrictedDomain(String var1);

    public boolean isPendingConfirmation(User var1);

    public String createUserPendingConfirmation(User var1, String var2) throws OperationFailedException, InvalidUserException, InvalidCredentialException, OperationNotPermittedException;

    public void enableConfirmedUser(User var1) throws UserNotFoundException, OperationFailedException, InvalidUserException, OperationNotPermittedException;

    public boolean isTokenForUserValid(User var1, String var2);

    public boolean doesUserHaveOutdatedSignupToken(User var1);

    public boolean isDomainRestrictedSignupEnabled();
}

