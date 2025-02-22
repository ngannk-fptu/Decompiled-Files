/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.auth;

public final class MD4 {
    private final int[] state;
    private final int[] x;
    private static final int blockSize = 64;
    private final byte[] buffer = new byte[64];
    private int bufOfs;
    private long bytesProcessed;
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
    private static final byte[] padding = new byte[136];

    public MD4() {
        this.state = new int[4];
        this.x = new int[16];
        this.implReset();
    }

    public byte[] digest(byte[] in) {
        this.implReset();
        this.engineUpdate(in, 0, in.length);
        byte[] out = new byte[16];
        this.implDigest(out, 0);
        return out;
    }

    private void implReset() {
        this.state[0] = 1732584193;
        this.state[1] = -271733879;
        this.state[2] = -1732584194;
        this.state[3] = 271733878;
        this.bufOfs = 0;
        this.bytesProcessed = 0L;
    }

    private void implDigest(byte[] out, int ofs) {
        long bitsProcessed = this.bytesProcessed << 3;
        int index = (int)this.bytesProcessed & 0x3F;
        int padLen = index < 56 ? 56 - index : 120 - index;
        this.engineUpdate(padding, 0, padLen);
        this.buffer[56] = (byte)bitsProcessed;
        this.buffer[57] = (byte)(bitsProcessed >> 8);
        this.buffer[58] = (byte)(bitsProcessed >> 16);
        this.buffer[59] = (byte)(bitsProcessed >> 24);
        this.buffer[60] = (byte)(bitsProcessed >> 32);
        this.buffer[61] = (byte)(bitsProcessed >> 40);
        this.buffer[62] = (byte)(bitsProcessed >> 48);
        this.buffer[63] = (byte)(bitsProcessed >> 56);
        this.implCompress(this.buffer, 0);
        for (int i = 0; i < this.state.length; ++i) {
            int x = this.state[i];
            out[ofs++] = (byte)x;
            out[ofs++] = (byte)(x >> 8);
            out[ofs++] = (byte)(x >> 16);
            out[ofs++] = (byte)(x >> 24);
        }
    }

    private void engineUpdate(byte[] b, int ofs, int len) {
        if (len == 0) {
            return;
        }
        if (ofs < 0 || len < 0 || ofs > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (this.bytesProcessed < 0L) {
            this.implReset();
        }
        this.bytesProcessed += (long)len;
        if (this.bufOfs != 0) {
            int n = Math.min(len, 64 - this.bufOfs);
            System.arraycopy(b, ofs, this.buffer, this.bufOfs, n);
            this.bufOfs += n;
            ofs += n;
            len -= n;
            if (this.bufOfs >= 64) {
                this.implCompress(this.buffer, 0);
                this.bufOfs = 0;
            }
        }
        while (len >= 64) {
            this.implCompress(b, ofs);
            len -= 64;
            ofs += 64;
        }
        if (len > 0) {
            System.arraycopy(b, ofs, this.buffer, 0, len);
            this.bufOfs = len;
        }
    }

    private static int FF(int a, int b, int c, int d, int x, int s) {
        return (a += (b & c | ~b & d) + x) << s | a >>> 32 - s;
    }

    private static int GG(int a, int b, int c, int d, int x, int s) {
        return (a += (b & c | b & d | c & d) + x + 1518500249) << s | a >>> 32 - s;
    }

    private static int HH(int a, int b, int c, int d, int x, int s) {
        return (a += (b ^ c ^ d) + x + 1859775393) << s | a >>> 32 - s;
    }

    private void implCompress(byte[] buf, int ofs) {
        for (int xfs = 0; xfs < this.x.length; ++xfs) {
            this.x[xfs] = buf[ofs] & 0xFF | (buf[ofs + 1] & 0xFF) << 8 | (buf[ofs + 2] & 0xFF) << 16 | (buf[ofs + 3] & 0xFF) << 24;
            ofs += 4;
        }
        int a = this.state[0];
        int b = this.state[1];
        int c = this.state[2];
        int d = this.state[3];
        a = MD4.FF(a, b, c, d, this.x[0], 3);
        d = MD4.FF(d, a, b, c, this.x[1], 7);
        c = MD4.FF(c, d, a, b, this.x[2], 11);
        b = MD4.FF(b, c, d, a, this.x[3], 19);
        a = MD4.FF(a, b, c, d, this.x[4], 3);
        d = MD4.FF(d, a, b, c, this.x[5], 7);
        c = MD4.FF(c, d, a, b, this.x[6], 11);
        b = MD4.FF(b, c, d, a, this.x[7], 19);
        a = MD4.FF(a, b, c, d, this.x[8], 3);
        d = MD4.FF(d, a, b, c, this.x[9], 7);
        c = MD4.FF(c, d, a, b, this.x[10], 11);
        b = MD4.FF(b, c, d, a, this.x[11], 19);
        a = MD4.FF(a, b, c, d, this.x[12], 3);
        d = MD4.FF(d, a, b, c, this.x[13], 7);
        c = MD4.FF(c, d, a, b, this.x[14], 11);
        b = MD4.FF(b, c, d, a, this.x[15], 19);
        a = MD4.GG(a, b, c, d, this.x[0], 3);
        d = MD4.GG(d, a, b, c, this.x[4], 5);
        c = MD4.GG(c, d, a, b, this.x[8], 9);
        b = MD4.GG(b, c, d, a, this.x[12], 13);
        a = MD4.GG(a, b, c, d, this.x[1], 3);
        d = MD4.GG(d, a, b, c, this.x[5], 5);
        c = MD4.GG(c, d, a, b, this.x[9], 9);
        b = MD4.GG(b, c, d, a, this.x[13], 13);
        a = MD4.GG(a, b, c, d, this.x[2], 3);
        d = MD4.GG(d, a, b, c, this.x[6], 5);
        c = MD4.GG(c, d, a, b, this.x[10], 9);
        b = MD4.GG(b, c, d, a, this.x[14], 13);
        a = MD4.GG(a, b, c, d, this.x[3], 3);
        d = MD4.GG(d, a, b, c, this.x[7], 5);
        c = MD4.GG(c, d, a, b, this.x[11], 9);
        b = MD4.GG(b, c, d, a, this.x[15], 13);
        a = MD4.HH(a, b, c, d, this.x[0], 3);
        d = MD4.HH(d, a, b, c, this.x[8], 9);
        c = MD4.HH(c, d, a, b, this.x[4], 11);
        b = MD4.HH(b, c, d, a, this.x[12], 15);
        a = MD4.HH(a, b, c, d, this.x[2], 3);
        d = MD4.HH(d, a, b, c, this.x[10], 9);
        c = MD4.HH(c, d, a, b, this.x[6], 11);
        b = MD4.HH(b, c, d, a, this.x[14], 15);
        a = MD4.HH(a, b, c, d, this.x[1], 3);
        d = MD4.HH(d, a, b, c, this.x[9], 9);
        c = MD4.HH(c, d, a, b, this.x[5], 11);
        b = MD4.HH(b, c, d, a, this.x[13], 15);
        a = MD4.HH(a, b, c, d, this.x[3], 3);
        d = MD4.HH(d, a, b, c, this.x[11], 9);
        c = MD4.HH(c, d, a, b, this.x[7], 11);
        b = MD4.HH(b, c, d, a, this.x[15], 15);
        this.state[0] = this.state[0] + a;
        this.state[1] = this.state[1] + b;
        this.state[2] = this.state[2] + c;
        this.state[3] = this.state[3] + d;
    }

    static {
        MD4.padding[0] = -128;
    }
}

