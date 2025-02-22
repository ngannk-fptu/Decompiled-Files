/*
 * Decompiled with CFR 0.152.
 */
package mssql.security.provider;

public class MD4 {
    private final byte[] xBuf = new byte[4];
    private int xBufOff;
    private long byteCount;
    private static final int DIGEST_LENGTH = 16;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int[] X = new int[16];
    private int xOff;
    private static final int S11 = 3;
    private static final int S12 = 7;
    private static final int S13 = 11;
    private static final int S14 = 19;
    private static final int S21 = 3;
    private static final int S22 = 5;
    private static final int S23 = 9;
    private static final int S24 = 13;
    private static final int S31 = 3;
    private static final int S32 = 9;
    private static final int S33 = 11;
    private static final int S34 = 15;

    public MD4() {
        this.reset();
    }

    public MD4(MD4 t) {
        System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
        this.xBufOff = t.xBufOff;
        this.byteCount = t.byteCount;
        this.copyIn(t);
    }

    private void copyIn(MD4 t) {
        System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
        this.xBufOff = t.xBufOff;
        this.byteCount = t.byteCount;
        this.H1 = t.H1;
        this.H2 = t.H2;
        this.H3 = t.H3;
        this.H4 = t.H4;
        System.arraycopy(t.X, 0, this.X, 0, t.X.length);
        this.xOff = t.xOff;
    }

    public String getAlgorithmName() {
        return "MD4";
    }

    public int getDigestSize() {
        return 16;
    }

    protected void processWord(byte[] in, int inOff) {
        this.X[this.xOff++] = in[inOff] & 0xFF | (in[inOff + 1] & 0xFF) << 8 | (in[inOff + 2] & 0xFF) << 16 | (in[inOff + 3] & 0xFF) << 24;
        if (this.xOff == 16) {
            this.processBlock();
        }
    }

    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            this.processBlock();
        }
        this.X[14] = (int)(bitLength & 0xFFFFFFFFFFFFFFFFL);
        this.X[15] = (int)(bitLength >>> 32);
    }

    private void unpackWord(int word, byte[] out, int outOff) {
        out[outOff] = (byte)word;
        out[outOff + 1] = (byte)(word >>> 8);
        out[outOff + 2] = (byte)(word >>> 16);
        out[outOff + 3] = (byte)(word >>> 24);
    }

    public void update(byte in) {
        this.xBuf[this.xBufOff++] = in;
        if (this.xBufOff == this.xBuf.length) {
            this.processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }
        ++this.byteCount;
    }

    public void update(byte[] in, int inOff, int len) {
        len = Math.max(0, len);
        int i = 0;
        if (this.xBufOff != 0) {
            while (i < len) {
                this.xBuf[this.xBufOff++] = in[inOff + i++];
                if (this.xBufOff != 4) continue;
                this.processWord(this.xBuf, 0);
                this.xBufOff = 0;
                break;
            }
        }
        int limit = (len - i & 0xFFFFFFFC) + i;
        while (i < limit) {
            this.processWord(in, inOff + i);
            i += 4;
        }
        while (i < len) {
            this.xBuf[this.xBufOff++] = in[inOff + i++];
        }
        this.byteCount += (long)len;
    }

    public void finish() {
        long bitLength = this.byteCount << 3;
        this.update((byte)-128);
        while (this.xBufOff != 0) {
            this.update((byte)0);
        }
        this.processLength(bitLength);
        this.processBlock();
    }

    public int doFinal(byte[] out, int outOff) {
        this.finish();
        this.unpackWord(this.H1, out, outOff);
        this.unpackWord(this.H2, out, outOff + 4);
        this.unpackWord(this.H3, out, outOff + 8);
        this.unpackWord(this.H4, out, outOff + 12);
        this.reset();
        return 16;
    }

    public void reset() {
        int i;
        this.byteCount = 0L;
        this.xBufOff = 0;
        for (i = 0; i < this.xBuf.length; ++i) {
            this.xBuf[i] = 0;
        }
        this.H1 = 1732584193;
        this.H2 = -271733879;
        this.H3 = -1732584194;
        this.H4 = 271733878;
        this.xOff = 0;
        for (i = 0; i != this.X.length; ++i) {
            this.X[i] = 0;
        }
    }

    private int rotateLeft(int x, int n) {
        return x << n | x >>> 32 - n;
    }

    private int F(int u, int v, int w) {
        return u & v | ~u & w;
    }

    private int G(int u, int v, int w) {
        return u & v | u & w | v & w;
    }

    private int H(int u, int v, int w) {
        return u ^ v ^ w;
    }

    protected void processBlock() {
        int a = this.H1;
        int b = this.H2;
        int c = this.H3;
        int d = this.H4;
        a = this.rotateLeft(a + this.F(b, c, d) + this.X[0], 3);
        d = this.rotateLeft(d + this.F(a, b, c) + this.X[1], 7);
        c = this.rotateLeft(c + this.F(d, a, b) + this.X[2], 11);
        b = this.rotateLeft(b + this.F(c, d, a) + this.X[3], 19);
        a = this.rotateLeft(a + this.F(b, c, d) + this.X[4], 3);
        d = this.rotateLeft(d + this.F(a, b, c) + this.X[5], 7);
        c = this.rotateLeft(c + this.F(d, a, b) + this.X[6], 11);
        b = this.rotateLeft(b + this.F(c, d, a) + this.X[7], 19);
        a = this.rotateLeft(a + this.F(b, c, d) + this.X[8], 3);
        d = this.rotateLeft(d + this.F(a, b, c) + this.X[9], 7);
        c = this.rotateLeft(c + this.F(d, a, b) + this.X[10], 11);
        b = this.rotateLeft(b + this.F(c, d, a) + this.X[11], 19);
        a = this.rotateLeft(a + this.F(b, c, d) + this.X[12], 3);
        d = this.rotateLeft(d + this.F(a, b, c) + this.X[13], 7);
        c = this.rotateLeft(c + this.F(d, a, b) + this.X[14], 11);
        b = this.rotateLeft(b + this.F(c, d, a) + this.X[15], 19);
        a = this.rotateLeft(a + this.G(b, c, d) + this.X[0] + 1518500249, 3);
        d = this.rotateLeft(d + this.G(a, b, c) + this.X[4] + 1518500249, 5);
        c = this.rotateLeft(c + this.G(d, a, b) + this.X[8] + 1518500249, 9);
        b = this.rotateLeft(b + this.G(c, d, a) + this.X[12] + 1518500249, 13);
        a = this.rotateLeft(a + this.G(b, c, d) + this.X[1] + 1518500249, 3);
        d = this.rotateLeft(d + this.G(a, b, c) + this.X[5] + 1518500249, 5);
        c = this.rotateLeft(c + this.G(d, a, b) + this.X[9] + 1518500249, 9);
        b = this.rotateLeft(b + this.G(c, d, a) + this.X[13] + 1518500249, 13);
        a = this.rotateLeft(a + this.G(b, c, d) + this.X[2] + 1518500249, 3);
        d = this.rotateLeft(d + this.G(a, b, c) + this.X[6] + 1518500249, 5);
        c = this.rotateLeft(c + this.G(d, a, b) + this.X[10] + 1518500249, 9);
        b = this.rotateLeft(b + this.G(c, d, a) + this.X[14] + 1518500249, 13);
        a = this.rotateLeft(a + this.G(b, c, d) + this.X[3] + 1518500249, 3);
        d = this.rotateLeft(d + this.G(a, b, c) + this.X[7] + 1518500249, 5);
        c = this.rotateLeft(c + this.G(d, a, b) + this.X[11] + 1518500249, 9);
        b = this.rotateLeft(b + this.G(c, d, a) + this.X[15] + 1518500249, 13);
        a = this.rotateLeft(a + this.H(b, c, d) + this.X[0] + 1859775393, 3);
        d = this.rotateLeft(d + this.H(a, b, c) + this.X[8] + 1859775393, 9);
        c = this.rotateLeft(c + this.H(d, a, b) + this.X[4] + 1859775393, 11);
        b = this.rotateLeft(b + this.H(c, d, a) + this.X[12] + 1859775393, 15);
        a = this.rotateLeft(a + this.H(b, c, d) + this.X[2] + 1859775393, 3);
        d = this.rotateLeft(d + this.H(a, b, c) + this.X[10] + 1859775393, 9);
        c = this.rotateLeft(c + this.H(d, a, b) + this.X[6] + 1859775393, 11);
        b = this.rotateLeft(b + this.H(c, d, a) + this.X[14] + 1859775393, 15);
        a = this.rotateLeft(a + this.H(b, c, d) + this.X[1] + 1859775393, 3);
        d = this.rotateLeft(d + this.H(a, b, c) + this.X[9] + 1859775393, 9);
        c = this.rotateLeft(c + this.H(d, a, b) + this.X[5] + 1859775393, 11);
        b = this.rotateLeft(b + this.H(c, d, a) + this.X[13] + 1859775393, 15);
        a = this.rotateLeft(a + this.H(b, c, d) + this.X[3] + 1859775393, 3);
        d = this.rotateLeft(d + this.H(a, b, c) + this.X[11] + 1859775393, 9);
        c = this.rotateLeft(c + this.H(d, a, b) + this.X[7] + 1859775393, 11);
        b = this.rotateLeft(b + this.H(c, d, a) + this.X[15] + 1859775393, 15);
        this.H1 += a;
        this.H2 += b;
        this.H3 += c;
        this.H4 += d;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; ++i) {
            this.X[i] = 0;
        }
    }
}

