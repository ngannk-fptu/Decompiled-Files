/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.ECDH;
import com.nimbusds.jose.crypto.impl.ECDHCryptoProvider;
import com.nimbusds.jose.crypto.utils.ECChecks;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.Base64URL;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;

public class ECDHDecrypter
extends ECDHCryptoProvider
implements JWEDecrypter,
CriticalHeaderParamsAware {
    public static final Set<Curve> SUPPORTED_ELLIPTIC_CURVES;
    private final PrivateKey privateKey;
    private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

    public ECDHDecrypter(ECPrivateKey privateKey) throws JOSEException {
        this(privateKey, null);
    }

    public ECDHDecrypter(ECKey ecJWK) throws JOSEException {
        super(ecJWK.getCurve());
        if (!ecJWK.isPrivate()) {
            throw new JOSEException("The EC JWK doesn't contain a private part");
        }
        this.privateKey = ecJWK.toECPrivateKey();
    }

    public ECDHDecrypter(ECPrivateKey privateKey, Set<String> defCritHeaders) throws JOSEException {
        this(privateKey, defCritHeaders, Curve.forECParameterSpec(privateKey.getParams()));
    }

    public ECDHDecrypter(PrivateKey privateKey, Set<String> defCritHeaders, Curve curve) throws JOSEException {
        super(curve);
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public Set<Curve> supportedEllipticCurves() {
        return SUPPORTED_ELLIPTIC_CURVES;
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
    public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) throws JOSEException {
        ECPrivateKey ecPrivateKey;
        this.critPolicy.ensureHeaderPasses(header);
        ECKey ephemeralKey = (ECKey)header.getEphemeralPublicKey();
        if (ephemeralKey == null) {
            throw new JOSEException("Missing ephemeral public EC key \"epk\" JWE header parameter");
        }
        ECPublicKey ephemeralPublicKey = ephemeralKey.toECPublicKey();
        if (this.getPrivateKey() instanceof ECPrivateKey ? !ECChecks.isPointOnCurve(ephemeralPublicKey, ecPrivateKey = (ECPrivateKey)this.getPrivateKey()) : !ECChecks.isPointOnCurve(ephemeralPublicKey, this.getCurve().toECParameterSpec())) {
            throw new JOSEException("Invalid ephemeral public EC key: Point(s) not on the expected curve");
        }
        SecretKey Z = ECDH.deriveSharedSecret(ephemeralPublicKey, this.privateKey, this.getJCAContext().getKeyEncryptionProvider());
        return this.decryptWithZ(header, Z, encryptedKey, iv, cipherText, authTag);
    }

    static {
        LinkedHashSet<Curve> curves = new LinkedHashSet<Curve>();
        curves.add(Curve.P_256);
        curves.add(Curve.P_384);
        curves.add(Curve.P_521);
        SUPPORTED_ELLIPTIC_CURVES = Collections.unmodifiableSet(curves);
    }
}

