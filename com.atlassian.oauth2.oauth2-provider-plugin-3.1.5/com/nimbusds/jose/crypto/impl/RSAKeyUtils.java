/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

public class RSAKeyUtils {
    public static PrivateKey toRSAPrivateKey(RSAKey rsaJWK) throws JOSEException {
        if (!rsaJWK.isPrivate()) {
            throw new JOSEException("The RSA JWK doesn't contain a private part");
        }
        return rsaJWK.toPrivateKey();
    }

    public static int keyBitLength(PrivateKey privateKey) {
        if (!(privateKey instanceof RSAPrivateKey)) {
            return -1;
        }
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)privateKey;
        try {
            return rsaPrivateKey.getModulus().bitLength();
        }
        catch (Exception e) {
            return -1;
        }
    }
}

