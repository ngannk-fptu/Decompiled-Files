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
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;

public class RC2WrapEngine
implements Wrapper {
    private CBCBlockCipher engine;
    private CipherParameters param;
    private ParametersWithIV paramPlusIV;
    private byte[] iv;
    private boolean forWrapping;
    private SecureRandom sr;
    private static final byte[] IV2 = new byte[]{74, -35, -94, 44, 121, -24, 33, 5};
    Digest sha1 = DigestFactory.createSHA1();
    byte[] digest = new byte[20];

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        this.engine = new CBCBlockCipher(new RC2Engine());
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom pWithR = (ParametersWithRandom)param;
            this.sr = pWithR.getRandom();
            param = pWithR.getParameters();
        } else {
            this.sr = CryptoServicesRegistrar.getSecureRandom();
        }
        if (param instanceof ParametersWithIV) {
            this.paramPlusIV = (ParametersWithIV)param;
            this.iv = this.paramPlusIV.getIV();
            this.param = this.paramPlusIV.getParameters();
            if (!this.forWrapping) throw new IllegalArgumentException("You should not supply an IV for unwrapping");
            if (this.iv != null && this.iv.length == 8) return;
            throw new IllegalArgumentException("IV is not 8 octets");
        }
        this.param = param;
        if (!this.forWrapping) return;
        this.iv = new byte[8];
        this.sr.nextBytes(this.iv);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
    }

    @Override
    public String getAlgorithmName() {
        return "RC2";
    }

    @Override
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        int length = inLen + 1;
        if (length % 8 != 0) {
            length += 8 - length % 8;
        }
        byte[] keyToBeWrapped = new byte[length];
        keyToBeWrapped[0] = (byte)inLen;
        System.arraycopy(in, inOff, keyToBeWrapped, 1, inLen);
        byte[] pad = new byte[keyToBeWrapped.length - inLen - 1];
        if (pad.length > 0) {
            this.sr.nextBytes(pad);
            System.arraycopy(pad, 0, keyToBeWrapped, inLen + 1, pad.length);
        }
        byte[] CKS = this.calculateCMSKeyChecksum(keyToBeWrapped);
        byte[] WKCKS = new byte[keyToBeWrapped.length + CKS.length];
        System.arraycopy(keyToBeWrapped, 0, WKCKS, 0, keyToBeWrapped.length);
        System.arraycopy(CKS, 0, WKCKS, keyToBeWrapped.length, CKS.length);
        byte[] TEMP1 = new byte[WKCKS.length];
        System.arraycopy(WKCKS, 0, TEMP1, 0, WKCKS.length);
        int noOfBlocks = WKCKS.length / this.engine.getBlockSize();
        int extraBytes = WKCKS.length % this.engine.getBlockSize();
        if (extraBytes != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        for (int i = 0; i < noOfBlocks; ++i) {
            int currentBytePos = i * this.engine.getBlockSize();
            this.engine.processBlock(TEMP1, currentBytePos, TEMP1, currentBytePos);
        }
        byte[] TEMP2 = new byte[this.iv.length + TEMP1.length];
        System.arraycopy(this.iv, 0, TEMP2, 0, this.iv.length);
        System.arraycopy(TEMP1, 0, TEMP2, this.iv.length, TEMP1.length);
        byte[] TEMP3 = new byte[TEMP2.length];
        for (int i = 0; i < TEMP2.length; ++i) {
            TEMP3[i] = TEMP2[TEMP2.length - (i + 1)];
        }
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(true, param2);
        for (int i = 0; i < noOfBlocks + 1; ++i) {
            int currentBytePos = i * this.engine.getBlockSize();
            this.engine.processBlock(TEMP3, currentBytePos, TEMP3, currentBytePos);
        }
        return TEMP3;
    }

    @Override
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (in == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        if (inLen % this.engine.getBlockSize() != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + this.engine.getBlockSize());
        }
        ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);
        this.engine.init(false, param2);
        byte[] TEMP3 = new byte[inLen];
        System.arraycopy(in, inOff, TEMP3, 0, inLen);
        for (int i = 0; i < TEMP3.length / this.engine.getBlockSize(); ++i) {
            int currentBytePos = i * this.engine.getBlockSize();
            this.engine.processBlock(TEMP3, currentBytePos, TEMP3, currentBytePos);
        }
        byte[] TEMP2 = new byte[TEMP3.length];
        for (int i = 0; i < TEMP3.length; ++i) {
            TEMP2[i] = TEMP3[TEMP3.length - (i + 1)];
        }
        this.iv = new byte[8];
        byte[] TEMP1 = new byte[TEMP2.length - 8];
        System.arraycopy(TEMP2, 0, this.iv, 0, 8);
        System.arraycopy(TEMP2, 8, TEMP1, 0, TEMP2.length - 8);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        this.engine.init(false, this.paramPlusIV);
        byte[] LCEKPADICV = new byte[TEMP1.length];
        System.arraycopy(TEMP1, 0, LCEKPADICV, 0, TEMP1.length);
        for (int i = 0; i < LCEKPADICV.length / this.engine.getBlockSize(); ++i) {
            int currentBytePos = i * this.engine.getBlockSize();
            this.engine.processBlock(LCEKPADICV, currentBytePos, LCEKPADICV, currentBytePos);
        }
        byte[] result = new byte[LCEKPADICV.length - 8];
        byte[] CKStoBeVerified = new byte[8];
        System.arraycopy(LCEKPADICV, 0, result, 0, LCEKPADICV.length - 8);
        System.arraycopy(LCEKPADICV, LCEKPADICV.length - 8, CKStoBeVerified, 0, 8);
        if (!this.checkCMSKeyChecksum(result, CKStoBeVerified)) {
            throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
        }
        if (result.length - ((result[0] & 0xFF) + 1) > 7) {
            throw new InvalidCipherTextException("too many pad bytes (" + (result.length - ((result[0] & 0xFF) + 1)) + ")");
        }
        byte[] CEK = new byte[result[0]];
        System.arraycopy(result, 1, CEK, 0, CEK.length);
        return CEK;
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

