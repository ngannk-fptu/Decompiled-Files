/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;

public class Tokens {
    private final AccessToken accessToken;
    private final RefreshToken refreshToken;
    private final Map<String, Object> metadata = new HashMap<String, Object>();

    public Tokens(AccessToken accessToken, RefreshToken refreshToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("The access token must not be null");
        }
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    public BearerAccessToken getBearerAccessToken() {
        if (this.accessToken instanceof BearerAccessToken) {
            return (BearerAccessToken)this.accessToken;
        }
        if (AccessTokenType.BEARER.equals(this.accessToken.getType())) {
            return new BearerAccessToken(this.accessToken.getValue(), this.accessToken.getLifetime(), this.accessToken.getScope(), this.accessToken.getIssuedTokenType());
        }
        return null;
    }

    public DPoPAccessToken getDPoPAccessToken() {
        if (this.accessToken instanceof DPoPAccessToken) {
            return (DPoPAccessToken)this.accessToken;
        }
        if (AccessTokenType.DPOP.equals(this.accessToken.getType())) {
            return new DPoPAccessToken(this.accessToken.getValue(), this.accessToken.getLifetime(), this.accessToken.getScope(), this.accessToken.getIssuedTokenType());
        }
        return null;
    }

    public RefreshToken getRefreshToken() {
        return this.refreshToken;
    }

    public Set<String> getParameterNames() {
        Set<String> paramNames = this.accessToken.getParameterNames();
        if (this.refreshToken != null) {
            paramNames.addAll(this.refreshToken.getParameterNames());
        }
        return Collections.unmodifiableSet(paramNames);
    }

    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public JSONObject toJSONObject() {
        JSONObject o = this.accessToken.toJSONObject();
        if (this.refreshToken != null) {
            o.putAll((Map)this.refreshToken.toJSONObject());
        }
        return o;
    }

    public OIDCTokens toOIDCTokens() {
        return (OIDCTokens)this;
    }

    public String toString() {
        return this.toJSONObject().toJSONString();
    }

    public static Tokens parse(JSONObject jsonObject) throws ParseException {
        return new Tokens(AccessToken.parse(jsonObject), RefreshToken.parse(jsonObject));
    }
}

