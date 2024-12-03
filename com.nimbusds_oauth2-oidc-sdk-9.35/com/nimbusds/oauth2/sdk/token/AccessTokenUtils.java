/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.nimbusds.oauth2.sdk.token.DPoPTokenError;
import com.nimbusds.oauth2.sdk.token.TokenSchemeError;
import com.nimbusds.oauth2.sdk.token.TokenTypeURI;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;

class AccessTokenUtils {
    static void parseAndEnsureType(JSONObject params, AccessTokenType type) throws ParseException {
        if (!new AccessTokenType(JSONObjectUtils.getString(params, "token_type")).equals(type)) {
            throw new ParseException("Token type must be " + type);
        }
    }

    static String parseValue(JSONObject params) throws ParseException {
        return JSONObjectUtils.getString(params, "access_token");
    }

    static long parseLifetime(JSONObject params) throws ParseException {
        if (params.containsKey((Object)"expires_in")) {
            if (params.get((Object)"expires_in") instanceof Number) {
                return JSONObjectUtils.getLong(params, "expires_in");
            }
            String lifetimeStr = JSONObjectUtils.getString(params, "expires_in");
            try {
                return Long.parseLong(lifetimeStr);
            }
            catch (NumberFormatException e) {
                throw new ParseException("Invalid expires_in parameter, must be integer");
            }
        }
        return 0L;
    }

    static Scope parseScope(JSONObject params) throws ParseException {
        return Scope.parse(JSONObjectUtils.getString(params, "scope", null));
    }

    static TokenTypeURI parseIssuedTokenType(JSONObject params) throws ParseException {
        String issuedTokenTypeString = JSONObjectUtils.getString(params, "issued_token_type", null);
        if (issuedTokenTypeString == null) {
            return null;
        }
        try {
            return TokenTypeURI.parse(issuedTokenTypeString);
        }
        catch (ParseException e) {
            throw new ParseException("Invalid issued_token_type parameter: " + e.getMessage());
        }
    }

    private static void ensureSupported(AccessTokenType type) {
        if (!AccessTokenType.BEARER.equals(type) && !AccessTokenType.DPOP.equals(type)) {
            throw new IllegalArgumentException("Unsupported access token type, must be Bearer or DPoP: " + type);
        }
    }

    static String parseValueFromHeader(String header, AccessTokenType type) throws ParseException {
        AccessTokenUtils.ensureSupported(type);
        if (StringUtils.isBlank(header)) {
            TokenSchemeError schemeError = BearerTokenError.MISSING_TOKEN;
            if (AccessTokenType.DPOP.equals(type)) {
                schemeError = DPoPTokenError.MISSING_TOKEN;
            }
            throw new ParseException("Missing HTTP Authorization header", schemeError);
        }
        String[] parts = header.split("\\s", 2);
        if (parts.length != 2) {
            TokenSchemeError schemeError = BearerTokenError.INVALID_REQUEST;
            if (AccessTokenType.DPOP.equals(type)) {
                schemeError = DPoPTokenError.INVALID_REQUEST;
            }
            throw new ParseException("Invalid HTTP Authorization header value", schemeError);
        }
        if (!parts[0].equals(type.getValue())) {
            TokenSchemeError schemeError = BearerTokenError.INVALID_REQUEST;
            if (AccessTokenType.DPOP.equals(type)) {
                schemeError = DPoPTokenError.INVALID_TOKEN;
            }
            throw new ParseException("Token type must be Bearer", schemeError);
        }
        if (StringUtils.isBlank(parts[1])) {
            TokenSchemeError schemeError = BearerTokenError.INVALID_REQUEST;
            if (AccessTokenType.DPOP.equals(type)) {
                schemeError = DPoPTokenError.INVALID_REQUEST;
            }
            throw new ParseException("The token value must not be null or empty string", schemeError);
        }
        return parts[1];
    }

    static String parseValueFromQueryParameters(Map<String, List<String>> parameters, AccessTokenType type) throws ParseException {
        AccessTokenUtils.ensureSupported(type);
        if (!parameters.containsKey("access_token")) {
            TokenSchemeError schemeError = BearerTokenError.MISSING_TOKEN;
            if (AccessTokenType.DPOP.equals(type)) {
                schemeError = DPoPTokenError.MISSING_TOKEN;
            }
            throw new ParseException("Missing access token parameter", schemeError);
        }
        String accessTokenValue = MultivaluedMapUtils.getFirstValue(parameters, "access_token");
        if (StringUtils.isBlank(accessTokenValue)) {
            TokenSchemeError schemeError = BearerTokenError.INVALID_REQUEST;
            if (AccessTokenType.DPOP.equals(type)) {
                schemeError = DPoPTokenError.INVALID_REQUEST;
            }
            throw new ParseException("Blank / empty access token", schemeError);
        }
        return accessTokenValue;
    }

    static String parseValueFromQueryParameters(Map<String, List<String>> parameters) throws ParseException {
        if (!parameters.containsKey("access_token")) {
            throw new ParseException("Missing access token parameter");
        }
        String accessTokenValue = MultivaluedMapUtils.getFirstValue(parameters, "access_token");
        if (StringUtils.isBlank(accessTokenValue)) {
            throw new ParseException("Blank / empty access token");
        }
        return accessTokenValue;
    }

    static AccessTokenType determineAccessTokenTypeFromAuthorizationHeader(String header) throws ParseException {
        if (StringUtils.isNotBlank(header)) {
            if (header.toLowerCase().startsWith(AccessTokenType.BEARER.getValue().toLowerCase() + " ")) {
                return AccessTokenType.BEARER;
            }
            if (header.toLowerCase().startsWith(AccessTokenType.DPOP.getValue().toLowerCase() + " ")) {
                return AccessTokenType.DPOP;
            }
        }
        throw new ParseException("Couldn't determine access token type from Authorization header");
    }

    private AccessTokenUtils() {
    }
}

