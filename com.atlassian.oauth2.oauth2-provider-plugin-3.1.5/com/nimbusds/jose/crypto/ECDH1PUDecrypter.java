/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.ECDH1PU;
import com.nimbusds.jose.crypto.impl.ECDH1PUCryptoProvider;
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
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ECDH1PUDecrypter
extends ECDH1PUCryptoProvider
implements JWEDecrypter,
CriticalHeaderParamsAware {
    public static final Set<Curve> SUPPORTED_ELLIPTIC_CURVES;
    private final ECPrivateKey privateKey;
    private final ECPublicKey publicKey;
    private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

    public ECDH1PUDecrypter(ECPrivateKey privateKey, ECPublicKey publicKey) throws JOSEException {
        this(privateKey, publicKey, null);
    }

    public ECDH1PUDecrypter(ECPrivateKey privateKey, ECPublicKey publicKey, Set<String> defCritHeaders) throws JOSEException {
        this(privateKey, publicKey, defCritHeaders, Curve.forECParameterSpec(privateKey.getParams()));
    }

    public ECDH1PUDecrypter(ECPrivateKey privateKey, ECPublicKey publicKey, Set<String> defCritHeaders, Curve curve) throws JOSEException {
        super(curve);
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public ECPublicKey getPublicKey() {
        return this.publicKey;
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
        this.critPolicy.ensureHeaderPasses(header);
        ECKey ephemeralKey = (ECKey)header.getEphemeralPublicKey();
        if (ephemeralKey == null) {
            throw new JOSEException("Missing ephemeral public EC key \"epk\" JWE header parameter");
        }
        ECPublicKey ephemeralPublicKey = ephemeralKey.toECPublicKey();
        SecretKey Z = ECDH1PU.deriveRecipientZ(this.privateKey, this.publicKey, ephemeralPublicKey, this.getJCAContext().getKeyEncryptionProvider());
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

