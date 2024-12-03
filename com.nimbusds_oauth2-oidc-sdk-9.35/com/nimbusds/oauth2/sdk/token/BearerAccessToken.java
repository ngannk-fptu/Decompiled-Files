/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.AccessTokenUtils;
import com.nimbusds.oauth2.sdk.token.TokenTypeURI;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class BearerAccessToken
extends AccessToken {
    private static final long serialVersionUID = 2387121016151061194L;

    public BearerAccessToken() {
        this(32);
    }

    public BearerAccessToken(int byteLength) {
        this(byteLength, 0L, null);
    }

    public BearerAccessToken(long lifetime, Scope scope) {
        this(32, lifetime, scope);
    }

    public BearerAccessToken(int byteLength, long lifetime, Scope scope) {
        super(AccessTokenType.BEARER, byteLength, lifetime, scope);
    }

    public BearerAccessToken(String value) {
        this(value, 0L, null, null);
    }

    public BearerAccessToken(String value, long lifetime, Scope scope) {
        this(value, lifetime, scope, null);
    }

    public BearerAccessToken(String value, long lifetime, Scope scope, TokenTypeURI issuedTokenType) {
        super(AccessTokenType.BEARER, value, lifetime, scope, issuedTokenType);
    }

    @Override
    public String toAuthorizationHeader() {
        return "Bearer " + this.getValue();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof BearerAccessToken && this.toString().equals(object.toString());
    }

    public static BearerAccessToken parse(JSONObject jsonObject) throws ParseException {
        AccessTokenUtils.parseAndEnsureType(jsonObject, AccessTokenType.BEARER);
        String accessTokenValue = AccessTokenUtils.parseValue(jsonObject);
        long lifetime = AccessTokenUtils.parseLifetime(jsonObject);
        Scope scope = AccessTokenUtils.parseScope(jsonObject);
        TokenTypeURI issuedTokenType = AccessTokenUtils.parseIssuedTokenType(jsonObject);
        return new BearerAccessToken(accessTokenValue, lifetime, scope, issuedTokenType);
    }

    public static BearerAccessToken parse(String header) throws ParseException {
        return new BearerAccessToken(AccessTokenUtils.parseValueFromHeader(header, AccessTokenType.BEARER));
    }

    public static BearerAccessToken parse(Map<String, List<String>> parameters) throws ParseException {
        return new BearerAccessToken(AccessTokenUtils.parseValueFromQueryParameters(parameters, AccessTokenType.BEARER));
    }

    public static BearerAccessToken parse(HTTPRequest request) throws ParseException {
        String authzHeader = request.getAuthorization();
        if (authzHeader != null) {
            return BearerAccessToken.parse(authzHeader);
        }
        Map<String, List<String>> params = request.getQueryParameters();
        return BearerAccessToken.parse(params);
    }
}

