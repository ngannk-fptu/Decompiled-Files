/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.openid.connect.sdk.claims.HashClaim;
import net.jcip.annotations.Immutable;

@Immutable
public final class CodeHash
extends HashClaim {
    private static final long serialVersionUID = 4627813971222806593L;

    public static boolean isRequiredInIDTokenClaims(ResponseType responseType) {
        return ResponseType.CODE_IDTOKEN.equals(responseType) || ResponseType.CODE_IDTOKEN_TOKEN.equals(responseType);
    }

    public CodeHash(String value) {
        super(value);
    }

    @Deprecated
    public static CodeHash compute(AuthorizationCode code, JWSAlgorithm alg) {
        String value = CodeHash.computeValue(code, alg);
        if (value == null) {
            return null;
        }
        return new CodeHash(value);
    }

    public static CodeHash compute(AuthorizationCode code, JWSAlgorithm alg, Curve crv) {
        String value = CodeHash.computeValue(code, alg, crv);
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

