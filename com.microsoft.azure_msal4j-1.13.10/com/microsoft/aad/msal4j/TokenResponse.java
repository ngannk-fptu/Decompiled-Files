/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.ParseException
 *  com.nimbusds.oauth2.sdk.http.HTTPResponse
 *  com.nimbusds.oauth2.sdk.token.AccessToken
 *  com.nimbusds.oauth2.sdk.token.RefreshToken
 *  com.nimbusds.oauth2.sdk.util.JSONObjectUtils
 *  com.nimbusds.openid.connect.sdk.OIDCTokenResponse
 *  com.nimbusds.openid.connect.sdk.token.OIDCTokens
 *  net.minidev.json.JSONObject
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.MsalClientException;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import net.minidev.json.JSONObject;

class TokenResponse
extends OIDCTokenResponse {
    private String scope;
    private String clientInfo;
    private long expiresIn;
    private long extExpiresIn;
    private String foci;
    private long refreshIn;

    TokenResponse(AccessToken accessToken, RefreshToken refreshToken, String idToken, String scope, String clientInfo, long expiresIn, long extExpiresIn, String foci, long refreshIn) {
        super(new OIDCTokens(idToken, accessToken, refreshToken));
        this.scope = scope;
        this.clientInfo = clientInfo;
        this.expiresIn = expiresIn;
        this.extExpiresIn = extExpiresIn;
        this.refreshIn = refreshIn;
        this.foci = foci;
    }

    static TokenResponse parseHttpResponse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(new int[]{200});
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        return TokenResponse.parseJsonObject(jsonObject);
    }

    static Long getLongValue(JSONObject jsonObject, String key) throws ParseException {
        Object value = jsonObject.get((Object)key);
        if (value instanceof Long) {
            return JSONObjectUtils.getLong((JSONObject)jsonObject, (String)key);
        }
        return Long.parseLong(JSONObjectUtils.getString((JSONObject)jsonObject, (String)key));
    }

    static TokenResponse parseJsonObject(JSONObject jsonObject) throws ParseException {
        String idTokenValue = "";
        if (jsonObject.containsKey((Object)"id_token")) {
            idTokenValue = JSONObjectUtils.getString((JSONObject)jsonObject, (String)"id_token");
        }
        String scopeValue = null;
        if (jsonObject.containsKey((Object)"scope")) {
            scopeValue = JSONObjectUtils.getString((JSONObject)jsonObject, (String)"scope");
        }
        String clientInfo = null;
        if (jsonObject.containsKey((Object)"client_info")) {
            clientInfo = JSONObjectUtils.getString((JSONObject)jsonObject, (String)"client_info");
        }
        long expiresIn = 0L;
        if (jsonObject.containsKey((Object)"expires_in")) {
            expiresIn = TokenResponse.getLongValue(jsonObject, "expires_in");
        }
        long ext_expires_in = 0L;
        if (jsonObject.containsKey((Object)"ext_expires_in")) {
            ext_expires_in = TokenResponse.getLongValue(jsonObject, "ext_expires_in");
        }
        String foci = null;
        if (jsonObject.containsKey((Object)"foci")) {
            foci = JSONObjectUtils.getString((JSONObject)jsonObject, (String)"foci");
        }
        long refreshIn = 0L;
        if (jsonObject.containsKey((Object)"refresh_in")) {
            refreshIn = TokenResponse.getLongValue(jsonObject, "refresh_in");
        }
        try {
            AccessToken accessToken = AccessToken.parse((JSONObject)jsonObject);
            RefreshToken refreshToken = RefreshToken.parse((JSONObject)jsonObject);
            return new TokenResponse(accessToken, refreshToken, idTokenValue, scopeValue, clientInfo, expiresIn, ext_expires_in, foci, refreshIn);
        }
        catch (ParseException e) {
            throw new MsalClientException("Invalid or missing token, could not parse. If using B2C, information on a potential B2C issue and workaround can be found here: https://aka.ms/msal4j-b2c-known-issues", "invalid_json");
        }
        catch (Exception e) {
            throw new MsalClientException(e);
        }
    }

    String getScope() {
        return this.scope;
    }

    String getClientInfo() {
        return this.clientInfo;
    }

    long getExpiresIn() {
        return this.expiresIn;
    }

    long getExtExpiresIn() {
        return this.extExpiresIn;
    }

    String getFoci() {
        return this.foci;
    }

    long getRefreshIn() {
        return this.refreshIn;
    }
}

