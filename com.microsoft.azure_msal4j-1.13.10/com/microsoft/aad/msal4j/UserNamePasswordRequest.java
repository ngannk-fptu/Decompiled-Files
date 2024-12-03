/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.AuthorizationGrant
 *  com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant
 *  com.nimbusds.oauth2.sdk.auth.Secret
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.OAuthAuthorizationGrant;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.auth.Secret;

class UserNamePasswordRequest
extends MsalRequest {
    UserNamePasswordRequest(UserNamePasswordParameters parameters, PublicClientApplication application, RequestContext requestContext) {
        super(application, UserNamePasswordRequest.createAuthenticationGrant(parameters), requestContext);
    }

    private static OAuthAuthorizationGrant createAuthenticationGrant(UserNamePasswordParameters parameters) {
        ResourceOwnerPasswordCredentialsGrant resourceOwnerPasswordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(parameters.username(), new Secret(new String(parameters.password())));
        return new OAuthAuthorizationGrant((AuthorizationGrant)resourceOwnerPasswordCredentialsGrant, parameters.scopes(), parameters.claims());
    }
}

