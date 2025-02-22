/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.crypto.tink.subtle.X25519
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto;

import com.google.crypto.tink.subtle.X25519;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.ECDH;
import com.nimbusds.jose.crypto.impl.ECDHCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.Set;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class X25519Encrypter
extends ECDHCryptoProvider
implements JWEEncrypter {
    private final OctetKeyPair publicKey;

    public X25519Encrypter(OctetKeyPair publicKey) throws JOSEException {
        super(publicKey.getCurve());
        if (!Curve.X25519.equals(publicKey.getCurve())) {
            throw new JOSEException("X25519Encrypter only supports OctetKeyPairs with crv=X25519");
        }
        if (publicKey.isPrivate()) {
            throw new JOSEException("X25519Encrypter requires a public key, use OctetKeyPair.toPublicJWK()");
        }
        this.publicKey = publicKey;
    }

    @Override
    public Set<Curve> supportedEllipticCurves() {
        return Collections.singleton(Curve.X25519);
    }

    public OctetKeyPair getPublicKey() {
        return this.publicKey;
    }

    @Override
    public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
        byte[] ephemeralPublicKeyBytes;
        byte[] ephemeralPrivateKeyBytes = X25519.generatePrivateKey();
        try {
            ephemeralPublicKeyBytes = X25519.publicFromPrivate((byte[])ephemeralPrivateKeyBytes);
        }
        catch (InvalidKeyException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        OctetKeyPair ephemeralPrivateKey = new OctetKeyPair.Builder(this.getCurve(), Base64URL.encode(ephemeralPublicKeyBytes)).d(Base64URL.encode(ephemeralPrivateKeyBytes)).build();
        OctetKeyPair ephemeralPublicKey = ephemeralPrivateKey.toPublicJWK();
        JWEHeader updatedHeader = new JWEHeader.Builder(header).ephemeralPublicKey(ephemeralPublicKey).build();
        SecretKey Z = ECDH.deriveSharedSecret(this.publicKey, ephemeralPrivateKey);
        return this.encryptWithZ(updatedHeader, Z, clearText);
    }
}

