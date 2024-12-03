/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.crypto.encrypt;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.CipherUtils;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.util.EncodingUtils;

public final class AesBytesEncryptor
implements BytesEncryptor {
    private final SecretKey secretKey;
    private final Cipher encryptor;
    private final Cipher decryptor;
    private final BytesKeyGenerator ivGenerator;
    private CipherAlgorithm alg;
    private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
    private static final BytesKeyGenerator NULL_IV_GENERATOR = new BytesKeyGenerator(){
        private final byte[] VALUE = new byte[16];

        @Override
        public int getKeyLength() {
            return this.VALUE.length;
        }

        @Override
        public byte[] generateKey() {
            return this.VALUE;
        }
    };

    public AesBytesEncryptor(String password, CharSequence salt) {
        this(password, salt, null);
    }

    public AesBytesEncryptor(String password, CharSequence salt, BytesKeyGenerator ivGenerator) {
        this(password, salt, ivGenerator, CipherAlgorithm.CBC);
    }

    public AesBytesEncryptor(String password, CharSequence salt, BytesKeyGenerator ivGenerator, CipherAlgorithm alg) {
        this(CipherUtils.newSecretKey("PBKDF2WithHmacSHA1", new PBEKeySpec(password.toCharArray(), Hex.decode(salt), 1024, 256)), ivGenerator, alg);
    }

    public AesBytesEncryptor(SecretKey secretKey, BytesKeyGenerator ivGenerator, CipherAlgorithm alg) {
        this.secretKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
        this.alg = alg;
        this.encryptor = alg.createCipher();
        this.decryptor = alg.createCipher();
        this.ivGenerator = ivGenerator != null ? ivGenerator : alg.defaultIvGenerator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] encrypt(byte[] bytes) {
        Cipher cipher = this.encryptor;
        synchronized (cipher) {
            byte[] iv = this.ivGenerator.generateKey();
            CipherUtils.initCipher(this.encryptor, 1, this.secretKey, this.alg.getParameterSpec(iv));
            byte[] encrypted = CipherUtils.doFinal(this.encryptor, bytes);
            return this.ivGenerator != NULL_IV_GENERATOR ? EncodingUtils.concatenate(iv, encrypted) : encrypted;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] decrypt(byte[] encryptedBytes) {
        Cipher cipher = this.decryptor;
        synchronized (cipher) {
            byte[] iv = this.iv(encryptedBytes);
            CipherUtils.initCipher(this.decryptor, 2, this.secretKey, this.alg.getParameterSpec(iv));
            return CipherUtils.doFinal(this.decryptor, this.ivGenerator != NULL_IV_GENERATOR ? this.encrypted(encryptedBytes, iv.length) : encryptedBytes);
        }
    }

    private byte[] iv(byte[] encrypted) {
        return this.ivGenerator != NULL_IV_GENERATOR ? EncodingUtils.subArray(encrypted, 0, this.ivGenerator.getKeyLength()) : NULL_IV_GENERATOR.generateKey();
    }

    private byte[] encrypted(byte[] encryptedBytes, int ivLength) {
        return EncodingUtils.subArray(encryptedBytes, ivLength, encryptedBytes.length);
    }

    static /* synthetic */ BytesKeyGenerator access$000() {
        return NULL_IV_GENERATOR;
    }

    public static enum CipherAlgorithm {
        CBC("AES/CBC/PKCS5Padding", AesBytesEncryptor.access$000()),
        GCM("AES/GCM/NoPadding", KeyGenerators.secureRandom(16));

        private BytesKeyGenerator ivGenerator;
        private String name;

        private CipherAlgorithm(String name, BytesKeyGenerator ivGenerator) {
            this.name = name;
            this.ivGenerator = ivGenerator;
        }

        public String toString() {
            return this.name;
        }

        public AlgorithmParameterSpec getParameterSpec(byte[] iv) {
            return this != CBC ? new GCMParameterSpec(128, iv) : new IvParameterSpec(iv);
        }

        public Cipher createCipher() {
            return CipherUtils.newCipher(this.toString());
        }

        public BytesKeyGenerator defaultIvGenerator() {
            return this.ivGenerator;
        }
    }
}

