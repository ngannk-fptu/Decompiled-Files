/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import net.minidev.json.JSONObject;

public class OIDCTokenResponseParser {
    public static TokenResponse parse(JSONObject jsonObject) throws ParseException {
        if (jsonObject.containsKey((Object)"error")) {
            return TokenErrorResponse.parse(jsonObject);
        }
        return OIDCTokenResponse.parse(jsonObject);
    }

    public static TokenResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 200) {
            return OIDCTokenResponse.parse(httpResponse);
        }
        return TokenErrorResponse.parse(httpResponse);
    }

    private OIDCTokenResponseParser() {
    }
}

