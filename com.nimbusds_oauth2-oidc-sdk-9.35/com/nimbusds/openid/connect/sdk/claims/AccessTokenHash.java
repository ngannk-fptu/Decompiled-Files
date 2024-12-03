/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.jwk.Curve
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.claims.HashClaim;
import net.jcip.annotations.Immutable;

@Immutable
public final class AccessTokenHash
extends HashClaim {
    private static final long serialVersionUID = -2260085393906006318L;

    public static boolean isRequiredInIDTokenClaims(ResponseType responseType) {
        return ResponseType.IDTOKEN_TOKEN.equals(responseType) || ResponseType.CODE_IDTOKEN_TOKEN.equals(responseType);
    }

    public AccessTokenHash(String value) {
        super(value);
    }

    @Deprecated
    public static AccessTokenHash compute(AccessToken accessToken, JWSAlgorithm alg) {
        String value = AccessTokenHash.computeValue(accessToken, alg);
        if (value == null) {
            return null;
        }
        return new AccessTokenHash(value);
    }

    public static AccessTokenHash compute(AccessToken accessToken, JWSAlgorithm alg, Curve crv) {
        String value = AccessTokenHash.computeValue(accessToken, alg, crv);
        if (value == null) {
            return null;
        }
        return new AccessTokenHash(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AccessTokenHash && this.toString().equals(object.toString());
    }
}

