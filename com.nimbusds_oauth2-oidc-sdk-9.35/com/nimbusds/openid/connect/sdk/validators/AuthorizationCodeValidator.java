/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JWSAlgorithm
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.claims.CodeHash;
import com.nimbusds.openid.connect.sdk.validators.InvalidHashException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class AuthorizationCodeValidator {
    public static void validate(AuthorizationCode code, JWSAlgorithm jwsAlgorithm, CodeHash codeHash) throws InvalidHashException {
        CodeHash expectedHash = CodeHash.compute(code, jwsAlgorithm);
        if (expectedHash == null) {
            throw InvalidHashException.INVALID_CODE_HASH_EXCEPTION;
        }
        if (!expectedHash.equals(codeHash)) {
            throw InvalidHashException.INVALID_CODE_HASH_EXCEPTION;
        }
    }
}

