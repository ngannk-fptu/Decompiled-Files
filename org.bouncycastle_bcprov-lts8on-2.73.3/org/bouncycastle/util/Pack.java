/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class Pack {
    public static short bigEndianToShort(byte[] bs, int off) {
        int n = (bs[off] & 0xFF) << 8;
        return (short)(n |= bs[++off] & 0xFF);
    }

    public static int bigEndianToInt(byte[] bs, int off) {
        int n = bs[off] << 24;
        n |= (bs[++off] & 0xFF) << 16;
        n |= (bs[++off] & 0xFF) << 8;
        return n |= bs[++off] & 0xFF;
    }

    public static void bigEndianToInt(byte[] bs, int off, int[] ns) {
        for (int i = 0; i < ns.length; ++i) {
            ns[i] = Pack.bigEndianToInt(bs, off);
            off += 4;
        }
    }

    public static void bigEndianToInt(byte[] bs, int off, int[] ns, int nsOff, int nsLen) {
        for (int i = 0; i < nsLen; ++i) {
            ns[nsOff + i] = Pack.bigEndianToInt(bs, off);
            off += 4;
        }
    }

    public static byte[] intToBigEndian(int n) {
        byte[] bs = new byte[4];
        Pack.intToBigEndian(n, bs, 0);
        return bs;
    }

    public static void intToBigEndian(int n, byte[] bs, int off) {
        bs[off] = (byte)(n >>> 24);
        bs[++off] = (byte)(n >>> 16);
        bs[++off] = (byte)(n >>> 8);
        bs[++off] = (byte)n;
    }

    public static byte[] intToBigEndian(int[] ns) {
        byte[] bs = new byte[4 * ns.length];
        Pack.intToBigEndian(ns, bs, 0);
        return bs;
    }

    public static void intToBigEndian(int[] ns, byte[] bs, int off) {
        for (int i = 0; i < ns.length; ++i) {
            Pack.intToBigEndian(ns[i], bs, off);
            off += 4;
        }
    }

    public static void intToBigEndian(int[] ns, int nsOff, int nsLen, byte[] bs, int bsOff) {
        for (int i = 0; i < nsLen; ++i) {
            Pack.intToBigEndian(ns[nsOff + i], bs, bsOff);
            bsOff += 4;
        }
    }

    public static long bigEndianToLong(byte[] bs, int off) {
        int hi = Pack.bigEndianToInt(bs, off);
        int lo = Pack.bigEndianToInt(bs, off + 4);
        return ((long)hi & 0xFFFFFFFFL) << 32 | (long)lo & 0xFFFFFFFFL;
    }

    public static void bigEndianToLong(byte[] bs, int off, long[] ns) {
        for (int i = 0; i < ns.length; ++i) {
            ns[i] = Pack.bigEndianToLong(bs, off);
            off += 8;
        }
    }

    public static void bigEndianToLong(byte[] bs, int bsOff, long[] ns, int nsOff, int nsLen) {
        for (int i = 0; i < nsLen; ++i) {
            ns[nsOff + i] = Pack.bigEndianToLong(bs, bsOff);
            bsOff += 8;
        }
    }

    public static byte[] longToBigEndian(long n) {
        byte[] bs = new byte[8];
        Pack.longToBigEndian(n, bs, 0);
        return bs;
    }

    public static void longToBigEndian(long n, byte[] bs, int off) {
        Pack.intToBigEndian((int)(n >>> 32), bs, off);
        Pack.intToBigEndian((int)(n & 0xFFFFFFFFL), bs, off + 4);
    }

    public static byte[] longToBigEndian(long[] ns) {
        byte[] bs = new byte[8 * ns.length];
        Pack.longToBigEndian(ns, bs, 0);
        return bs;
    }

    public static void longToBigEndian(long[] ns, byte[] bs, int off) {
        for (int i = 0; i < ns.length; ++i) {
            Pack.longToBigEndian(ns[i], bs, off);
            off += 8;
        }
    }

    public static void longToBigEndian(long[] ns, int nsOff, int nsLen, byte[] bs, int bsOff) {
        for (int i = 0; i < nsLen; ++i) {
            Pack.longToBigEndian(ns[nsOff + i], bs, bsOff);
            bsOff += 8;
        }
    }

    public static short littleEndianToShort(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        return (short)(n |= (bs[++off] & 0xFF) << 8);
    }

    public static int littleEndianToInt(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        n |= (bs[++off] & 0xFF) << 8;
        n |= (bs[++off] & 0xFF) << 16;
        return n |= bs[++off] << 24;
    }

    public static int littleEndianToInt_High(byte[] bs, int off, int len) {
        return Pack.littleEndianToInt_Low(bs, off, len) << (4 - len << 3);
    }

    public static int littleEndianToInt_Low(byte[] bs, int off, int len) {
        int result = bs[off] & 0xFF;
        int pos = 0;
        for (int i = 1; i < len; ++i) {
            result |= (bs[off + i] & 0xFF) << (pos += 8);
        }
        return result;
    }

    public static void littleEndianToInt(byte[] bs, int off, int[] ns) {
        for (int i = 0; i < ns.length; ++i) {
            ns[i] = Pack.littleEndianToInt(bs, off);
            off += 4;
        }
    }

    public static void littleEndianToInt(byte[] bs, int bOff, int[] ns, int nOff, int count) {
        for (int i = 0; i < count; ++i) {
            ns[nOff + i] = Pack.littleEndianToInt(bs, bOff);
            bOff += 4;
        }
    }

    public static int[] littleEndianToInt(byte[] bs, int off, int count) {
        int[] ns = new int[count];
        for (int i = 0; i < ns.length; ++i) {
            ns[i] = Pack.littleEndianToInt(bs, off);
            off += 4;
        }
        return ns;
    }

    public static byte[] shortToLittleEndian(short n) {
        byte[] bs = new byte[2];
        Pack.shortToLittleEndian(n, bs, 0);
        return bs;
    }

    public static void shortToLittleEndian(short n, byte[] bs, int off) {
        bs[off] = (byte)n;
        bs[++off] = (byte)(n >>> 8);
    }

    public static byte[] shortToBigEndian(short n) {
        byte[] r = new byte[2];
        Pack.shortToBigEndian(n, r, 0);
        return r;
    }

    public static void shortToBigEndian(short n, byte[] bs, int off) {
        bs[off] = (byte)(n >>> 8);
        bs[++off] = (byte)n;
    }

    public static byte[] intToLittleEndian(int n) {
        byte[] bs = new byte[4];
        Pack.intToLittleEndian(n, bs, 0);
        return bs;
    }

    public static void intToLittleEndian(int n, byte[] bs, int off) {
        bs[off] = (byte)n;
        bs[++off] = (byte)(n >>> 8);
        bs[++off] = (byte)(n >>> 16);
        bs[++off] = (byte)(n >>> 24);
    }

    public static byte[] intToLittleEndian(int[] ns) {
        byte[] bs = new byte[4 * ns.length];
        Pack.intToLittleEndian(ns, bs, 0);
        return bs;
    }

    public static void intToLittleEndian(int[] ns, byte[] bs, int off) {
        for (int i = 0; i < ns.length; ++i) {
            Pack.intToLittleEndian(ns[i], bs, off);
            off += 4;
        }
    }

    public static void intToLittleEndian(int[] ns, int nsOff, int nsLen, byte[] bs, int bsOff) {
        for (int i = 0; i < nsLen; ++i) {
            Pack.intToLittleEndian(ns[nsOff + i], bs, bsOff);
            bsOff += 4;
        }
    }

    public static long littleEndianToLong(byte[] bs, int off) {
        int lo = Pack.littleEndianToInt(bs, off);
        int hi = Pack.littleEndianToInt(bs, off + 4);
        return ((long)hi & 0xFFFFFFFFL) << 32 | (long)lo & 0xFFFFFFFFL;
    }

    public static void littleEndianToLong(byte[] bs, int off, long[] ns) {
        for (int i = 0; i < ns.length; ++i) {
            ns[i] = Pack.littleEndianToLong(bs, off);
            off += 8;
        }
    }

    public static void littleEndianToLong(byte[] bs, int bsOff, long[] ns, int nsOff, int nsLen) {
        for (int i = 0; i < nsLen; ++i) {
            ns[nsOff + i] = Pack.littleEndianToLong(bs, bsOff);
            bsOff += 8;
        }
    }

    public static void longToLittleEndian_High(long n, byte[] bs, int off, int len) {
        int pos = 56;
        bs[off] = (byte)(n >>> pos);
        for (int i = 1; i < len; ++i) {
            bs[off + i] = (byte)(n >>> (pos -= 8));
        }
    }

    public static long littleEndianToLong_High(byte[] bs, int off, int len) {
        return Pack.littleEndianToLong_Low(bs, off, len) << (8 - len << 3);
    }

    public static long littleEndianToLong_Low(byte[] bs, int off, int len) {
        long result = bs[off] & 0xFF;
        for (int i = 1; i < len; ++i) {
            result <<= 8;
            result |= (long)(bs[off + i] & 0xFF);
        }
        return result;
    }

    public static byte[] longToLittleEndian(long n) {
        byte[] bs = new byte[8];
        Pack.longToLittleEndian(n, bs, 0);
        return bs;
    }

    public static void longToLittleEndian(long n, byte[] bs, int off) {
        Pack.intToLittleEndian((int)(n & 0xFFFFFFFFL), bs, off);
        Pack.intToLittleEndian((int)(n >>> 32), bs, off + 4);
    }

    public static byte[] longToLittleEndian(long[] ns) {
        byte[] bs = new byte[8 * ns.length];
        Pack.longToLittleEndian(ns, bs, 0);
        return bs;
    }

    public static void longToLittleEndian(long[] ns, byte[] bs, int off) {
        for (int i = 0; i < ns.length; ++i) {
            Pack.longToLittleEndian(ns[i], bs, off);
            off += 8;
        }
    }

    public static void longToLittleEndian(long[] ns, int nsOff, int nsLen, byte[] bs, int bsOff) {
        for (int i = 0; i < nsLen; ++i) {
            Pack.longToLittleEndian(ns[nsOff + i], bs, bsOff);
            bsOff += 8;
        }
    }
}

