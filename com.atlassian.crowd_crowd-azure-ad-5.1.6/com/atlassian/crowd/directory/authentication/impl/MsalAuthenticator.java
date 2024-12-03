/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.microsoft.aad.msal4j.ClientCredentialParameters
 *  com.microsoft.aad.msal4j.IAuthenticationResult
 *  com.microsoft.aad.msal4j.IConfidentialClientApplication
 */
package com.atlassian.crowd.directory.authentication.impl;

import com.atlassian.crowd.directory.authentication.MsGraphApiAuthenticator;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.exception.OperationFailedException;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IConfidentialClientApplication;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MsalAuthenticator
implements MsGraphApiAuthenticator {
    private final IConfidentialClientApplication confidentialClientApplication;
    private final AzureApiUriResolver apiUriResolver;

    public MsalAuthenticator(IConfidentialClientApplication confidentialClientApplication, AzureApiUriResolver apiUriResolver) {
        this.confidentialClientApplication = confidentialClientApplication;
        this.apiUriResolver = apiUriResolver;
    }

    @Override
    public IAuthenticationResult getApiToken() throws OperationFailedException {
        try {
            ClientCredentialParameters parameters = ClientCredentialParameters.builder(Collections.singleton(this.apiUriResolver.getScopeUrl())).build();
            CompletableFuture future = this.confidentialClientApplication.acquireToken(parameters);
            IAuthenticationResult authResult = (IAuthenticationResult)future.get();
            return authResult;
        }
        catch (InterruptedException | ExecutionException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }
}

