/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.LongDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.MemoableResetException;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SHA512tDigest
extends LongDigest {
    private int digestLength;
    private long H1t;
    private long H2t;
    private long H3t;
    private long H4t;
    private long H5t;
    private long H6t;
    private long H7t;
    private long H8t;

    public SHA512tDigest(int bitLength) {
        this(bitLength, CryptoServicePurpose.ANY);
    }

    public SHA512tDigest(int bitLength, CryptoServicePurpose purpose) {
        if (bitLength >= 512) {
            throw new IllegalArgumentException("bitLength cannot be >= 512");
        }
        if (bitLength % 8 != 0) {
            throw new IllegalArgumentException("bitLength needs to be a multiple of 8");
        }
        if (bitLength == 384) {
            throw new IllegalArgumentException("bitLength cannot be 384 use SHA384 instead");
        }
        this.digestLength = bitLength / 8;
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.tIvGenerate(this.digestLength * 8);
        this.reset();
    }

    public SHA512tDigest(SHA512tDigest t) {
        super(t);
        this.digestLength = t.digestLength;
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.reset(t);
    }

    public SHA512tDigest(byte[] encodedState) {
        this(SHA512tDigest.readDigestLength(encodedState), CryptoServicePurpose.values()[encodedState[encodedState.length - 1]]);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.restoreState(encodedState);
    }

    private static int readDigestLength(byte[] encodedState) {
        return Pack.bigEndianToInt(encodedState, encodedState.length - 5);
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-512/" + Integer.toString(this.digestLength * 8);
    }

    @Override
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.finish();
        SHA512tDigest.longToBigEndian(this.H1, out, outOff, this.digestLength);
        SHA512tDigest.longToBigEndian(this.H2, out, outOff + 8, this.digestLength - 8);
        SHA512tDigest.longToBigEndian(this.H3, out, outOff + 16, this.digestLength - 16);
        SHA512tDigest.longToBigEndian(this.H4, out, outOff + 24, this.digestLength - 24);
        SHA512tDigest.longToBigEndian(this.H5, out, outOff + 32, this.digestLength - 32);
        SHA512tDigest.longToBigEndian(this.H6, out, outOff + 40, this.digestLength - 40);
        SHA512tDigest.longToBigEndian(this.H7, out, outOff + 48, this.digestLength - 48);
        SHA512tDigest.longToBigEndian(this.H8, out, outOff + 56, this.digestLength - 56);
        this.reset();
        return this.digestLength;
    }

    @Override
    public void reset() {
        super.reset();
        this.H1 = this.H1t;
        this.H2 = this.H2t;
        this.H3 = this.H3t;
        this.H4 = this.H4t;
        this.H5 = this.H5t;
        this.H6 = this.H6t;
        this.H7 = this.H7t;
        this.H8 = this.H8t;
    }

    private void tIvGenerate(int bitLength) {
        this.H1 = -3482333909917012819L;
        this.H2 = 2216346199247487646L;
        this.H3 = -7364697282686394994L;
        this.H4 = 65953792586715988L;
        this.H5 = -816286391624063116L;
        this.H6 = 4512832404995164602L;
        this.H7 = -5033199132376557362L;
        this.H8 = -124578254951840548L;
        this.update((byte)83);
        this.update((byte)72);
        this.update((byte)65);
        this.update((byte)45);
        this.update((byte)53);
        this.update((byte)49);
        this.update((byte)50);
        this.update((byte)47);
        if (bitLength > 100) {
            this.update((byte)(bitLength / 100 + 48));
            this.update((byte)((bitLength %= 100) / 10 + 48));
            this.update((byte)((bitLength %= 10) + 48));
        } else if (bitLength > 10) {
            this.update((byte)(bitLength / 10 + 48));
            this.update((byte)((bitLength %= 10) + 48));
        } else {
            this.update((byte)(bitLength + 48));
        }
        this.finish();
        this.H1t = this.H1;
        this.H2t = this.H2;
        this.H3t = this.H3;
        this.H4t = this.H4;
        this.H5t = this.H5;
        this.H6t = this.H6;
        this.H7t = this.H7;
        this.H8t = this.H8;
    }

    private static void longToBigEndian(long n, byte[] bs, int off, int max) {
        if (max > 0) {
            SHA512tDigest.intToBigEndian((int)(n >>> 32), bs, off, max);
            if (max > 4) {
                SHA512tDigest.intToBigEndian((int)(n & 0xFFFFFFFFL), bs, off + 4, max - 4);
            }
        }
    }

    private static void intToBigEndian(int n, byte[] bs, int off, int max) {
        int num = Math.min(4, max);
        while (--num >= 0) {
            int shift = 8 * (3 - num);
            bs[off + num] = (byte)(n >>> shift);
        }
    }

    @Override
    public Memoable copy() {
        return new SHA512tDigest(this);
    }

    @Override
    public void reset(Memoable other) {
        SHA512tDigest t = (SHA512tDigest)other;
        if (this.digestLength != t.digestLength) {
            throw new MemoableResetException("digestLength inappropriate in other");
        }
        super.copyIn(t);
        this.H1t = t.H1t;
        this.H2t = t.H2t;
        this.H3t = t.H3t;
        this.H4t = t.H4t;
        this.H5t = t.H5t;
        this.H6t = t.H6t;
        this.H7t = t.H7t;
        this.H8t = t.H8t;
    }

    @Override
    public byte[] getEncodedState() {
        int baseSize = this.getEncodedStateSize();
        byte[] encoded = new byte[baseSize + 4 + 1];
        this.populateState(encoded);
        Pack.intToBigEndian(this.digestLength * 8, encoded, baseSize);
        encoded[encoded.length - 1] = (byte)this.purpose.ordinal();
        return encoded;
    }

    @Override
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, this.getDigestSize() * 8, this.purpose);
    }
}

