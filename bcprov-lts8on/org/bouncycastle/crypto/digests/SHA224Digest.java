/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.EncodableDigest;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA224Digest
extends GeneralDigest
implements EncodableDigest {
    private static final int DIGEST_LENGTH = 28;
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

    public SHA224Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public SHA224Digest(CryptoServicePurpose purpose) {
        super(purpose);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.reset();
    }

    public SHA224Digest(SHA224Digest t) {
        super(t);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.doCopy(t);
    }

    private void doCopy(SHA224Digest t) {
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

    public SHA224Digest(byte[] encodedState) {
        super(encodedState);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
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
        return "SHA-224";
    }

    @Override
    public int getDigestSize() {
        return 28;
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
        this.reset();
        return 28;
    }

    @Override
    public void reset() {
        super.reset();
        this.H1 = -1056596264;
        this.H2 = 914150663;
        this.H3 = 812702999;
        this.H4 = -150054599;
        this.H5 = -4191439;
        this.H6 = 1750603025;
        this.H7 = 1694076839;
        this.H8 = -1090891868;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; ++i) {
            this.X[i] = 0;
        }
    }

    @Override
    protected void processBlock() {
        int i;
        for (int t = 16; t <= 63; ++t) {
            this.X[t] = this.Theta1(this.X[t - 2]) + this.X[t - 7] + this.Theta0(this.X[t - 15]) + this.X[t - 16];
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
            d += (h += this.Sum1(e) + this.Ch(e, f, g) + K[t] + this.X[t]);
            h += this.Sum0(a) + this.Maj(a, b, c);
            c += (g += this.Sum1(d) + this.Ch(d, e, f) + K[++t] + this.X[t]);
            g += this.Sum0(h) + this.Maj(h, a, b);
            b += (f += this.Sum1(c) + this.Ch(c, d, e) + K[++t] + this.X[t]);
            f += this.Sum0(g) + this.Maj(g, h, a);
            a += (e += this.Sum1(b) + this.Ch(b, c, d) + K[++t] + this.X[t]);
            e += this.Sum0(f) + this.Maj(f, g, h);
            h += (d += this.Sum1(a) + this.Ch(a, b, c) + K[++t] + this.X[t]);
            d += this.Sum0(e) + this.Maj(e, f, g);
            g += (c += this.Sum1(h) + this.Ch(h, a, b) + K[++t] + this.X[t]);
            c += this.Sum0(d) + this.Maj(d, e, f);
            f += (b += this.Sum1(g) + this.Ch(g, h, a) + K[++t] + this.X[t]);
            b += this.Sum0(c) + this.Maj(c, d, e);
            e += (a += this.Sum1(f) + this.Ch(f, g, h) + K[++t] + this.X[t]);
            a += this.Sum0(b) + this.Maj(b, c, d);
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

    private int Ch(int x, int y, int z) {
        return x & y ^ ~x & z;
    }

    private int Maj(int x, int y, int z) {
        return x & y ^ x & z ^ y & z;
    }

    private int Sum0(int x) {
        return (x >>> 2 | x << 30) ^ (x >>> 13 | x << 19) ^ (x >>> 22 | x << 10);
    }

    private int Sum1(int x) {
        return (x >>> 6 | x << 26) ^ (x >>> 11 | x << 21) ^ (x >>> 25 | x << 7);
    }

    private int Theta0(int x) {
        return (x >>> 7 | x << 25) ^ (x >>> 18 | x << 14) ^ x >>> 3;
    }

    private int Theta1(int x) {
        return (x >>> 17 | x << 15) ^ (x >>> 19 | x << 13) ^ x >>> 10;
    }

    @Override
    public Memoable copy() {
        return new SHA224Digest(this);
    }

    @Override
    public void reset(Memoable other) {
        SHA224Digest d = (SHA224Digest)other;
        this.doCopy(d);
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
        return Utils.getDefaultProperties(this, 192, this.purpose);
    }
}

