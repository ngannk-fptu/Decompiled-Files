/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.microsoft.aad.msal4j.MsalInteractionRequiredException
 *  com.microsoft.aad.msal4j.PublicClientApplication
 *  com.microsoft.aad.msal4j.UserNamePasswordParameters
 */
package com.atlassian.crowd.directory.authentication.impl;

import com.atlassian.crowd.directory.authentication.UserCredentialVerifier;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.microsoft.aad.msal4j.MsalInteractionRequiredException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MsalUserCredentialVerifier
implements UserCredentialVerifier {
    private final PublicClientApplication publicClientApplication;
    private final AzureApiUriResolver endpointDataProvider;

    public MsalUserCredentialVerifier(PublicClientApplication publicClientApplication, AzureApiUriResolver endpointDataProvider) {
        this.publicClientApplication = publicClientApplication;
        this.endpointDataProvider = endpointDataProvider;
    }

    @Override
    public void checkUserCredential(String username, PasswordCredential userCredential) throws InvalidAuthenticationException, OperationFailedException {
        try {
            UserNamePasswordParameters parameters = UserNamePasswordParameters.builder(Collections.singleton(this.endpointDataProvider.getScopeUrl()), (String)username, (char[])userCredential.getCredential().toCharArray()).build();
            CompletableFuture future = this.publicClientApplication.acquireToken(parameters);
            future.get();
        }
        catch (InterruptedException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (ExecutionException e) {
            if (e.getCause() instanceof MsalInteractionRequiredException) {
                throw new InvalidAuthenticationException("Could not authenticate user " + username, (Throwable)e);
            }
            throw new OperationFailedException((Throwable)e);
        }
    }
}

