/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.RSASSAProvider;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

public class RSASSA {
    public static Signature getSignerAndVerifier(JWSAlgorithm alg, Provider provider) throws JOSEException {
        Signature signature;
        String jcaAlg;
        PSSParameterSpec pssSpec = null;
        if (alg.equals(JWSAlgorithm.RS256)) {
            jcaAlg = "SHA256withRSA";
        } else if (alg.equals(JWSAlgorithm.RS384)) {
            jcaAlg = "SHA384withRSA";
        } else if (alg.equals(JWSAlgorithm.RS512)) {
            jcaAlg = "SHA512withRSA";
        } else if (alg.equals(JWSAlgorithm.PS256)) {
            jcaAlg = "RSASSA-PSS";
            pssSpec = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);
        } else if (alg.equals(JWSAlgorithm.PS384)) {
            jcaAlg = "RSASSA-PSS";
            pssSpec = new PSSParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, 48, 1);
        } else if (alg.equals(JWSAlgorithm.PS512)) {
            jcaAlg = "RSASSA-PSS";
            pssSpec = new PSSParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1);
        } else {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, RSASSAProvider.SUPPORTED_ALGORITHMS));
        }
        try {
            signature = provider != null ? Signature.getInstance(jcaAlg, provider) : Signature.getInstance(jcaAlg);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Unsupported RSASSA algorithm: " + e.getMessage(), e);
        }
        if (pssSpec != null) {
            try {
                signature.setParameter(pssSpec);
            }
            catch (InvalidAlgorithmParameterException e) {
                throw new JOSEException("Invalid RSASSA-PSS salt length parameter: " + e.getMessage(), e);
            }
        }
        return signature;
    }

    private RSASSA() {
    }
}

