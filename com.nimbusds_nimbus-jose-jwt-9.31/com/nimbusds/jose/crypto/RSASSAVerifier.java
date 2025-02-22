/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.RSASSA;
import com.nimbusds.jose.crypto.impl.RSASSAProvider;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RSASSAVerifier
extends RSASSAProvider
implements JWSVerifier,
CriticalHeaderParamsAware {
    private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();
    private final RSAPublicKey publicKey;

    public RSASSAVerifier(RSAPublicKey publicKey) {
        this(publicKey, null);
    }

    public RSASSAVerifier(RSAKey rsaJWK) throws JOSEException {
        this(rsaJWK.toRSAPublicKey(), null);
    }

    public RSASSAVerifier(RSAPublicKey publicKey, Set<String> defCritHeaders) {
        if (publicKey == null) {
            throw new IllegalArgumentException("The public RSA key must not be null");
        }
        this.publicKey = publicKey;
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
    }

    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public Set<String> getProcessedCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }

    @Override
    public Set<String> getDeferredCriticalHeaderParams() {
        return this.critPolicy.getDeferredCriticalHeaderParams();
    }

    @Override
    public boolean verify(JWSHeader header, byte[] signedContent, Base64URL signature) throws JOSEException {
        if (!this.critPolicy.headerPasses(header)) {
            return false;
        }
        Signature verifier = RSASSA.getSignerAndVerifier(header.getAlgorithm(), this.getJCAContext().getProvider());
        try {
            verifier.initVerify(this.publicKey);
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("Invalid public RSA key: " + e.getMessage(), e);
        }
        try {
            verifier.update(signedContent);
            return verifier.verify(signature.decode());
        }
        catch (SignatureException e) {
            return false;
        }
    }
}

