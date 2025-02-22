/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.crypto.tink.subtle.Ed25519Verify
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto;

import com.google.crypto.tink.subtle.Ed25519Verify;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.EdDSAProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.security.GeneralSecurityException;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Ed25519Verifier
extends EdDSAProvider
implements JWSVerifier,
CriticalHeaderParamsAware {
    private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();
    private final OctetKeyPair publicKey;
    private final Ed25519Verify tinkVerifier;

    public Ed25519Verifier(OctetKeyPair publicKey) throws JOSEException {
        this(publicKey, null);
    }

    public Ed25519Verifier(OctetKeyPair publicKey, Set<String> defCritHeaders) throws JOSEException {
        if (!Curve.Ed25519.equals(publicKey.getCurve())) {
            throw new JOSEException("Ed25519Verifier only supports OctetKeyPairs with crv=Ed25519");
        }
        if (publicKey.isPrivate()) {
            throw new JOSEException("Ed25519Verifier requires a public key, use OctetKeyPair.toPublicJWK()");
        }
        this.publicKey = publicKey;
        this.tinkVerifier = new Ed25519Verify(publicKey.getDecodedX());
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
    }

    public OctetKeyPair getPublicKey() {
        return this.publicKey;
    }

    @Override
    public Set<String> getProcessedCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }

    @Override
    public Set<String> getDeferredCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }

    @Override
    public boolean verify(JWSHeader header, byte[] signedContent, Base64URL signature) throws JOSEException {
        JWSAlgorithm alg = header.getAlgorithm();
        if (!JWSAlgorithm.EdDSA.equals(alg)) {
            throw new JOSEException("Ed25519Verifier requires alg=EdDSA in JWSHeader");
        }
        if (!this.critPolicy.headerPasses(header)) {
            return false;
        }
        byte[] jwsSignature = signature.decode();
        try {
            this.tinkVerifier.verify(jwsSignature, signedContent);
            return true;
        }
        catch (GeneralSecurityException e) {
            return false;
        }
    }
}

