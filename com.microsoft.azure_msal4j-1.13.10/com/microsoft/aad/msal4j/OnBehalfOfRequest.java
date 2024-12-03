/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.SignedJWT
 *  com.nimbusds.oauth2.sdk.AuthorizationGrant
 *  com.nimbusds.oauth2.sdk.JWTBearerGrant
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.OAuthAuthorizationGrant;
import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import com.microsoft.aad.msal4j.RequestContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.JWTBearerGrant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class OnBehalfOfRequest
extends MsalRequest {
    OnBehalfOfParameters parameters;

    OnBehalfOfRequest(OnBehalfOfParameters parameters, ConfidentialClientApplication application, RequestContext requestContext) {
        super(application, OnBehalfOfRequest.createAuthenticationGrant(parameters), requestContext);
        this.parameters = parameters;
    }

    private static OAuthAuthorizationGrant createAuthenticationGrant(OnBehalfOfParameters parameters) {
        JWTBearerGrant jWTBearerGrant;
        try {
            jWTBearerGrant = new JWTBearerGrant(SignedJWT.parse((String)parameters.userAssertion().getAssertion()));
        }
        catch (Exception e) {
            throw new MsalClientException(e);
        }
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("requested_token_use", Collections.singletonList("on_behalf_of"));
        if (parameters.claims() != null) {
            params.put("claims", Collections.singletonList(parameters.claims().formatAsJSONString()));
        }
        return new OAuthAuthorizationGrant((AuthorizationGrant)jWTBearerGrant, String.join((CharSequence)" ", parameters.scopes()), params);
    }

    public OnBehalfOfParameters parameters() {
        return this.parameters;
    }
}

