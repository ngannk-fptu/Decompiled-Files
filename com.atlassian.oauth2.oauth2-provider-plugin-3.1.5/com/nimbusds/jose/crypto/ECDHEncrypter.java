/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.ECDH;
import com.nimbusds.jose.crypto.impl.ECDHCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ECDHEncrypter
extends ECDHCryptoProvider
implements JWEEncrypter {
    public static final Set<Curve> SUPPORTED_ELLIPTIC_CURVES;
    private final ECPublicKey publicKey;
    private final SecretKey contentEncryptionKey;

    public ECDHEncrypter(ECPublicKey publicKey) throws JOSEException {
        this(publicKey, null);
    }

    public ECDHEncrypter(ECKey ecJWK) throws JOSEException {
        super(ecJWK.getCurve());
        this.publicKey = ecJWK.toECPublicKey();
        this.contentEncryptionKey = null;
    }

    public ECDHEncrypter(ECPublicKey publicKey, SecretKey contentEncryptionKey) throws JOSEException {
        super(Curve.forECParameterSpec(publicKey.getParams()));
        this.publicKey = publicKey;
        if (contentEncryptionKey != null) {
            if (contentEncryptionKey.getAlgorithm() == null || !contentEncryptionKey.getAlgorithm().equals("AES")) {
                throw new IllegalArgumentException("The algorithm of the content encryption key (CEK) must be AES");
            }
            this.contentEncryptionKey = contentEncryptionKey;
        } else {
            this.contentEncryptionKey = null;
        }
    }

    public ECPublicKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public Set<Curve> supportedEllipticCurves() {
        return SUPPORTED_ELLIPTIC_CURVES;
    }

    @Override
    public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
        KeyPair ephemeralKeyPair = this.generateEphemeralKeyPair(this.publicKey.getParams());
        ECPublicKey ephemeralPublicKey = (ECPublicKey)ephemeralKeyPair.getPublic();
        ECPrivateKey ephemeralPrivateKey = (ECPrivateKey)ephemeralKeyPair.getPrivate();
        JWEHeader updatedHeader = new JWEHeader.Builder(header).ephemeralPublicKey(new ECKey.Builder(this.getCurve(), ephemeralPublicKey).build()).build();
        SecretKey Z = ECDH.deriveSharedSecret(this.publicKey, ephemeralPrivateKey, this.getJCAContext().getKeyEncryptionProvider());
        return this.encryptWithZ(updatedHeader, Z, clearText, this.contentEncryptionKey);
    }

    private KeyPair generateEphemeralKeyPair(ECParameterSpec ecParameterSpec) throws JOSEException {
        Provider keProvider = this.getJCAContext().getKeyEncryptionProvider();
        try {
            KeyPairGenerator generator = keProvider != null ? KeyPairGenerator.getInstance("EC", keProvider) : KeyPairGenerator.getInstance("EC");
            generator.initialize(ecParameterSpec);
            return generator.generateKeyPair();
        }
        catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't generate ephemeral EC key pair: " + e.getMessage(), e);
        }
    }

    static {
        LinkedHashSet<Curve> curves = new LinkedHashSet<Curve>();
        curves.add(Curve.P_256);
        curves.add(Curve.P_384);
        curves.add(Curve.P_521);
        SUPPORTED_ELLIPTIC_CURVES = Collections.unmodifiableSet(curves);
    }
}

