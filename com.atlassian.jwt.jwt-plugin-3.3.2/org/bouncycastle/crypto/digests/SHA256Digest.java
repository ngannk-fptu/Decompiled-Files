/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.digests.EncodableDigest;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA256Digest
extends GeneralDigest
implements EncodableDigest {
    private static final int DIGEST_LENGTH = 32;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int H6;
    private int H7;
    private int H8;
    private int[] X = new int[64];
    private int xOff;
    static final int[] K = new int[]{1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};

    public SHA256Digest() {
        this.reset();
    }

    public SHA256Digest(SHA256Digest sHA256Digest) {
        super(sHA256Digest);
        this.copyIn(sHA256Digest);
    }

    private void copyIn(SHA256Digest sHA256Digest) {
        super.copyIn(sHA256Digest);
        this.H1 = sHA256Digest.H1;
        this.H2 = sHA256Digest.H2;
        this.H3 = sHA256Digest.H3;
        this.H4 = sHA256Digest.H4;
        this.H5 = sHA256Digest.H5;
        this.H6 = sHA256Digest.H6;
        this.H7 = sHA256Digest.H7;
        this.H8 = sHA256Digest.H8;
        System.arraycopy(sHA256Digest.X, 0, this.X, 0, sHA256Digest.X.length);
        this.xOff = sHA256Digest.xOff;
    }

    public SHA256Digest(byte[] byArray) {
        super(byArray);
        this.H1 = Pack.bigEndianToInt(byArray, 16);
        this.H2 = Pack.bigEndianToInt(byArray, 20);
        this.H3 = Pack.bigEndianToInt(byArray, 24);
        this.H4 = Pack.bigEndianToInt(byArray, 28);
        this.H5 = Pack.bigEndianToInt(byArray, 32);
        this.H6 = Pack.bigEndianToInt(byArray, 36);
        this.H7 = Pack.bigEndianToInt(byArray, 40);
        this.H8 = Pack.bigEndianToInt(byArray, 44);
        this.xOff = Pack.bigEndianToInt(byArray, 48);
        for (int i = 0; i != this.xOff; ++i) {
            this.X[i] = Pack.bigEndianToInt(byArray, 52 + i * 4);
        }
    }

    public String getAlgorithmName() {
        return "SHA-256";
    }

    public int getDigestSize() {
        return 32;
    }

    protected void processWord(byte[] byArray, int n) {
        int n2 = byArray[n] << 24;
        n2 |= (byArray[++n] & 0xFF) << 16;
        n2 |= (byArray[++n] & 0xFF) << 8;
        this.X[this.xOff] = n2 |= byArray[++n] & 0xFF;
        if (++this.xOff == 16) {
            this.processBlock();
        }
    }

    protected void processLength(long l) {
        if (this.xOff > 14) {
            this.processBlock();
        }
        this.X[14] = (int)(l >>> 32);
        this.X[15] = (int)(l & 0xFFFFFFFFFFFFFFFFL);
    }

    public int doFinal(byte[] byArray, int n) {
        this.finish();
        Pack.intToBigEndian(this.H1, byArray, n);
        Pack.intToBigEndian(this.H2, byArray, n + 4);
        Pack.intToBigEndian(this.H3, byArray, n + 8);
        Pack.intToBigEndian(this.H4, byArray, n + 12);
        Pack.intToBigEndian(this.H5, byArray, n + 16);
        Pack.intToBigEndian(this.H6, byArray, n + 20);
        Pack.intToBigEndian(this.H7, byArray, n + 24);
        Pack.intToBigEndian(this.H8, byArray, n + 28);
        this.reset();
        return 32;
    }

    public void reset() {
        super.reset();
        this.H1 = 1779033703;
        this.H2 = -1150833019;
        this.H3 = 1013904242;
        this.H4 = -1521486534;
        this.H5 = 1359893119;
        this.H6 = -1694144372;
        this.H7 = 528734635;
        this.H8 = 1541459225;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; ++i) {
            this.X[i] = 0;
        }
    }

    protected void processBlock() {
        int n;
        int n2;
        for (n2 = 16; n2 <= 63; ++n2) {
            this.X[n2] = SHA256Digest.Theta1(this.X[n2 - 2]) + this.X[n2 - 7] + SHA256Digest.Theta0(this.X[n2 - 15]) + this.X[n2 - 16];
        }
        n2 = this.H1;
        int n3 = this.H2;
        int n4 = this.H3;
        int n5 = this.H4;
        int n6 = this.H5;
        int n7 = this.H6;
        int n8 = this.H7;
        int n9 = this.H8;
        int n10 = 0;
        for (n = 0; n < 8; ++n) {
            n5 += (n9 += SHA256Digest.Sum1(n6) + SHA256Digest.Ch(n6, n7, n8) + K[n10] + this.X[n10]);
            n9 += SHA256Digest.Sum0(n2) + SHA256Digest.Maj(n2, n3, n4);
            n4 += (n8 += SHA256Digest.Sum1(n5) + SHA256Digest.Ch(n5, n6, n7) + K[++n10] + this.X[n10]);
            n8 += SHA256Digest.Sum0(n9) + SHA256Digest.Maj(n9, n2, n3);
            n3 += (n7 += SHA256Digest.Sum1(n4) + SHA256Digest.Ch(n4, n5, n6) + K[++n10] + this.X[n10]);
            n7 += SHA256Digest.Sum0(n8) + SHA256Digest.Maj(n8, n9, n2);
            n2 += (n6 += SHA256Digest.Sum1(n3) + SHA256Digest.Ch(n3, n4, n5) + K[++n10] + this.X[n10]);
            n6 += SHA256Digest.Sum0(n7) + SHA256Digest.Maj(n7, n8, n9);
            n9 += (n5 += SHA256Digest.Sum1(n2) + SHA256Digest.Ch(n2, n3, n4) + K[++n10] + this.X[n10]);
            n5 += SHA256Digest.Sum0(n6) + SHA256Digest.Maj(n6, n7, n8);
            n8 += (n4 += SHA256Digest.Sum1(n9) + SHA256Digest.Ch(n9, n2, n3) + K[++n10] + this.X[n10]);
            n4 += SHA256Digest.Sum0(n5) + SHA256Digest.Maj(n5, n6, n7);
            n7 += (n3 += SHA256Digest.Sum1(n8) + SHA256Digest.Ch(n8, n9, n2) + K[++n10] + this.X[n10]);
            n3 += SHA256Digest.Sum0(n4) + SHA256Digest.Maj(n4, n5, n6);
            n6 += (n2 += SHA256Digest.Sum1(n7) + SHA256Digest.Ch(n7, n8, n9) + K[++n10] + this.X[n10]);
            n2 += SHA256Digest.Sum0(n3) + SHA256Digest.Maj(n3, n4, n5);
            ++n10;
        }
        this.H1 += n2;
        this.H2 += n3;
        this.H3 += n4;
        this.H4 += n5;
        this.H5 += n6;
        this.H6 += n7;
        this.H7 += n8;
        this.H8 += n9;
        this.xOff = 0;
        for (n = 0; n < 16; ++n) {
            this.X[n] = 0;
        }
    }

    private static int Ch(int n, int n2, int n3) {
        return n & n2 ^ ~n & n3;
    }

    private static int Maj(int n, int n2, int n3) {
        return n & n2 | n3 & (n ^ n2);
    }

    private static int Sum0(int n) {
        return (n >>> 2 | n << 30) ^ (n >>> 13 | n << 19) ^ (n >>> 22 | n << 10);
    }

    private static int Sum1(int n) {
        return (n >>> 6 | n << 26) ^ (n >>> 11 | n << 21) ^ (n >>> 25 | n << 7);
    }

    private static int Theta0(int n) {
        return (n >>> 7 | n << 25) ^ (n >>> 18 | n << 14) ^ n >>> 3;
    }

    private static int Theta1(int n) {
        return (n >>> 17 | n << 15) ^ (n >>> 19 | n << 13) ^ n >>> 10;
    }

    public Memoable copy() {
        return new SHA256Digest(this);
    }

    public void reset(Memoable memoable) {
        SHA256Digest sHA256Digest = (SHA256Digest)memoable;
        this.copyIn(sHA256Digest);
    }

    public byte[] getEncodedState() {
        byte[] byArray = new byte[52 + this.xOff * 4];
        super.populateState(byArray);
        Pack.intToBigEndian(this.H1, byArray, 16);
        Pack.intToBigEndian(this.H2, byArray, 20);
        Pack.intToBigEndian(this.H3, byArray, 24);
        Pack.intToBigEndian(this.H4, byArray, 28);
        Pack.intToBigEndian(this.H5, byArray, 32);
        Pack.intToBigEndian(this.H6, byArray, 36);
        Pack.intToBigEndian(this.H7, byArray, 40);
        Pack.intToBigEndian(this.H8, byArray, 44);
        Pack.intToBigEndian(this.xOff, byArray, 48);
        for (int i = 0; i != this.xOff; ++i) {
            Pack.intToBigEndian(this.X[i], byArray, 52 + i * 4);
        }
        return byArray;
    }
}

