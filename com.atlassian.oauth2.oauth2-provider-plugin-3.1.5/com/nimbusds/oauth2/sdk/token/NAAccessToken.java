/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.AccessTokenUtils;
import com.nimbusds.oauth2.sdk.token.TokenTypeURI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class NAAccessToken
extends AccessToken {
    private static final long serialVersionUID = 268047904352224888L;

    public NAAccessToken(String value, long lifetime, Scope scope, TokenTypeURI issuedTokenType) {
        super(AccessTokenType.N_A, value, lifetime, scope, issuedTokenType);
    }

    @Override
    public String toAuthorizationHeader() {
        throw new UnsupportedOperationException();
    }

    public static NAAccessToken parse(JSONObject jsonObject) throws ParseException {
        AccessTokenUtils.parseAndEnsureType(jsonObject, AccessTokenType.N_A);
        String accessTokenValue = AccessTokenUtils.parseValue(jsonObject);
        long lifetime = AccessTokenUtils.parseLifetime(jsonObject);
        Scope scope = AccessTokenUtils.parseScope(jsonObject);
        TokenTypeURI issuedTokenType = AccessTokenUtils.parseIssuedTokenType(jsonObject);
        return new NAAccessToken(accessTokenValue, lifetime, scope, issuedTokenType);
    }
}

