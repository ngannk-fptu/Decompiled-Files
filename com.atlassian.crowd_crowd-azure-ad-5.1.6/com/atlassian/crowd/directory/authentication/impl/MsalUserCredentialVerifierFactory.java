/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.microsoft.aad.msal4j.PublicClientApplication
 *  com.microsoft.aad.msal4j.PublicClientApplication$Builder
 */
package com.atlassian.crowd.directory.authentication.impl;

import com.atlassian.crowd.directory.authentication.UserCredentialVerifierFactory;
import com.atlassian.crowd.directory.authentication.impl.MsalUserCredentialVerifier;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import java.net.MalformedURLException;

public class MsalUserCredentialVerifierFactory
implements UserCredentialVerifierFactory {
    @Override
    public MsalUserCredentialVerifier create(AzureApiUriResolver endpointDataProvider, String nativeClientId, String tenantId) {
        try {
            PublicClientApplication app = ((PublicClientApplication.Builder)PublicClientApplication.builder((String)nativeClientId).authority(endpointDataProvider.getAuthorityApiUrl(tenantId))).build();
            return new MsalUserCredentialVerifier(app, endpointDataProvider);
        }
        catch (MalformedURLException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }
}

