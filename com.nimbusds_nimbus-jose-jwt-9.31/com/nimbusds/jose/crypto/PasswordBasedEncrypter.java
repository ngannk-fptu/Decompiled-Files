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
import com.nimbusds.jose.crypto.impl.AESKW;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.PBKDF2;
import com.nimbusds.jose.crypto.impl.PRFParams;
import com.nimbusds.jose.crypto.impl.PasswordBasedCryptoProvider;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.StandardCharset;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class PasswordBasedEncrypter
extends PasswordBasedCryptoProvider
implements JWEEncrypter {
    public static final int MIN_SALT_LENGTH = 8;
    private final int saltLength;
    public static final int MIN_RECOMMENDED_ITERATION_COUNT = 1000;
    private final int iterationCount;

    public PasswordBasedEncrypter(byte[] password, int saltLength, int iterationCount) {
        super(password);
        if (saltLength < 8) {
            throw new IllegalArgumentException("The minimum salt length (p2s) is 8 bytes");
        }
        this.saltLength = saltLength;
        if (iterationCount < 1000) {
            throw new IllegalArgumentException("The minimum recommended iteration count (p2c) is 1000");
        }
        this.iterationCount = iterationCount;
    }

    public PasswordBasedEncrypter(String password, int saltLength, int iterationCount) {
        this(password.getBytes(StandardCharset.UTF_8), saltLength, iterationCount);
    }

    @Override
    public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
        JWEAlgorithm alg = header.getAlgorithm();
        EncryptionMethod enc = header.getEncryptionMethod();
        byte[] salt = new byte[this.saltLength];
        this.getJCAContext().getSecureRandom().nextBytes(salt);
        byte[] formattedSalt = PBKDF2.formatSalt(alg, salt);
        PRFParams prfParams = PRFParams.resolve(alg, this.getJCAContext().getMACProvider());
        SecretKey psKey = PBKDF2.deriveKey(this.getPassword(), formattedSalt, this.iterationCount, prfParams);
        JWEHeader updatedHeader = new JWEHeader.Builder(header).pbes2Salt(Base64URL.encode(salt)).pbes2Count(this.iterationCount).build();
        SecretKey cek = ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
        Base64URL encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, psKey, this.getJCAContext().getKeyEncryptionProvider()));
        return ContentCryptoProvider.encrypt(updatedHeader, clearText, cek, encryptedKey, this.getJCAContext());
    }

    public int getSaltLength() {
        return this.saltLength;
    }

    public int getIterationCount() {
        return this.iterationCount;
    }
}

