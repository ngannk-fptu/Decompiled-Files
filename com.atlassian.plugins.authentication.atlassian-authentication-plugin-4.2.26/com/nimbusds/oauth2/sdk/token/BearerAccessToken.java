/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class BearerAccessToken
extends AccessToken {
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
        this(value, 0L, null);
    }

    public BearerAccessToken(String value, long lifetime, Scope scope) {
        super(AccessTokenType.BEARER, value, lifetime, scope);
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
        AccessTokenType tokenType = new AccessTokenType(JSONObjectUtils.getString(jsonObject, "token_type"));
        if (!tokenType.equals(AccessTokenType.BEARER)) {
            throw new ParseException("Token type must be Bearer");
        }
        String accessTokenValue = JSONObjectUtils.getString(jsonObject, "access_token");
        long lifetime = 0L;
        if (jsonObject.containsKey("expires_in")) {
            if (jsonObject.get("expires_in") instanceof Number) {
                lifetime = JSONObjectUtils.getLong(jsonObject, "expires_in");
            } else {
                String lifetimeStr = JSONObjectUtils.getString(jsonObject, "expires_in");
                try {
                    lifetime = Long.parseLong(lifetimeStr);
                }
                catch (NumberFormatException e) {
                    throw new ParseException("Invalid expires_in parameter, must be integer");
                }
            }
        }
        Scope scope = Scope.parse(JSONObjectUtils.getString(jsonObject, "scope", null));
        return new BearerAccessToken(accessTokenValue, lifetime, scope);
    }

    public static BearerAccessToken parse(String header) throws ParseException {
        if (StringUtils.isBlank(header)) {
            throw new ParseException("Missing HTTP Authorization header", BearerTokenError.MISSING_TOKEN);
        }
        String[] parts = header.split("\\s", 2);
        if (parts.length != 2) {
            throw new ParseException("Invalid HTTP Authorization header value", BearerTokenError.INVALID_REQUEST);
        }
        if (!parts[0].equals("Bearer")) {
            throw new ParseException("Token type must be Bearer", BearerTokenError.INVALID_REQUEST);
        }
        try {
            return new BearerAccessToken(parts[1]);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), BearerTokenError.INVALID_REQUEST);
        }
    }

    public static BearerAccessToken parse(Map<String, List<String>> parameters) throws ParseException {
        if (!parameters.containsKey("access_token")) {
            throw new ParseException("Missing access token parameter", BearerTokenError.MISSING_TOKEN);
        }
        String accessTokenValue = MultivaluedMapUtils.getFirstValue(parameters, "access_token");
        if (StringUtils.isBlank(accessTokenValue)) {
            throw new ParseException("Blank / empty access token", BearerTokenError.INVALID_REQUEST);
        }
        return new BearerAccessToken(accessTokenValue);
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

