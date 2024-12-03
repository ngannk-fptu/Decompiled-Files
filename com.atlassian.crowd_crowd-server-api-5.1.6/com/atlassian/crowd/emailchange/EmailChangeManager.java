/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidEmailAddressException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 */
package com.atlassian.crowd.emailchange;

import com.atlassian.crowd.emailchange.InvalidChangeEmailTokenException;
import com.atlassian.crowd.emailchange.SameEmailAddressException;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidEmailAddressException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.mail.MailSendException;

public interface EmailChangeManager {
    public void sendEmailAuthorization(String var1, String var2, long var3, String var5) throws InvalidAuthenticationException, InvalidEmailAddressException, MailSendException, OperationNotPermittedException, ApplicationNotFoundException, SameEmailAddressException;

    public void changeEmail(String var1, String var2) throws InvalidChangeEmailTokenException, InvalidAuthenticationException, UserNotFoundException, OperationFailedException, ApplicationNotFoundException, OperationNotPermittedException;

    public boolean hasUserEmailChangePending(String var1, long var2);

    public String getPendingNewEmailByUser(String var1, long var2);

    public String getPendingNewEmailByToken(String var1);

    public void resendEmail(String var1, long var2) throws InvalidEmailAddressException, MailSendException, UserNotFoundException, ApplicationNotFoundException;

    public void abort(String var1, long var2);

    public boolean isTokenValid(String var1);

    public boolean isAvailableForDirectory(long var1);
}

