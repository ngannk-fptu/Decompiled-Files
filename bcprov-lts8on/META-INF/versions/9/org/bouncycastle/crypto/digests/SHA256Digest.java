/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import java.io.Serializable;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.digests.SHA256NativeDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SHA256Digest
extends GeneralDigest
implements SavableDigest {
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

    public static SavableDigest newInstance() {
        if (CryptoServicesRegistrar.hasEnabledService("SHA2")) {
            return new SHA256NativeDigest();
        }
        return new SHA256Digest();
    }

    public static SavableDigest newInstance(CryptoServicePurpose purpose) {
        if (CryptoServicesRegistrar.hasEnabledService("SHA2")) {
            return new SHA256NativeDigest(purpose);
        }
        return new SHA256Digest(purpose);
    }

    public static SavableDigest newInstance(Digest digest) {
        if (digest instanceof SHA256Digest) {
            return new SHA256Digest((SHA256Digest)digest);
        }
        if (digest instanceof SHA256NativeDigest && CryptoServicesRegistrar.hasEnabledService("SHA2")) {
            return new SHA256NativeDigest((SHA256NativeDigest)digest);
        }
        throw new IllegalArgumentException("receiver digest not available for input type " + (Serializable)(digest != null ? digest.getClass() : "null"));
    }

    public static SavableDigest newInstance(byte[] encoded, int offset) {
        if (CryptoServicesRegistrar.hasEnabledService("SHA2")) {
            SHA256NativeDigest sha256 = new SHA256NativeDigest();
            sha256.restoreFullState(encoded, offset);
            return sha256;
        }
        return new SHA256Digest(encoded);
    }

    public SHA256Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public SHA256Digest(CryptoServicePurpose purpose) {
        super(purpose);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.reset();
    }

    public SHA256Digest(SHA256Digest t) {
        super(t);
        this.copyIn(t);
    }

    private void copyIn(SHA256Digest t) {
        super.copyIn(t);
        this.H1 = t.H1;
        this.H2 = t.H2;
        this.H3 = t.H3;
        this.H4 = t.H4;
        this.H5 = t.H5;
        this.H6 = t.H6;
        this.H7 = t.H7;
        this.H8 = t.H8;
        System.arraycopy(t.X, 0, this.X, 0, t.X.length);
        this.xOff = t.xOff;
    }

    public SHA256Digest(byte[] encodedState) {
        super(encodedState);
        this.H1 = Pack.bigEndianToInt(encodedState, 16);
        this.H2 = Pack.bigEndianToInt(encodedState, 20);
        this.H3 = Pack.bigEndianToInt(encodedState, 24);
        this.H4 = Pack.bigEndianToInt(encodedState, 28);
        this.H5 = Pack.bigEndianToInt(encodedState, 32);
        this.H6 = Pack.bigEndianToInt(encodedState, 36);
        this.H7 = Pack.bigEndianToInt(encodedState, 40);
        this.H8 = Pack.bigEndianToInt(encodedState, 44);
        this.xOff = Pack.bigEndianToInt(encodedState, 48);
        for (int i = 0; i != this.xOff; ++i) {
            this.X[i] = Pack.bigEndianToInt(encodedState, 52 + i * 4);
        }
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-256";
    }

    @Override
    public int getDigestSize() {
        return 32;
    }

    @Override
    protected void processWord(byte[] in, int inOff) {
        this.X[this.xOff] = Pack.bigEndianToInt(in, inOff);
        if (++this.xOff == 16) {
            this.processBlock();
        }
    }

    @Override
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            this.processBlock();
        }
        this.X[14] = (int)(bitLength >>> 32);
        this.X[15] = (int)(bitLength & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.finish();
        Pack.intToBigEndian(this.H1, out, outOff);
        Pack.intToBigEndian(this.H2, out, outOff + 4);
        Pack.intToBigEndian(this.H3, out, outOff + 8);
        Pack.intToBigEndian(this.H4, out, outOff + 12);
        Pack.intToBigEndian(this.H5, out, outOff + 16);
        Pack.intToBigEndian(this.H6, out, outOff + 20);
        Pack.intToBigEndian(this.H7, out, outOff + 24);
        Pack.intToBigEndian(this.H8, out, outOff + 28);
        this.reset();
        return 32;
    }

    @Override
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

    @Override
    protected void processBlock() {
        int i;
        for (int t = 16; t <= 63; ++t) {
            this.X[t] = SHA256Digest.Theta1(this.X[t - 2]) + this.X[t - 7] + SHA256Digest.Theta0(this.X[t - 15]) + this.X[t - 16];
        }
        int a = this.H1;
        int b = this.H2;
        int c = this.H3;
        int d = this.H4;
        int e = this.H5;
        int f = this.H6;
        int g = this.H7;
        int h = this.H8;
        int t = 0;
        for (i = 0; i < 8; ++i) {
            d += (h += SHA256Digest.Sum1(e) + SHA256Digest.Ch(e, f, g) + K[t] + this.X[t]);
            h += SHA256Digest.Sum0(a) + SHA256Digest.Maj(a, b, c);
            c += (g += SHA256Digest.Sum1(d) + SHA256Digest.Ch(d, e, f) + K[++t] + this.X[t]);
            g += SHA256Digest.Sum0(h) + SHA256Digest.Maj(h, a, b);
            b += (f += SHA256Digest.Sum1(c) + SHA256Digest.Ch(c, d, e) + K[++t] + this.X[t]);
            f += SHA256Digest.Sum0(g) + SHA256Digest.Maj(g, h, a);
            a += (e += SHA256Digest.Sum1(b) + SHA256Digest.Ch(b, c, d) + K[++t] + this.X[t]);
            e += SHA256Digest.Sum0(f) + SHA256Digest.Maj(f, g, h);
            h += (d += SHA256Digest.Sum1(a) + SHA256Digest.Ch(a, b, c) + K[++t] + this.X[t]);
            d += SHA256Digest.Sum0(e) + SHA256Digest.Maj(e, f, g);
            g += (c += SHA256Digest.Sum1(h) + SHA256Digest.Ch(h, a, b) + K[++t] + this.X[t]);
            c += SHA256Digest.Sum0(d) + SHA256Digest.Maj(d, e, f);
            f += (b += SHA256Digest.Sum1(g) + SHA256Digest.Ch(g, h, a) + K[++t] + this.X[t]);
            b += SHA256Digest.Sum0(c) + SHA256Digest.Maj(c, d, e);
            e += (a += SHA256Digest.Sum1(f) + SHA256Digest.Ch(f, g, h) + K[++t] + this.X[t]);
            a += SHA256Digest.Sum0(b) + SHA256Digest.Maj(b, c, d);
            ++t;
        }
        this.H1 += a;
        this.H2 += b;
        this.H3 += c;
        this.H4 += d;
        this.H5 += e;
        this.H6 += f;
        this.H7 += g;
        this.H8 += h;
        this.xOff = 0;
        for (i = 0; i < 16; ++i) {
            this.X[i] = 0;
        }
    }

    private static int Ch(int x, int y, int z) {
        return x & y ^ ~x & z;
    }

    private static int Maj(int x, int y, int z) {
        return x & y | z & (x ^ y);
    }

    private static int Sum0(int x) {
        return (x >>> 2 | x << 30) ^ (x >>> 13 | x << 19) ^ (x >>> 22 | x << 10);
    }

    private static int Sum1(int x) {
        return (x >>> 6 | x << 26) ^ (x >>> 11 | x << 21) ^ (x >>> 25 | x << 7);
    }

    private static int Theta0(int x) {
        return (x >>> 7 | x << 25) ^ (x >>> 18 | x << 14) ^ x >>> 3;
    }

    private static int Theta1(int x) {
        return (x >>> 17 | x << 15) ^ (x >>> 19 | x << 13) ^ x >>> 10;
    }

    @Override
    public Memoable copy() {
        return new SHA256Digest(this);
    }

    @Override
    public void reset(Memoable other) {
        SHA256Digest d = (SHA256Digest)other;
        this.copyIn(d);
    }

    @Override
    public byte[] getEncodedState() {
        byte[] state = new byte[52 + this.xOff * 4 + 1];
        super.populateState(state);
        Pack.intToBigEndian(this.H1, state, 16);
        Pack.intToBigEndian(this.H2, state, 20);
        Pack.intToBigEndian(this.H3, state, 24);
        Pack.intToBigEndian(this.H4, state, 28);
        Pack.intToBigEndian(this.H5, state, 32);
        Pack.intToBigEndian(this.H6, state, 36);
        Pack.intToBigEndian(this.H7, state, 40);
        Pack.intToBigEndian(this.H8, state, 44);
        Pack.intToBigEndian(this.xOff, state, 48);
        for (int i = 0; i != this.xOff; ++i) {
            Pack.intToBigEndian(this.X[i], state, 52 + i * 4);
        }
        state[state.length - 1] = (byte)this.purpose.ordinal();
        return state;
    }

    @Override
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, 256, this.purpose);
    }

    public String toString() {
        return "SHA256[Java]()";
    }
}

