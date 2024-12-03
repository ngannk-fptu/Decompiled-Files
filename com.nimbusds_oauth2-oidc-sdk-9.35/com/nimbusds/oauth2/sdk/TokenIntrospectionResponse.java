/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.TokenIntrospectionErrorResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public abstract class TokenIntrospectionResponse
implements Response {
    public TokenIntrospectionSuccessResponse toSuccessResponse() {
        return (TokenIntrospectionSuccessResponse)this;
    }

    public TokenIntrospectionErrorResponse toErrorResponse() {
        return (TokenIntrospectionErrorResponse)this;
    }

    public static TokenIntrospectionResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 200) {
            return TokenIntrospectionSuccessResponse.parse(httpResponse);
        }
        return TokenIntrospectionErrorResponse.parse(httpResponse);
    }
}

