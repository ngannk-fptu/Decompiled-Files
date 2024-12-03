/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.microsoft.aad.msal4j.ClientCredentialFactory
 *  com.microsoft.aad.msal4j.ConfidentialClientApplication
 *  com.microsoft.aad.msal4j.ConfidentialClientApplication$Builder
 *  com.microsoft.aad.msal4j.IClientCredential
 *  com.microsoft.aad.msal4j.IConfidentialClientApplication
 */
package com.atlassian.crowd.directory.authentication.impl;

import com.atlassian.crowd.directory.authentication.MsGraphApiAuthenticator;
import com.atlassian.crowd.directory.authentication.MsGraphApiAuthenticatorFactory;
import com.atlassian.crowd.directory.authentication.impl.MsalAuthenticator;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.IConfidentialClientApplication;
import java.net.MalformedURLException;

public class MsalAuthenticatorFactory
implements MsGraphApiAuthenticatorFactory {
    @Override
    public MsGraphApiAuthenticator create(String clientId, String clientSecret, String tenantId, AzureApiUriResolver apiUriResolver) {
        try {
            ConfidentialClientApplication.Builder builder = (ConfidentialClientApplication.Builder)ConfidentialClientApplication.builder((String)clientId, (IClientCredential)ClientCredentialFactory.createFromSecret((String)clientSecret)).authority(apiUriResolver.getAuthorityApiUrl(tenantId));
            ConfidentialClientApplication confidentialClientApplication = builder.build();
            return new MsalAuthenticator((IConfidentialClientApplication)confidentialClientApplication, apiUriResolver);
        }
        catch (MalformedURLException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }
}

