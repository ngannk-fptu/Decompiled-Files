/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.crypto.tink.subtle.Ed25519Sign
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto;

import com.google.crypto.tink.subtle.Ed25519Sign;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.impl.EdDSAProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.security.GeneralSecurityException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Ed25519Signer
extends EdDSAProvider
implements JWSSigner {
    private final OctetKeyPair privateKey;
    private final Ed25519Sign tinkSigner;

    public Ed25519Signer(OctetKeyPair privateKey) throws JOSEException {
        if (!Curve.Ed25519.equals(privateKey.getCurve())) {
            throw new JOSEException("Ed25519Signer only supports OctetKeyPairs with crv=Ed25519");
        }
        if (!privateKey.isPrivate()) {
            throw new JOSEException("The OctetKeyPair doesn't contain a private part");
        }
        this.privateKey = privateKey;
        try {
            this.tinkSigner = new Ed25519Sign(privateKey.getDecodedD());
        }
        catch (GeneralSecurityException e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public OctetKeyPair getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public Base64URL sign(JWSHeader header, byte[] signingInput) throws JOSEException {
        byte[] jwsSignature;
        JWSAlgorithm alg = header.getAlgorithm();
        if (!JWSAlgorithm.EdDSA.equals(alg)) {
            throw new JOSEException("Ed25519Signer requires alg=EdDSA in JWSHeader");
        }
        try {
            jwsSignature = this.tinkSigner.sign(signingInput);
        }
        catch (GeneralSecurityException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        return Base64URL.encode(jwsSignature);
    }
}

