/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.PasswordBasedCryptoProvider;
import java.security.Provider;
import net.jcip.annotations.Immutable;

@Immutable
public final class PRFParams {
    private final String jcaMacAlg;
    private final Provider macProvider;
    private final int dkLen;

    public PRFParams(String jcaMacAlg, Provider macProvider, int dkLen) {
        this.jcaMacAlg = jcaMacAlg;
        this.macProvider = macProvider;
        this.dkLen = dkLen;
    }

    public String getMACAlgorithm() {
        return this.jcaMacAlg;
    }

    public Provider getMacProvider() {
        return this.macProvider;
    }

    public int getDerivedKeyByteLength() {
        return this.dkLen;
    }

    public static PRFParams resolve(JWEAlgorithm alg, Provider macProvider) throws JOSEException {
        int dkLen;
        String jcaMagAlg;
        if (JWEAlgorithm.PBES2_HS256_A128KW.equals(alg)) {
            jcaMagAlg = "HmacSHA256";
            dkLen = 16;
        } else if (JWEAlgorithm.PBES2_HS384_A192KW.equals(alg)) {
            jcaMagAlg = "HmacSHA384";
            dkLen = 24;
        } else if (JWEAlgorithm.PBES2_HS512_A256KW.equals(alg)) {
            jcaMagAlg = "HmacSHA512";
            dkLen = 32;
        } else {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, PasswordBasedCryptoProvider.SUPPORTED_ALGORITHMS));
        }
        return new PRFParams(jcaMagAlg, macProvider, dkLen);
    }
}

