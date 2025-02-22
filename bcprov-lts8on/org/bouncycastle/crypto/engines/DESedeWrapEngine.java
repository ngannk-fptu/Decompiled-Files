/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class DESedeWrapEngine
implements Wrapper {
    private CBCBlockCipher engine;
    private KeyParameter param;
    private ParametersWithIV paramPlusIV;
    private byte[] iv;
    private boolean forWrapping;
    private static final byte[] IV2 = new byte[]{74, -35, -94, 44, 121, -24, 33, 5};
    Digest sha1 = DigestFactory.createSHA1();
    byte[] digest = new byte[20];

    @Override
    public void init(boolean forWrapping, CipherParameters param) {
        SecureRandom sr;
        this.forWrapping = forWrapping;
        this.engine = new CBCBlockCipher(new DESedeEngine());
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom pr = (ParametersWithRandom)param;
            param = pr.getParameters();
            sr = pr.getRandom();
        } else {
            sr = CryptoServicesRegistrar.getSecureRandom();
        }
        if (param instanceof KeyParameter) {
            this.param = (KeyParameter)param;
            if (this.forWrapping) {
                this.iv = new byte[8];
                sr.nextBytes(this.iv);
                this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
            }
        } else if (param instanceof ParametersWithIV) {
            this.paramPlusIV = (ParametersWithIV)param;
            this.iv = this.paramPlusIV.getIV();
            this.param = (KeyParameter)this.paramPlusIV.getParameters();
            if (this.forWrapping) {
                if (this.iv == null || this.iv.length != 8) {
                    throw new IllegalArgumentException("IV is not 8 octets");
                }
            } else {
                throw new IllegalArgumentException("You should not supply an IV for unwrapping");
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "DESede";
    }

    @Override
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        byte[] keyToBeWrapped = new byte[inLen];
        System.arraycopy(in, inOff, keyToBeWrapped, 0, inLen);
        byte[] CKS = this.calculateCMSKeyChecksum(keyToBeWrapped);
        byte[] WKCKS = new byte[keyToBeWrapped.length + CKS.length];
        System.arraycopy(keyToBeWrapped, 0, WKCKS, 0, keyToBeWrapped.length);
        System.arraycopy(CKS, 0, WKCKS, keyToBeWrapped.length, CKS.length);
        int blockSize = this.engine.getBlockSize();
        if (WKCKS.length % blockSize != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        byte[] TEMP1 = new byte[WKCKS.length];
        for (int currentBytePos = 0; currentBytePos != WKCKS.length; currentBytePos += blockSize) {
            this.engine.processBlock(WKCKS, currentBytePos, TEMP1, currentBytePos);
        }
        byte[] TEMP2 = new byte[this.iv.length + TEMP1.length];
        System.arraycopy(this.iv, 0, TEMP2, 0, this.iv.length);
        System.arraycopy(TEMP1, 0, TEMP2, this.iv.length, TEMP1.length);
        Arrays.reverseInPlace(TEMP2);
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(true, param2);
        for (int currentBytePos = 0; currentBytePos != TEMP2.length; currentBytePos += blockSize) {
            this.engine.processBlock(TEMP2, currentBytePos, TEMP2, currentBytePos);
        }
        return TEMP2;
    }

    @Override
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (in == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        int blockSize = this.engine.getBlockSize();
        if (inLen % blockSize != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + blockSize);
        }
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(false, param2);
        byte[] TEMP2 = new byte[inLen];
        for (int currentBytePos = 0; currentBytePos != inLen; currentBytePos += blockSize) {
            this.engine.processBlock(in, inOff + currentBytePos, TEMP2, currentBytePos);
        }
        Arrays.reverseInPlace(TEMP2);
        this.iv = new byte[8];
        byte[] TEMP1 = new byte[TEMP2.length - 8];
        System.arraycopy(TEMP2, 0, this.iv, 0, 8);
        System.arraycopy(TEMP2, 8, TEMP1, 0, TEMP2.length - 8);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        this.engine.init(false, this.paramPlusIV);
        byte[] WKCKS = new byte[TEMP1.length];
        for (int currentBytePos = 0; currentBytePos != WKCKS.length; currentBytePos += blockSize) {
            this.engine.processBlock(TEMP1, currentBytePos, WKCKS, currentBytePos);
        }
        byte[] result = new byte[WKCKS.length - 8];
        byte[] CKStoBeVerified = new byte[8];
        System.arraycopy(WKCKS, 0, result, 0, WKCKS.length - 8);
        System.arraycopy(WKCKS, WKCKS.length - 8, CKStoBeVerified, 0, 8);
        if (!this.checkCMSKeyChecksum(result, CKStoBeVerified)) {
            throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
        }
        return result;
    }

    private byte[] calculateCMSKeyChecksum(byte[] key) {
        byte[] result = new byte[8];
        this.sha1.update(key, 0, key.length);
        this.sha1.doFinal(this.digest, 0);
        System.arraycopy(this.digest, 0, result, 0, 8);
        return result;
    }

    private boolean checkCMSKeyChecksum(byte[] key, byte[] checksum) {
        return Arrays.constantTimeAreEqual(this.calculateCMSKeyChecksum(key), checksum);
    }
}

