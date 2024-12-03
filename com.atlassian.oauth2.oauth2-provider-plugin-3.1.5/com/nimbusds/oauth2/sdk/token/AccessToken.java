/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.AccessTokenUtils;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.nimbusds.oauth2.sdk.token.NAAccessToken;
import com.nimbusds.oauth2.sdk.token.Token;
import com.nimbusds.oauth2.sdk.token.TokenTypeURI;
import com.nimbusds.oauth2.sdk.token.TypelessAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;

public abstract class AccessToken
extends Token {
    private static final long serialVersionUID = 2947643641344083799L;
    private final AccessTokenType type;
    private final long lifetime;
    private final Scope scope;
    private final TokenTypeURI issuedTokenType;

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
        this(type, byteLength, lifetime, scope, null);
    }

    public AccessToken(AccessTokenType type, int byteLength, long lifetime, Scope scope, TokenTypeURI issuedTokenType) {
        super(byteLength);
        if (type == null) {
            throw new IllegalArgumentException("The access token type must not be null");
        }
        this.type = type;
        this.lifetime = lifetime;
        this.scope = scope;
        this.issuedTokenType = issuedTokenType;
    }

    public AccessToken(AccessTokenType type, String value) {
        this(type, value, 0L, null);
    }

    public AccessToken(AccessTokenType type, String value, long lifetime, Scope scope) {
        this(type, value, lifetime, scope, null);
    }

    public AccessToken(AccessTokenType type, String value, long lifetime, Scope scope, TokenTypeURI issuedTokenType) {
        super(value);
        if (type == null) {
            throw new IllegalArgumentException("The access token type must not be null");
        }
        this.type = type;
        this.lifetime = lifetime;
        this.scope = scope;
        this.issuedTokenType = issuedTokenType;
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

    public TokenTypeURI getIssuedTokenType() {
        return this.issuedTokenType;
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
        if (this.getIssuedTokenType() != null) {
            paramNames.add("issued_token_type");
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
        if (this.getIssuedTokenType() != null) {
            o.put("issued_token_type", this.getIssuedTokenType().getURI().toString());
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toString();
    }

    public abstract String toAuthorizationHeader();

    public static AccessToken parse(JSONObject jsonObject) throws ParseException {
        AccessTokenType tokenType = new AccessTokenType(JSONObjectUtils.getString(jsonObject, "token_type"));
        if (AccessTokenType.BEARER.equals(tokenType)) {
            return BearerAccessToken.parse(jsonObject);
        }
        if (AccessTokenType.DPOP.equals(tokenType)) {
            return DPoPAccessToken.parse(jsonObject);
        }
        if (AccessTokenType.N_A.equals(tokenType)) {
            return NAAccessToken.parse(jsonObject);
        }
        throw new ParseException("Unsupported token_type: " + tokenType);
    }

    @Deprecated
    public static AccessToken parse(String header) throws ParseException {
        return BearerAccessToken.parse(header);
    }

    public static AccessToken parse(String header, AccessTokenType preferredType) throws ParseException {
        if (!AccessTokenType.BEARER.equals(preferredType) && !AccessTokenType.DPOP.equals(preferredType)) {
            throw new IllegalArgumentException("Unsupported Authorization scheme: " + preferredType);
        }
        if (header != null && header.startsWith(AccessTokenType.BEARER.getValue()) || AccessTokenType.BEARER.equals(preferredType)) {
            return BearerAccessToken.parse(header);
        }
        return DPoPAccessToken.parse(header);
    }

    public static AccessToken parse(HTTPRequest request) throws ParseException {
        if (request.getAuthorization() != null) {
            AccessTokenType tokenType = AccessTokenUtils.determineAccessTokenTypeFromAuthorizationHeader(request.getAuthorization());
            if (AccessTokenType.BEARER.equals(tokenType)) {
                return BearerAccessToken.parse(request.getAuthorization());
            }
            if (AccessTokenType.DPOP.equals(tokenType)) {
                return DPoPAccessToken.parse(request.getAuthorization());
            }
            throw new ParseException("Couldn't determine access token type from Authorization header");
        }
        Map<String, List<String>> params = request.getQueryParameters();
        return new TypelessAccessToken(AccessTokenUtils.parseValueFromQueryParameters(params));
    }
}

