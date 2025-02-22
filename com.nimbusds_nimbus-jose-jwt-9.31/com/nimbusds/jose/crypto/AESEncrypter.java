/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AESCryptoProvider;
import com.nimbusds.jose.crypto.impl.AESGCM;
import com.nimbusds.jose.crypto.impl.AESGCMKW;
import com.nimbusds.jose.crypto.impl.AESKW;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.Container;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class AESEncrypter
extends AESCryptoProvider
implements JWEEncrypter {
    public AESEncrypter(SecretKey kek) throws KeyLengthException {
        super(kek);
    }

    public AESEncrypter(byte[] keyBytes) throws KeyLengthException {
        this(new SecretKeySpec(keyBytes, "AES"));
    }

    public AESEncrypter(OctetSequenceKey octJWK) throws KeyLengthException {
        this(octJWK.toSecretKey("AES"));
    }

    @Override
    public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
        JWEHeader updatedHeader;
        Base64URL encryptedKey;
        AlgFamily algFamily;
        JWEAlgorithm alg = header.getAlgorithm();
        if (alg.equals(JWEAlgorithm.A128KW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 128) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 128 bits for A128KW encryption");
            }
            algFamily = AlgFamily.AESKW;
        } else if (alg.equals(JWEAlgorithm.A192KW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 192) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 192 bits for A192KW encryption");
            }
            algFamily = AlgFamily.AESKW;
        } else if (alg.equals(JWEAlgorithm.A256KW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 256) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 256 bits for A256KW encryption");
            }
            algFamily = AlgFamily.AESKW;
        } else if (alg.equals(JWEAlgorithm.A128GCMKW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 128) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 128 bits for A128GCMKW encryption");
            }
            algFamily = AlgFamily.AESGCMKW;
        } else if (alg.equals(JWEAlgorithm.A192GCMKW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 192) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 192 bits for A192GCMKW encryption");
            }
            algFamily = AlgFamily.AESGCMKW;
        } else if (alg.equals(JWEAlgorithm.A256GCMKW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 256) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 256 bits for A256GCMKW encryption");
            }
            algFamily = AlgFamily.AESGCMKW;
        } else {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
        }
        EncryptionMethod enc = header.getEncryptionMethod();
        SecretKey cek = ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
        if (AlgFamily.AESKW.equals((Object)algFamily)) {
            encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, this.getKey(), this.getJCAContext().getKeyEncryptionProvider()));
            updatedHeader = header;
        } else if (AlgFamily.AESGCMKW.equals((Object)algFamily)) {
            Container<byte[]> keyIV = new Container<byte[]>(AESGCM.generateIV(this.getJCAContext().getSecureRandom()));
            AuthenticatedCipherText authCiphCEK = AESGCMKW.encryptCEK(cek, keyIV, this.getKey(), this.getJCAContext().getKeyEncryptionProvider());
            encryptedKey = Base64URL.encode(authCiphCEK.getCipherText());
            updatedHeader = new JWEHeader.Builder(header).iv(Base64URL.encode(keyIV.get())).authTag(Base64URL.encode(authCiphCEK.getAuthenticationTag())).build();
        } else {
            throw new JOSEException("Unexpected JWE algorithm: " + alg);
        }
        return ContentCryptoProvider.encrypt(updatedHeader, clearText, cek, encryptedKey, this.getJCAContext());
    }

    private static enum AlgFamily {
        AESKW,
        AESGCMKW;

    }
}

