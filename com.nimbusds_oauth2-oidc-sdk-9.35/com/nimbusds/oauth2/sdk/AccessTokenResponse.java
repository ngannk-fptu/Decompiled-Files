/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.Tokens;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class AccessTokenResponse
extends TokenResponse
implements SuccessResponse {
    private final Tokens tokens;
    private final Map<String, Object> customParams;

    public AccessTokenResponse(Tokens tokens) {
        this(tokens, null);
    }

    public AccessTokenResponse(Tokens tokens, Map<String, Object> customParams) {
        if (tokens == null) {
            throw new IllegalArgumentException("The tokens must not be null");
        }
        this.tokens = tokens;
        this.customParams = customParams;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public Tokens getTokens() {
        return this.tokens;
    }

    public Map<String, Object> getCustomParameters() {
        if (this.customParams == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.customParams);
    }

    @Deprecated
    public Map<String, Object> getCustomParams() {
        return this.getCustomParameters();
    }

    public JSONObject toJSONObject() {
        JSONObject o = this.tokens.toJSONObject();
        if (this.customParams != null) {
            o.putAll(this.customParams);
        }
        return o;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setCacheControl("no-store");
        httpResponse.setPragma("no-cache");
        httpResponse.setContent(this.toJSONObject().toString());
        return httpResponse;
    }

    public static AccessTokenResponse parse(JSONObject jsonObject) throws ParseException {
        Tokens tokens = Tokens.parse(jsonObject);
        HashSet customParamNames = new HashSet(jsonObject.keySet());
        customParamNames.removeAll(tokens.getParameterNames());
        HashMap<String, Object> customParams = null;
        if (!customParamNames.isEmpty()) {
            customParams = new HashMap<String, Object>();
            for (String name : customParamNames) {
                customParams.put(name, jsonObject.get((Object)name));
            }
        }
        return new AccessTokenResponse(tokens, customParams);
    }

    public static AccessTokenResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200);
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        return AccessTokenResponse.parse(jsonObject);
    }
}

