/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class OIDCTokenResponse
extends AccessTokenResponse {
    private final OIDCTokens tokens;

    public OIDCTokenResponse(OIDCTokens tokens) {
        this(tokens, null);
    }

    public OIDCTokenResponse(OIDCTokens tokens, Map<String, Object> customParams) {
        super(tokens, customParams);
        this.tokens = tokens;
    }

    public OIDCTokens getOIDCTokens() {
        return this.tokens;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        o.putAll((Map)this.getOIDCTokens().toJSONObject());
        return o;
    }

    @Override
    public OIDCTokenResponse toSuccessResponse() {
        return this;
    }

    public static OIDCTokenResponse parse(JSONObject jsonObject) throws ParseException {
        OIDCTokens tokens = OIDCTokens.parse(jsonObject);
        HashMap<String, Object> customParams = new HashMap<String, Object>((Map<String, Object>)jsonObject);
        for (String tokenParam : tokens.getParameterNames()) {
            customParams.remove(tokenParam);
        }
        if (customParams.isEmpty()) {
            return new OIDCTokenResponse(tokens);
        }
        return new OIDCTokenResponse(tokens, customParams);
    }

    public static OIDCTokenResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200);
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        return OIDCTokenResponse.parse(jsonObject);
    }
}

