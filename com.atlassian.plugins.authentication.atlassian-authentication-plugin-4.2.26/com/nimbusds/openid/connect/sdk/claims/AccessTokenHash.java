/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.claims.HashClaim;
import net.jcip.annotations.Immutable;

@Immutable
public final class AccessTokenHash
extends HashClaim {
    public static boolean isRequiredInIDTokenClaims(ResponseType responseType) {
        return new ResponseType("token", "id_token").equals(responseType) || new ResponseType("code", "id_token", "token").equals(responseType);
    }

    public AccessTokenHash(String value) {
        super(value);
    }

    public static AccessTokenHash compute(AccessToken accessToken, JWSAlgorithm alg) {
        String value = AccessTokenHash.computeValue(accessToken, alg);
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

