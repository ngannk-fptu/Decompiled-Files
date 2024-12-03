/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.RSA1_5;
import com.nimbusds.jose.crypto.impl.RSACryptoProvider;
import com.nimbusds.jose.crypto.impl.RSA_OAEP;
import com.nimbusds.jose.crypto.impl.RSA_OAEP_SHA2;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RSAEncrypter
extends RSACryptoProvider
implements JWEEncrypter {
    private final RSAPublicKey publicKey;
    private final SecretKey contentEncryptionKey;

    public RSAEncrypter(RSAPublicKey publicKey) {
        this(publicKey, null);
    }

    public RSAEncrypter(RSAKey rsaJWK) throws JOSEException {
        this(rsaJWK.toRSAPublicKey());
    }

    public RSAEncrypter(RSAPublicKey publicKey, SecretKey contentEncryptionKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("The public RSA key must not be null");
        }
        this.publicKey = publicKey;
        Set<String> acceptableCEKAlgs = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("AES", "ChaCha20")));
        if (contentEncryptionKey != null) {
            if (contentEncryptionKey.getAlgorithm() == null || !acceptableCEKAlgs.contains(contentEncryptionKey.getAlgorithm())) {
                throw new IllegalArgumentException("The algorithm of the content encryption key (CEK) must be AES or ChaCha20");
            }
            this.contentEncryptionKey = contentEncryptionKey;
        } else {
            this.contentEncryptionKey = null;
        }
    }

    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
        Base64URL encryptedKey;
        JWEAlgorithm alg = header.getAlgorithm();
        EncryptionMethod enc = header.getEncryptionMethod();
        SecretKey cek = this.contentEncryptionKey != null ? this.contentEncryptionKey : ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
        if (alg.equals(JWEAlgorithm.RSA1_5)) {
            encryptedKey = Base64URL.encode(RSA1_5.encryptCEK(this.publicKey, cek, this.getJCAContext().getKeyEncryptionProvider()));
        } else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
            encryptedKey = Base64URL.encode(RSA_OAEP.encryptCEK(this.publicKey, cek, this.getJCAContext().getKeyEncryptionProvider()));
        } else if (alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
            encryptedKey = Base64URL.encode(RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 256, this.getJCAContext().getKeyEncryptionProvider()));
        } else if (alg.equals(JWEAlgorithm.RSA_OAEP_384)) {
            encryptedKey = Base64URL.encode(RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 384, this.getJCAContext().getKeyEncryptionProvider()));
        } else if (alg.equals(JWEAlgorithm.RSA_OAEP_512)) {
            encryptedKey = Base64URL.encode(RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 512, this.getJCAContext().getKeyEncryptionProvider()));
        } else {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
        }
        return ContentCryptoProvider.encrypt(header, clearText, cek, encryptedKey, this.getJCAContext());
    }
}

