/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.openid.connect.sdk.claims.HashClaim;
import net.jcip.annotations.Immutable;

@Immutable
public final class CodeHash
extends HashClaim {
    public static boolean isRequiredInIDTokenClaims(ResponseType responseType) {
        return new ResponseType("code", "id_token").equals(responseType) || new ResponseType("code", "id_token", "token").equals(responseType);
    }

    public CodeHash(String value) {
        super(value);
    }

    public static CodeHash compute(AuthorizationCode code, JWSAlgorithm alg) {
        String value = CodeHash.computeValue(code, alg);
        if (value == null) {
            return null;
        }
        return new CodeHash(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof CodeHash && this.toString().equals(object.toString());
    }
}

