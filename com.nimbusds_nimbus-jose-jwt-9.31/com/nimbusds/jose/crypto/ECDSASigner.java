/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.nimbusds.jose.crypto.impl.ECDSAProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.Base64URL;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ECDSASigner
extends ECDSAProvider
implements JWSSigner {
    private final PrivateKey privateKey;

    public ECDSASigner(ECPrivateKey privateKey) throws JOSEException {
        super(ECDSA.resolveAlgorithm(privateKey));
        this.privateKey = privateKey;
    }

    public ECDSASigner(PrivateKey privateKey, Curve curve) throws JOSEException {
        super(ECDSA.resolveAlgorithm(curve));
        if (!"EC".equalsIgnoreCase(privateKey.getAlgorithm())) {
            throw new IllegalArgumentException("The private key algorithm must be EC");
        }
        this.privateKey = privateKey;
    }

    public ECDSASigner(ECKey ecJWK) throws JOSEException {
        super(ECDSA.resolveAlgorithm(ecJWK.getCurve()));
        if (!ecJWK.isPrivate()) {
            throw new JOSEException("The EC JWK doesn't contain a private part");
        }
        this.privateKey = ecJWK.toPrivateKey();
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public Base64URL sign(JWSHeader header, byte[] signingInput) throws JOSEException {
        byte[] jcaSignature;
        JWSAlgorithm alg = header.getAlgorithm();
        if (!this.supportedJWSAlgorithms().contains(alg)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, this.supportedJWSAlgorithms()));
        }
        try {
            Signature dsa = ECDSA.getSignerAndVerifier(alg, this.getJCAContext().getProvider());
            dsa.initSign(this.privateKey, this.getJCAContext().getSecureRandom());
            dsa.update(signingInput);
            jcaSignature = dsa.sign();
        }
        catch (InvalidKeyException | SignatureException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        int rsByteArrayLength = ECDSA.getSignatureByteArrayLength(header.getAlgorithm());
        byte[] jwsSignature = ECDSA.transcodeSignatureToConcat(jcaSignature, rsByteArrayLength);
        return Base64URL.encode(jwsSignature);
    }
}

