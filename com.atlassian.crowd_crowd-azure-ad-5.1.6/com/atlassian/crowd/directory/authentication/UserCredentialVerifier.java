/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.OperationFailedException
 */
package com.atlassian.crowd.directory.authentication;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;

public interface UserCredentialVerifier {
    public void checkUserCredential(String var1, PasswordCredential var2) throws InvalidAuthenticationException, OperationFailedException;
}

