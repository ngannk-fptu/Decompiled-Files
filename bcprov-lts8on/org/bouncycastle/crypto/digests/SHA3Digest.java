/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.digests.KeccakDigest;

public class SHA3Digest
extends KeccakDigest {
    private static int checkBitLength(int bitLength) {
        switch (bitLength) {
            case 224: 
            case 256: 
            case 384: 
            case 512: {
                return bitLength;
            }
        }
        throw new IllegalArgumentException("'bitLength' " + bitLength + " not supported for SHA-3");
    }

    public SHA3Digest() {
        this(256, CryptoServicePurpose.ANY);
    }

    public SHA3Digest(CryptoServicePurpose purpose) {
        this(256, purpose);
    }

    public SHA3Digest(int bitLength) {
        super(SHA3Digest.checkBitLength(bitLength), CryptoServicePurpose.ANY);
    }

    public SHA3Digest(int bitLength, CryptoServicePurpose purpose) {
        super(SHA3Digest.checkBitLength(bitLength), purpose);
    }

    public SHA3Digest(SHA3Digest source) {
        super(source);
    }

    @Override
    public String getAlgorithmName() {
        return "SHA3-" + this.fixedOutputLength;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.absorbBits(2, 2);
        return super.doFinal(out, outOff);
    }

    @Override
    protected int doFinal(byte[] out, int outOff, byte partialByte, int partialBits) {
        if (partialBits < 0 || partialBits > 7) {
            throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
        }
        int finalInput = partialByte & (1 << partialBits) - 1 | 2 << partialBits;
        int finalBits = partialBits + 2;
        if (finalBits >= 8) {
            this.absorb((byte)finalInput);
            finalBits -= 8;
            finalInput >>>= 8;
        }
        return super.doFinal(out, outOff, (byte)finalInput, finalBits);
    }
}

