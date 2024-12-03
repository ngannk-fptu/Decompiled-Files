/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.AuthorizationCode
 *  com.nimbusds.oauth2.sdk.AuthorizationCodeGrant
 *  com.nimbusds.oauth2.sdk.AuthorizationGrant
 *  com.nimbusds.oauth2.sdk.pkce.CodeVerifier
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.AbstractMsalAuthorizationGrant;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.OAuthAuthorizationGrant;
import com.microsoft.aad.msal4j.RequestContext;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;

class AuthorizationCodeRequest
extends MsalRequest {
    AuthorizationCodeRequest(AuthorizationCodeParameters parameters, AbstractClientApplicationBase application, RequestContext requestContext) {
        super(application, AuthorizationCodeRequest.createMsalGrant(parameters), requestContext);
    }

    private static AbstractMsalAuthorizationGrant createMsalGrant(AuthorizationCodeParameters parameters) {
        AuthorizationCodeGrant authorizationGrant = parameters.codeVerifier() != null ? new AuthorizationCodeGrant(new AuthorizationCode(parameters.authorizationCode()), parameters.redirectUri(), new CodeVerifier(parameters.codeVerifier())) : new AuthorizationCodeGrant(new AuthorizationCode(parameters.authorizationCode()), parameters.redirectUri());
        return new OAuthAuthorizationGrant((AuthorizationGrant)authorizationGrant, parameters.scopes(), parameters.claims());
    }
}

