/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.microsoft.aad.msal4j.IAuthenticationResult
 */
package com.atlassian.crowd.directory.authentication;

import com.atlassian.crowd.exception.OperationFailedException;
import com.microsoft.aad.msal4j.IAuthenticationResult;

public interface MsGraphApiAuthenticator {
    public IAuthenticationResult getApiToken() throws OperationFailedException;
}

