/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.internal.crypto.AesCbc;
import com.amazonaws.services.s3.internal.crypto.AesCtr;
import com.amazonaws.services.s3.internal.crypto.AesGcm;
import com.amazonaws.services.s3.internal.crypto.CipherLite;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public abstract class ContentCryptoScheme {
    public static final long MAX_GCM_BLOCKS = 0xFFFFFFFEL;
    public static final long MAX_GCM_BYTES = 0xFFFFFFFE0L;
    static final long MAX_CBC_BYTES = 0x10000000000000L;
    static final long MAX_CTR_BYTES = -1L;
    public static final ContentCryptoScheme AES_CBC = new AesCbc();
    public static final ContentCryptoScheme AES_GCM = new AesGcm();
    public static final ContentCryptoScheme AES_CTR = new AesCtr();

    public abstract String getKeyGeneratorAlgorithm();

    public abstract String getCipherAlgorithm();

    public String getPreferredCipherProvider() {
        return null;
    }

    public abstract int getKeyLengthInBits();

    public abstract int getBlockSizeInBytes();

    public abstract int getIVLengthInBytes();

    public int getTagLengthInBits() {
        return 0;
    }

    public byte[] adjustIV(byte[] iv, long startingBytePos) {
        return iv;
    }

    public String toString() {
        return "cipherAlgo=" + this.getCipherAlgorithm() + ", blockSizeInBytes=" + this.getBlockSizeInBytes() + ", ivLengthInBytes=" + this.getIVLengthInBytes() + ", keyGenAlgo=" + this.getKeyGeneratorAlgorithm() + ", keyLengthInBits=" + this.getKeyLengthInBits() + ", preferredProvider=" + this.getPreferredCipherProvider() + ", tagLengthInBits=" + this.getTagLengthInBits();
    }

    public static byte[] incrementBlocks(byte[] counter, long blockDelta) {
        if (blockDelta == 0L) {
            return counter;
        }
        if (counter == null || counter.length != 16) {
            throw new IllegalArgumentException();
        }
        if (blockDelta > 0xFFFFFFFEL) {
            throw new IllegalStateException();
        }
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (int i = 12; i <= 15; ++i) {
            bb.put(i - 8, counter[i]);
        }
        long val = bb.getLong() + blockDelta;
        if (val > 0xFFFFFFFEL) {
            throw new IllegalStateException();
        }
        bb.rewind();
        byte[] result = bb.putLong(val).array();
        for (int i = 12; i <= 15; ++i) {
            counter[i] = result[i - 8];
        }
        return counter;
    }

    public static ContentCryptoScheme fromCEKAlgo(String cekAlgo) {
        return ContentCryptoScheme.fromCEKAlgo(cekAlgo, false);
    }

    public static ContentCryptoScheme fromCEKAlgo(String cekAlgo, boolean isRangeGet) {
        if (AES_GCM.getCipherAlgorithm().equals(cekAlgo)) {
            return isRangeGet ? AES_CTR : AES_GCM;
        }
        if (cekAlgo == null || AES_CBC.getCipherAlgorithm().equals(cekAlgo)) {
            return AES_CBC;
        }
        throw new UnsupportedOperationException("Unsupported content encryption scheme: " + cekAlgo);
    }

    public CipherLite createCipherLite(SecretKey cek, byte[] iv, int cipherMode, Provider provider, boolean alwaysUseProvider) {
        try {
            Cipher cipher = this.createCipher(provider, alwaysUseProvider);
            cipher.init(cipherMode, (Key)cek, new IvParameterSpec(iv));
            return this.newCipherLite(cipher, cek, cipherMode);
        }
        catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException)e : new SdkClientException("Unable to build cipher: " + e.getMessage() + "\nMake sure you have the JCE unlimited strength policy files installed and configured for your JVM", e);
        }
    }

    private Cipher createCipher(Provider provider, boolean alwaysUseProvider) throws GeneralSecurityException {
        String algorithm = this.getCipherAlgorithm();
        String preferredProvider = this.getPreferredCipherProvider();
        if (alwaysUseProvider) {
            return Cipher.getInstance(algorithm, provider);
        }
        if (preferredProvider != null) {
            return Cipher.getInstance(algorithm, preferredProvider);
        }
        if (provider != null) {
            return Cipher.getInstance(algorithm, provider);
        }
        return Cipher.getInstance(algorithm);
    }

    protected CipherLite newCipherLite(Cipher cipher, SecretKey cek, int cipherMode) {
        return new CipherLite(cipher, this, cek, cipherMode);
    }

    CipherLite createAuxillaryCipher(SecretKey cek, byte[] iv, int cipherMode, Provider securityProvider, long startingBytePos) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        return null;
    }

    public CipherLite createCipherLite(SecretKey cek, byte[] iv, int cipherMode) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        return this.createCipherLite(cek, iv, cipherMode, null, false);
    }

    abstract long getMaxPlaintextSize();

    public final String getKeySpec() {
        return this.getKeyGeneratorAlgorithm() + "_" + this.getKeyLengthInBits();
    }
}

