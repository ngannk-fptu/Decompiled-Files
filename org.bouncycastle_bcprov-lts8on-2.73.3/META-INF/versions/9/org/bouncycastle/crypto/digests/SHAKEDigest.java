/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.Utils;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SHAKEDigest
extends KeccakDigest
implements Xof {
    private static int checkBitLength(int bitStrength) {
        switch (bitStrength) {
            case 128: 
            case 256: {
                return bitStrength;
            }
        }
        throw new IllegalArgumentException("'bitStrength' " + bitStrength + " not supported for SHAKE");
    }

    public SHAKEDigest() {
        this(128);
    }

    public SHAKEDigest(CryptoServicePurpose purpose) {
        this(128, purpose);
    }

    public SHAKEDigest(int bitStrength) {
        super(SHAKEDigest.checkBitLength(bitStrength), CryptoServicePurpose.ANY);
    }

    public SHAKEDigest(int bitStrength, CryptoServicePurpose purpose) {
        super(SHAKEDigest.checkBitLength(bitStrength), purpose);
    }

    public SHAKEDigest(SHAKEDigest source) {
        super(source);
    }

    @Override
    public String getAlgorithmName() {
        return "SHAKE" + this.fixedOutputLength;
    }

    @Override
    public int getDigestSize() {
        return this.fixedOutputLength / 4;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        return this.doFinal(out, outOff, this.getDigestSize());
    }

    @Override
    public int doFinal(byte[] out, int outOff, int outLen) {
        int length = this.doOutput(out, outOff, outLen);
        this.reset();
        return length;
    }

    @Override
    public int doOutput(byte[] out, int outOff, int outLen) {
        if (!this.squeezing) {
            this.absorbBits(15, 4);
        }
        this.squeeze(out, outOff, (long)outLen * 8L);
        return outLen;
    }

    @Override
    protected int doFinal(byte[] out, int outOff, byte partialByte, int partialBits) {
        return this.doFinal(out, outOff, this.getDigestSize(), partialByte, partialBits);
    }

    protected int doFinal(byte[] out, int outOff, int outLen, byte partialByte, int partialBits) {
        if (partialBits < 0 || partialBits > 7) {
            throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
        }
        int finalInput = partialByte & (1 << partialBits) - 1 | 15 << partialBits;
        int finalBits = partialBits + 4;
        if (finalBits >= 8) {
            this.absorb((byte)finalInput);
            finalBits -= 8;
            finalInput >>>= 8;
        }
        if (finalBits > 0) {
            this.absorbBits(finalInput, finalBits);
        }
        this.squeeze(out, outOff, (long)outLen * 8L);
        this.reset();
        return outLen;
    }

    @Override
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, this.purpose);
    }
}

