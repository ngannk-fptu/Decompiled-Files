/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.Token;
import java.util.HashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public abstract class AccessToken
extends Token {
    private final AccessTokenType type;
    private final long lifetime;
    private final Scope scope;

    public AccessToken(AccessTokenType type) {
        this(type, 32);
    }

    public AccessToken(AccessTokenType type, int byteLength) {
        this(type, byteLength, 0L, null);
    }

    public AccessToken(AccessTokenType type, long lifetime, Scope scope) {
        this(type, 32, lifetime, scope);
    }

    public AccessToken(AccessTokenType type, int byteLength, long lifetime, Scope scope) {
        super(byteLength);
        if (type == null) {
            throw new IllegalArgumentException("The access token type must not be null");
        }
        this.type = type;
        this.lifetime = lifetime;
        this.scope = scope;
    }

    public AccessToken(AccessTokenType type, String value) {
        this(type, value, 0L, null);
    }

    public AccessToken(AccessTokenType type, String value, long lifetime, Scope scope) {
        super(value);
        if (type == null) {
            throw new IllegalArgumentException("The access token type must not be null");
        }
        this.type = type;
        this.lifetime = lifetime;
        this.scope = scope;
    }

    public AccessTokenType getType() {
        return this.type;
    }

    public long getLifetime() {
        return this.lifetime;
    }

    public Scope getScope() {
        return this.scope;
    }

    @Override
    public Set<String> getParameterNames() {
        HashSet<String> paramNames = new HashSet<String>();
        paramNames.add("access_token");
        paramNames.add("token_type");
        if (this.getLifetime() > 0L) {
            paramNames.add("expires_in");
        }
        if (this.getScope() != null) {
            paramNames.add("scope");
        }
        return paramNames;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("access_token", this.getValue());
        o.put("token_type", this.type.toString());
        if (this.getLifetime() > 0L) {
            o.put("expires_in", this.lifetime);
        }
        if (this.getScope() != null) {
            o.put("scope", this.scope.toString());
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toString();
    }

    public abstract String toAuthorizationHeader();

    public static AccessToken parse(JSONObject jsonObject) throws ParseException {
        return BearerAccessToken.parse(jsonObject);
    }

    public static AccessToken parse(String header) throws ParseException {
        return BearerAccessToken.parse(header);
    }
}

