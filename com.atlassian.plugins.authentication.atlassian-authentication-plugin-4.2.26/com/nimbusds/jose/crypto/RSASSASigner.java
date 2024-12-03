/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.impl.RSAKeyUtils;
import com.nimbusds.jose.crypto.impl.RSASSA;
import com.nimbusds.jose.crypto.impl.RSASSAProvider;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RSASSASigner
extends RSASSAProvider
implements JWSSigner {
    private final PrivateKey privateKey;

    public RSASSASigner(PrivateKey privateKey) {
        this(privateKey, false);
    }

    public RSASSASigner(PrivateKey privateKey, boolean allowWeakKey) {
        int keyBitLength;
        if (!"RSA".equalsIgnoreCase(privateKey.getAlgorithm())) {
            throw new IllegalArgumentException("The private key algorithm must be RSA");
        }
        if (!allowWeakKey && (keyBitLength = RSAKeyUtils.keyBitLength(privateKey)) > 0 && keyBitLength < 2048) {
            throw new IllegalArgumentException("The RSA key size must be at least 2048 bits");
        }
        this.privateKey = privateKey;
    }

    public RSASSASigner(RSAKey rsaJWK) throws JOSEException {
        this(rsaJWK, false);
    }

    public RSASSASigner(RSAKey rsaJWK, boolean allowWeakKey) throws JOSEException {
        this(RSAKeyUtils.toRSAPrivateKey(rsaJWK), allowWeakKey);
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public Base64URL sign(JWSHeader header, byte[] signingInput) throws JOSEException {
        Signature signer = RSASSA.getSignerAndVerifier(header.getAlgorithm(), this.getJCAContext().getProvider());
        try {
            signer.initSign(this.privateKey);
            signer.update(signingInput);
            return Base64URL.encode(signer.sign());
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("Invalid private RSA key: " + e.getMessage(), e);
        }
        catch (SignatureException e) {
            throw new JOSEException("RSA signature exception: " + e.getMessage(), e);
        }
    }
}

