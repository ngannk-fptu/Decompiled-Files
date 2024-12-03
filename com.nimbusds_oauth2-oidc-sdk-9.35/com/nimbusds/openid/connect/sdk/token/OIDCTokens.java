/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTParser
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.token;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public final class OIDCTokens
extends Tokens {
    private final JWT idToken;
    private final String idTokenString;

    public OIDCTokens(JWT idToken, AccessToken accessToken, RefreshToken refreshToken) {
        super(accessToken, refreshToken);
        if (idToken == null) {
            throw new IllegalArgumentException("The ID token must not be null");
        }
        this.idToken = idToken;
        this.idTokenString = null;
    }

    public OIDCTokens(String idTokenString, AccessToken accessToken, RefreshToken refreshToken) {
        super(accessToken, refreshToken);
        if (idTokenString == null) {
            throw new IllegalArgumentException("The ID token string must not be null");
        }
        this.idTokenString = idTokenString;
        this.idToken = null;
    }

    public OIDCTokens(AccessToken accessToken, RefreshToken refreshToken) {
        super(accessToken, refreshToken);
        this.idToken = null;
        this.idTokenString = null;
    }

    public JWT getIDToken() {
        if (this.idToken != null) {
            return this.idToken;
        }
        if (this.idTokenString != null) {
            try {
                return JWTParser.parse((String)this.idTokenString);
            }
            catch (java.text.ParseException e) {
                return null;
            }
        }
        return null;
    }

    public String getIDTokenString() {
        if (this.idTokenString != null) {
            return this.idTokenString;
        }
        if (this.idToken != null) {
            if (this.idToken.getParsedString() != null) {
                return this.idToken.getParsedString();
            }
            try {
                return this.idToken.serialize();
            }
            catch (IllegalStateException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Set<String> getParameterNames() {
        HashSet<String> paramNames = new HashSet<String>(super.getParameterNames());
        if (this.idToken != null || this.idTokenString != null) {
            paramNames.add("id_token");
        }
        return Collections.unmodifiableSet(paramNames);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.getIDTokenString() != null) {
            o.put((Object)"id_token", (Object)this.getIDTokenString());
        }
        return o;
    }

    public static OIDCTokens parse(JSONObject jsonObject) throws ParseException {
        AccessToken accessToken = AccessToken.parse(jsonObject);
        RefreshToken refreshToken = RefreshToken.parse(jsonObject);
        if (jsonObject.get((Object)"id_token") != null) {
            JWT idToken;
            try {
                idToken = JWTParser.parse((String)JSONObjectUtils.getString(jsonObject, "id_token"));
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Couldn't parse ID token: " + e.getMessage(), e);
            }
            return new OIDCTokens(idToken, accessToken, refreshToken);
        }
        return new OIDCTokens(accessToken, refreshToken);
    }
}

