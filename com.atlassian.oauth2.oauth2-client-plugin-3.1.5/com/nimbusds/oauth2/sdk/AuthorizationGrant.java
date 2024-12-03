/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.JWTBearerGrant;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.SAML2BearerGrant;
import com.nimbusds.oauth2.sdk.ciba.CIBAGrant;
import com.nimbusds.oauth2.sdk.device.DeviceCodeGrant;
import com.nimbusds.oauth2.sdk.tokenexchange.TokenExchangeGrant;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.List;
import java.util.Map;

public abstract class AuthorizationGrant {
    private final GrantType type;

    protected AuthorizationGrant(GrantType type) {
        if (type == null) {
            throw new IllegalArgumentException("The grant type must not be null");
        }
        this.type = type;
    }

    public GrantType getType() {
        return this.type;
    }

    public abstract Map<String, List<String>> toParameters();

    public static AuthorizationGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType grantType;
        String grantTypeString = MultivaluedMapUtils.getFirstValue(params, "grant_type");
        if (grantTypeString == null) {
            String msg = "Missing grant_type parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        try {
            grantType = GrantType.parse(grantTypeString);
        }
        catch (ParseException e) {
            String msg = "Invalid grant type: " + e.getMessage();
            throw new ParseException(msg, OAuth2Error.UNSUPPORTED_GRANT_TYPE.appendDescription(": " + msg));
        }
        if (grantType.equals(GrantType.AUTHORIZATION_CODE)) {
            return AuthorizationCodeGrant.parse(params);
        }
        if (grantType.equals(GrantType.REFRESH_TOKEN)) {
            return RefreshTokenGrant.parse(params);
        }
        if (grantType.equals(GrantType.PASSWORD)) {
            return ResourceOwnerPasswordCredentialsGrant.parse(params);
        }
        if (grantType.equals(GrantType.CLIENT_CREDENTIALS)) {
            return ClientCredentialsGrant.parse(params);
        }
        if (grantType.equals(GrantType.JWT_BEARER)) {
            return JWTBearerGrant.parse(params);
        }
        if (grantType.equals(GrantType.SAML2_BEARER)) {
            return SAML2BearerGrant.parse(params);
        }
        if (grantType.equals(GrantType.DEVICE_CODE)) {
            return DeviceCodeGrant.parse(params);
        }
        if (grantType.equals(GrantType.CIBA)) {
            return CIBAGrant.parse(params);
        }
        if (grantType.equals(GrantType.TOKEN_EXCHANGE)) {
            return TokenExchangeGrant.parse(params);
        }
        throw new ParseException("Invalid or unsupported grant type: " + grantType, OAuth2Error.UNSUPPORTED_GRANT_TYPE);
    }
}

