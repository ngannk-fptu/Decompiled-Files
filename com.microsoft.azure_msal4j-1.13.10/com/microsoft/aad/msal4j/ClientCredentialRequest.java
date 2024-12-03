/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.AuthorizationGrant
 *  com.nimbusds.oauth2.sdk.ClientCredentialsGrant
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AppTokenProviderParameters;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.OAuthAuthorizationGrant;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.TokenProviderResult;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class ClientCredentialRequest
extends MsalRequest {
    ClientCredentialParameters parameters;
    Function<AppTokenProviderParameters, CompletableFuture<TokenProviderResult>> appTokenProvider;

    ClientCredentialRequest(ClientCredentialParameters parameters, ConfidentialClientApplication application, RequestContext requestContext) {
        super(application, ClientCredentialRequest.createMsalGrant(parameters), requestContext);
        this.parameters = parameters;
        this.appTokenProvider = null;
    }

    ClientCredentialRequest(ClientCredentialParameters parameters, ConfidentialClientApplication application, RequestContext requestContext, Function<AppTokenProviderParameters, CompletableFuture<TokenProviderResult>> appTokenProvider) {
        super(application, ClientCredentialRequest.createMsalGrant(parameters), requestContext);
        this.parameters = parameters;
        this.appTokenProvider = appTokenProvider;
    }

    private static OAuthAuthorizationGrant createMsalGrant(ClientCredentialParameters parameters) {
        return new OAuthAuthorizationGrant((AuthorizationGrant)new ClientCredentialsGrant(), parameters.scopes(), parameters.claims());
    }
}

