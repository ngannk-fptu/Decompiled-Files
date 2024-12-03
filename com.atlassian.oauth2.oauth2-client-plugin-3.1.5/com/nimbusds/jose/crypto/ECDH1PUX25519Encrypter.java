/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.ECDH1PU;
import com.nimbusds.jose.crypto.impl.ECDH1PUCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import java.util.Collections;
import java.util.Set;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ECDH1PUX25519Encrypter
extends ECDH1PUCryptoProvider
implements JWEEncrypter {
    private final OctetKeyPair publicKey;
    private final OctetKeyPair privateKey;
    private final SecretKey contentEncryptionKey;

    public ECDH1PUX25519Encrypter(OctetKeyPair privateKey, OctetKeyPair publicKey) throws JOSEException {
        this(privateKey, publicKey, null);
    }

    public ECDH1PUX25519Encrypter(OctetKeyPair privateKey, OctetKeyPair publicKey, SecretKey contentEncryptionKey) throws JOSEException {
        super(publicKey.getCurve());
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        if (!(contentEncryptionKey == null || contentEncryptionKey.getAlgorithm() != null && contentEncryptionKey.getAlgorithm().equals("AES"))) {
            throw new IllegalArgumentException("The algorithm of the content encryption key (CEK) must be AES");
        }
        this.contentEncryptionKey = contentEncryptionKey;
    }

    @Override
    public Set<Curve> supportedEllipticCurves() {
        return Collections.singleton(Curve.X25519);
    }

    public OctetKeyPair getPublicKey() {
        return this.publicKey;
    }

    public OctetKeyPair getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
        OctetKeyPair ephemeralPrivateKey = new OctetKeyPairGenerator(this.getCurve()).generate();
        OctetKeyPair ephemeralPublicKey = ephemeralPrivateKey.toPublicJWK();
        JWEHeader updatedHeader = new JWEHeader.Builder(header).ephemeralPublicKey(ephemeralPublicKey).build();
        SecretKey Z = ECDH1PU.deriveSenderZ(this.privateKey, this.publicKey, ephemeralPrivateKey);
        return this.encryptWithZ(updatedHeader, Z, clearText, this.contentEncryptionKey);
    }
}

