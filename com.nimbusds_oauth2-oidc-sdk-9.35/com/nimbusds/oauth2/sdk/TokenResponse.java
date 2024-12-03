/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import net.minidev.json.JSONObject;

public abstract class TokenResponse
implements Response {
    public AccessTokenResponse toSuccessResponse() {
        return (AccessTokenResponse)this;
    }

    public TokenErrorResponse toErrorResponse() {
        return (TokenErrorResponse)this;
    }

    public static TokenResponse parse(JSONObject jsonObject) throws ParseException {
        if (jsonObject.containsKey((Object)"access_token")) {
            return AccessTokenResponse.parse(jsonObject);
        }
        return TokenErrorResponse.parse(jsonObject);
    }

    public static TokenResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 200) {
            return AccessTokenResponse.parse(httpResponse);
        }
        return TokenErrorResponse.parse(httpResponse);
    }
}

