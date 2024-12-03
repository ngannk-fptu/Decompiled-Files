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
public class DPoPAccessToken
extends AccessToken {
    private static final long serialVersionUID = 7745184045632691024L;

    public DPoPAccessToken(String value) {
        this(value, 0L, null);
    }

    public DPoPAccessToken(String value, long lifetime, Scope scope) {
        this(value, lifetime, scope, null);
    }

    public DPoPAccessToken(String value, long lifetime, Scope scope, TokenTypeURI issuedTokenType) {
        super(AccessTokenType.DPOP, value, lifetime, scope, issuedTokenType);
    }

    @Override
    public String toAuthorizationHeader() {
        return "DPoP " + this.getValue();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof DPoPAccessToken && this.toString().equals(object.toString());
    }

    public static DPoPAccessToken parse(JSONObject jsonObject) throws ParseException {
        AccessTokenUtils.parseAndEnsureType(jsonObject, AccessTokenType.DPOP);
        String accessTokenValue = AccessTokenUtils.parseValue(jsonObject);
        long lifetime = AccessTokenUtils.parseLifetime(jsonObject);
        Scope scope = AccessTokenUtils.parseScope(jsonObject);
        TokenTypeURI issuedTokenType = AccessTokenUtils.parseIssuedTokenType(jsonObject);
        return new DPoPAccessToken(accessTokenValue, lifetime, scope, issuedTokenType);
    }

    public static DPoPAccessToken parse(String header) throws ParseException {
        return new DPoPAccessToken(AccessTokenUtils.parseValueFromHeader(header, AccessTokenType.DPOP));
    }

    public static DPoPAccessToken parse(Map<String, List<String>> parameters) throws ParseException {
        return new DPoPAccessToken(AccessTokenUtils.parseValueFromQueryParameters(parameters, AccessTokenType.DPOP));
    }

    public static DPoPAccessToken parse(HTTPRequest request) throws ParseException {
        String authzHeader = request.getAuthorization();
        if (authzHeader != null) {
            return DPoPAccessToken.parse(authzHeader);
        }
        Map<String, List<String>> params = request.getQueryParameters();
        return DPoPAccessToken.parse(params);
    }
}

