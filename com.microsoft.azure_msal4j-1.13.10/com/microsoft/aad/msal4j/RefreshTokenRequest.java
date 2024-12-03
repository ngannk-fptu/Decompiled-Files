/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.AuthorizationGrant
 *  com.nimbusds.oauth2.sdk.RefreshTokenGrant
 *  com.nimbusds.oauth2.sdk.token.RefreshToken
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.AbstractMsalAuthorizationGrant;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.OAuthAuthorizationGrant;
import com.microsoft.aad.msal4j.RefreshTokenParameters;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.SilentRequest;
import com.microsoft.aad.msal4j.StringHelper;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import java.util.TreeSet;

class RefreshTokenRequest
extends MsalRequest {
    private SilentRequest parentSilentRequest;
    private RefreshTokenParameters parameters;

    RefreshTokenRequest(RefreshTokenParameters parameters, AbstractClientApplicationBase application, RequestContext requestContext) {
        super(application, RefreshTokenRequest.createAuthenticationGrant(parameters), requestContext);
        this.parameters = parameters;
    }

    RefreshTokenRequest(RefreshTokenParameters parameters, AbstractClientApplicationBase application, RequestContext requestContext, SilentRequest silentRequest) {
        this(parameters, application, requestContext);
        this.parentSilentRequest = silentRequest;
    }

    private static AbstractMsalAuthorizationGrant createAuthenticationGrant(RefreshTokenParameters parameters) {
        RefreshTokenGrant refreshTokenGrant = new RefreshTokenGrant(new RefreshToken(parameters.refreshToken()));
        return new OAuthAuthorizationGrant((AuthorizationGrant)refreshTokenGrant, parameters.scopes(), parameters.claims());
    }

    String getFullThumbprint() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.application().clientId() + ".");
        String authority = this.parentSilentRequest != null && this.parentSilentRequest.requestAuthority() != null ? this.parentSilentRequest.requestAuthority().authority() : this.application().authority();
        sb.append(authority + ".");
        if (this.parentSilentRequest != null && this.parentSilentRequest.parameters().account() != null) {
            sb.append(this.parentSilentRequest.parameters().account().homeAccountId() + ".");
        }
        sb.append(this.parameters.refreshToken() + ".");
        TreeSet<String> sortedScopes = new TreeSet<String>(this.parameters.scopes());
        sb.append(String.join((CharSequence)" ", sortedScopes) + ".");
        return StringHelper.createSha256Hash(sb.toString());
    }
}

