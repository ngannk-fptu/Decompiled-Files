/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JWSAlgorithm
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.claims.AccessTokenHash;
import com.nimbusds.openid.connect.sdk.validators.InvalidHashException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class AccessTokenValidator {
    public static void validate(AccessToken accessToken, JWSAlgorithm jwsAlgorithm, AccessTokenHash accessTokenHash) throws InvalidHashException {
        AccessTokenHash expectedHash = AccessTokenHash.compute(accessToken, jwsAlgorithm);
        if (expectedHash == null) {
            throw InvalidHashException.INVALID_ACCESS_T0KEN_HASH_EXCEPTION;
        }
        if (!expectedHash.equals(accessTokenHash)) {
            throw InvalidHashException.INVALID_ACCESS_T0KEN_HASH_EXCEPTION;
        }
    }
}

