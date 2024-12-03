/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.PushedAuthorizationErrorResponse;
import com.nimbusds.oauth2.sdk.PushedAuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public abstract class PushedAuthorizationResponse
implements Response {
    public PushedAuthorizationSuccessResponse toSuccessResponse() {
        return (PushedAuthorizationSuccessResponse)this;
    }

    public PushedAuthorizationErrorResponse toErrorResponse() {
        return (PushedAuthorizationErrorResponse)this;
    }

    public static PushedAuthorizationResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 201 || httpResponse.getStatusCode() == 200) {
            return PushedAuthorizationSuccessResponse.parse(httpResponse);
        }
        return PushedAuthorizationErrorResponse.parse(httpResponse);
    }
}

