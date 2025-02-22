/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.fpe;

import java.math.BigInteger;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.util.RadixConverter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

class SP80038G {
    static final String FPE_DISABLED = "org.bouncycastle.fpe.disable";
    static final String FF1_DISABLED = "org.bouncycastle.fpe.disable_ff1";
    protected static final int BLOCK_SIZE = 16;
    protected static final double LOG2 = Math.log(2.0);
    protected static final double TWO_TO_96 = Math.pow(2.0, 96.0);

    SP80038G() {
    }

    static byte[] decryptFF1(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak, byte[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, true, radixConverter.getRadix(), buf, off, len);
        int n = len;
        int u = n / 2;
        int v = n - u;
        short[] A = SP80038G.toShort(buf, off, u);
        short[] B = SP80038G.toShort(buf, off + u, v);
        short[] rv = SP80038G.decFF1(cipher, radixConverter, tweak, n, u, v, A, B);
        return SP80038G.toByte(rv);
    }

    static short[] decryptFF1w(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak, short[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, true, radixConverter.getRadix(), buf, off, len);
        int n = len;
        int u = n / 2;
        int v = n - u;
        short[] A = new short[u];
        short[] B = new short[v];
        System.arraycopy(buf, off, A, 0, u);
        System.arraycopy(buf, off + u, B, 0, v);
        return SP80038G.decFF1(cipher, radixConverter, tweak, n, u, v, A, B);
    }

    static short[] decFF1(BlockCipher cipher, RadixConverter radixConverter, byte[] T, int n, int u, int v, short[] A, short[] B) {
        int radix = radixConverter.getRadix();
        int t = T.length;
        int b = SP80038G.calculateB_FF1(radix, v);
        int d = b + 7 & 0xFFFFFFFC;
        byte[] P = SP80038G.calculateP_FF1(radix, (byte)u, n, t);
        BigInteger bigRadix = BigInteger.valueOf(radix);
        BigInteger[] modUV = SP80038G.calculateModUV(bigRadix, u, v);
        int m = u;
        for (int i = 9; i >= 0; --i) {
            BigInteger y = SP80038G.calculateY_FF1(cipher, T, b, d, i, P, A, radixConverter);
            m = n - m;
            BigInteger modulus = modUV[i & 1];
            BigInteger c = radixConverter.fromEncoding(B).subtract(y).mod(modulus);
            short[] C = B;
            B = A;
            A = C;
            radixConverter.toEncoding(c, m, C);
        }
        return Arrays.concatenate(A, B);
    }

    static byte[] decryptFF3(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak64, byte[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, false, radixConverter.getRadix(), buf, off, len);
        if (tweak64.length != 8) {
            throw new IllegalArgumentException();
        }
        return SP80038G.implDecryptFF3(cipher, radixConverter, tweak64, buf, off, len);
    }

    static byte[] decryptFF3_1(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak56, byte[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, false, radixConverter.getRadix(), buf, off, len);
        if (tweak56.length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
        byte[] tweak64 = SP80038G.calculateTweak64_FF3_1(tweak56);
        return SP80038G.implDecryptFF3(cipher, radixConverter, tweak64, buf, off, len);
    }

    static short[] decryptFF3_1w(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak56, short[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, false, radixConverter.getRadix(), buf, off, len);
        if (tweak56.length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
        byte[] tweak64 = SP80038G.calculateTweak64_FF3_1(tweak56);
        return SP80038G.implDecryptFF3w(cipher, radixConverter, tweak64, buf, off, len);
    }

    static byte[] encryptFF1(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak, byte[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, true, radixConverter.getRadix(), buf, off, len);
        int n = len;
        int u = n / 2;
        int v = n - u;
        short[] A = SP80038G.toShort(buf, off, u);
        short[] B = SP80038G.toShort(buf, off + u, v);
        return SP80038G.toByte(SP80038G.encFF1(cipher, radixConverter, tweak, n, u, v, A, B));
    }

    static short[] encryptFF1w(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak, short[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, true, radixConverter.getRadix(), buf, off, len);
        int n = len;
        int u = n / 2;
        int v = n - u;
        short[] A = new short[u];
        short[] B = new short[v];
        System.arraycopy(buf, off, A, 0, u);
        System.arraycopy(buf, off + u, B, 0, v);
        return SP80038G.encFF1(cipher, radixConverter, tweak, n, u, v, A, B);
    }

    private static short[] encFF1(BlockCipher cipher, RadixConverter radixConverter, byte[] T, int n, int u, int v, short[] A, short[] B) {
        int radix = radixConverter.getRadix();
        int t = T.length;
        int b = SP80038G.calculateB_FF1(radix, v);
        int d = b + 7 & 0xFFFFFFFC;
        byte[] P = SP80038G.calculateP_FF1(radix, (byte)u, n, t);
        BigInteger bigRadix = BigInteger.valueOf(radix);
        BigInteger[] modUV = SP80038G.calculateModUV(bigRadix, u, v);
        int m = v;
        for (int i = 0; i < 10; ++i) {
            BigInteger y = SP80038G.calculateY_FF1(cipher, T, b, d, i, P, B, radixConverter);
            m = n - m;
            BigInteger modulus = modUV[i & 1];
            BigInteger num = radixConverter.fromEncoding(A);
            BigInteger c = num.add(y).mod(modulus);
            short[] C = A;
            A = B;
            B = C;
            radixConverter.toEncoding(c, m, C);
        }
        return Arrays.concatenate(A, B);
    }

    static byte[] encryptFF3(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak64, byte[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, false, radixConverter.getRadix(), buf, off, len);
        if (tweak64.length != 8) {
            throw new IllegalArgumentException();
        }
        return SP80038G.implEncryptFF3(cipher, radixConverter, tweak64, buf, off, len);
    }

    static short[] encryptFF3w(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak64, short[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, false, radixConverter.getRadix(), buf, off, len);
        if (tweak64.length != 8) {
            throw new IllegalArgumentException();
        }
        return SP80038G.implEncryptFF3w(cipher, radixConverter, tweak64, buf, off, len);
    }

    static short[] encryptFF3_1w(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak56, short[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, false, radixConverter.getRadix(), buf, off, len);
        if (tweak56.length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
        byte[] tweak64 = SP80038G.calculateTweak64_FF3_1(tweak56);
        return SP80038G.encryptFF3w(cipher, radixConverter, tweak64, buf, off, len);
    }

    static byte[] encryptFF3_1(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak56, byte[] buf, int off, int len) {
        SP80038G.checkArgs(cipher, false, radixConverter.getRadix(), buf, off, len);
        if (tweak56.length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
        byte[] tweak64 = SP80038G.calculateTweak64_FF3_1(tweak56);
        return SP80038G.encryptFF3(cipher, radixConverter, tweak64, buf, off, len);
    }

    protected static int calculateB_FF1(int radix, int v) {
        int powersOfTwo = Integers.numberOfTrailingZeros(radix);
        int bits = powersOfTwo * v;
        int oddPart = radix >>> powersOfTwo;
        if (oddPart != 1) {
            bits += BigInteger.valueOf(oddPart).pow(v).bitLength();
        }
        return (bits + 7) / 8;
    }

    protected static BigInteger[] calculateModUV(BigInteger bigRadix, int u, int v) {
        BigInteger[] modUV;
        modUV = new BigInteger[]{bigRadix.pow(u), modUV[0]};
        if (v != u) {
            modUV[1] = modUV[1].multiply(bigRadix);
        }
        return modUV;
    }

    protected static byte[] calculateP_FF1(int radix, byte uLow, int n, int t) {
        byte[] P = new byte[16];
        P[0] = 1;
        P[1] = 2;
        P[2] = 1;
        P[3] = 0;
        P[4] = (byte)(radix >> 8);
        P[5] = (byte)radix;
        P[6] = 10;
        P[7] = uLow;
        Pack.intToBigEndian(n, P, 8);
        Pack.intToBigEndian(t, P, 12);
        return P;
    }

    protected static byte[] calculateTweak64_FF3_1(byte[] tweak56) {
        byte[] tweak64 = new byte[]{tweak56[0], tweak56[1], tweak56[2], (byte)(tweak56[3] & 0xF0), tweak56[4], tweak56[5], tweak56[6], (byte)(tweak56[3] << 4)};
        return tweak64;
    }

    protected static BigInteger calculateY_FF1(BlockCipher cipher, byte[] T, int b, int d, int round, byte[] P, short[] AB, RadixConverter radixConverter) {
        byte[] R;
        int t = T.length;
        BigInteger numAB = radixConverter.fromEncoding(AB);
        byte[] bytesAB = BigIntegers.asUnsignedByteArray(numAB);
        int zeroes = -(t + b + 1) & 0xF;
        byte[] Q = new byte[t + zeroes + 1 + b];
        System.arraycopy(T, 0, Q, 0, t);
        Q[t + zeroes] = (byte)round;
        System.arraycopy(bytesAB, 0, Q, Q.length - bytesAB.length, bytesAB.length);
        byte[] sBlocks = R = SP80038G.prf(cipher, Arrays.concatenate(P, Q));
        if (d > 16) {
            int sBlocksLen = (d + 16 - 1) / 16;
            sBlocks = new byte[sBlocksLen * 16];
            System.arraycopy(R, 0, sBlocks, 0, 16);
            byte[] uint32 = new byte[4];
            for (int j = 1; j < sBlocksLen; ++j) {
                int sOff = j * 16;
                System.arraycopy(R, 0, sBlocks, sOff, 16);
                Pack.intToBigEndian(j, uint32, 0);
                SP80038G.xor(uint32, 0, sBlocks, sOff + 16 - 4, 4);
                cipher.processBlock(sBlocks, sOff, sBlocks, sOff);
            }
        }
        return SP80038G.num(sBlocks, 0, d);
    }

    protected static BigInteger calculateY_FF3(BlockCipher cipher, byte[] T, int wOff, int round, short[] AB, RadixConverter radixConverter) {
        byte[] P = new byte[16];
        Pack.intToBigEndian(round, P, 0);
        SP80038G.xor(T, wOff, P, 0, 4);
        BigInteger numAB = radixConverter.fromEncoding(AB);
        byte[] bytesAB = BigIntegers.asUnsignedByteArray(numAB);
        if (P.length - bytesAB.length < 4) {
            throw new IllegalStateException("input out of range");
        }
        System.arraycopy(bytesAB, 0, P, P.length - bytesAB.length, bytesAB.length);
        SP80038G.rev(P);
        cipher.processBlock(P, 0, P, 0);
        SP80038G.rev(P);
        byte[] S = P;
        return SP80038G.num(S, 0, S.length);
    }

    protected static void checkArgs(BlockCipher cipher, boolean isFF1, int radix, short[] buf, int off, int len) {
        SP80038G.checkCipher(cipher);
        if (radix < 2 || radix > 65536) {
            throw new IllegalArgumentException();
        }
        SP80038G.checkData(isFF1, radix, buf, off, len);
    }

    protected static void checkArgs(BlockCipher cipher, boolean isFF1, int radix, byte[] buf, int off, int len) {
        SP80038G.checkCipher(cipher);
        if (radix < 2 || radix > 256) {
            throw new IllegalArgumentException();
        }
        SP80038G.checkData(isFF1, radix, buf, off, len);
    }

    protected static void checkCipher(BlockCipher cipher) {
        if (16 != cipher.getBlockSize()) {
            throw new IllegalArgumentException();
        }
    }

    protected static void checkData(boolean isFF1, int radix, short[] buf, int off, int len) {
        SP80038G.checkLength(isFF1, radix, len);
        for (int i = 0; i < len; ++i) {
            int b = buf[off + i] & 0xFFFF;
            if (b < radix) continue;
            throw new IllegalArgumentException("input data outside of radix");
        }
    }

    protected static void checkData(boolean isFF1, int radix, byte[] buf, int off, int len) {
        SP80038G.checkLength(isFF1, radix, len);
        for (int i = 0; i < len; ++i) {
            int b = buf[off + i] & 0xFF;
            if (b < radix) continue;
            throw new IllegalArgumentException("input data outside of radix");
        }
    }

    private static void checkLength(boolean isFF1, int radix, int len) {
        int maxLen;
        if (len < 2 || Math.pow(radix, len) < 1000000.0) {
            throw new IllegalArgumentException("input too short");
        }
        if (!isFF1 && len > (maxLen = 2 * (int)Math.floor(Math.log(TWO_TO_96) / Math.log(radix)))) {
            throw new IllegalArgumentException("maximum input length is " + maxLen);
        }
    }

    protected static byte[] implDecryptFF3(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak64, byte[] buf, int off, int len) {
        byte[] T = tweak64;
        int n = len;
        int v = n / 2;
        int u = n - v;
        short[] A = SP80038G.toShort(buf, off, u);
        short[] B = SP80038G.toShort(buf, off + u, v);
        short[] rv = SP80038G.decFF3_1(cipher, radixConverter, T, n, v, u, A, B);
        return SP80038G.toByte(rv);
    }

    protected static short[] implDecryptFF3w(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak64, short[] buf, int off, int len) {
        byte[] T = tweak64;
        int n = len;
        int v = n / 2;
        int u = n - v;
        short[] A = new short[u];
        short[] B = new short[v];
        System.arraycopy(buf, off, A, 0, u);
        System.arraycopy(buf, off + u, B, 0, v);
        return SP80038G.decFF3_1(cipher, radixConverter, T, n, v, u, A, B);
    }

    private static short[] decFF3_1(BlockCipher cipher, RadixConverter radixConverter, byte[] T, int n, int v, int u, short[] A, short[] B) {
        BigInteger bigRadix = BigInteger.valueOf(radixConverter.getRadix());
        BigInteger[] modVU = SP80038G.calculateModUV(bigRadix, v, u);
        int m = u;
        SP80038G.rev(A);
        SP80038G.rev(B);
        for (int i = 7; i >= 0; --i) {
            m = n - m;
            BigInteger modulus = modVU[1 - (i & 1)];
            int wOff = 4 - (i & 1) * 4;
            BigInteger y = SP80038G.calculateY_FF3(cipher, T, wOff, i, A, radixConverter);
            BigInteger c = radixConverter.fromEncoding(B).subtract(y).mod(modulus);
            short[] C = B;
            B = A;
            A = C;
            radixConverter.toEncoding(c, m, C);
        }
        SP80038G.rev(A);
        SP80038G.rev(B);
        return Arrays.concatenate(A, B);
    }

    protected static byte[] implEncryptFF3(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak64, byte[] buf, int off, int len) {
        byte[] T = tweak64;
        int n = len;
        int v = n / 2;
        int u = n - v;
        short[] A = SP80038G.toShort(buf, off, u);
        short[] B = SP80038G.toShort(buf, off + u, v);
        short[] rv = SP80038G.encFF3_1(cipher, radixConverter, T, n, v, u, A, B);
        return SP80038G.toByte(rv);
    }

    protected static short[] implEncryptFF3w(BlockCipher cipher, RadixConverter radixConverter, byte[] tweak64, short[] buf, int off, int len) {
        byte[] T = tweak64;
        int n = len;
        int v = n / 2;
        int u = n - v;
        short[] A = new short[u];
        short[] B = new short[v];
        System.arraycopy(buf, off, A, 0, u);
        System.arraycopy(buf, off + u, B, 0, v);
        return SP80038G.encFF3_1(cipher, radixConverter, T, n, v, u, A, B);
    }

    private static short[] encFF3_1(BlockCipher cipher, RadixConverter radixConverter, byte[] t, int n, int v, int u, short[] a, short[] b) {
        BigInteger bigRadix = BigInteger.valueOf(radixConverter.getRadix());
        BigInteger[] modVU = SP80038G.calculateModUV(bigRadix, v, u);
        int m = v;
        SP80038G.rev(a);
        SP80038G.rev(b);
        for (int i = 0; i < 8; ++i) {
            m = n - m;
            BigInteger modulus = modVU[1 - (i & 1)];
            int wOff = 4 - (i & 1) * 4;
            BigInteger y = SP80038G.calculateY_FF3(cipher, t, wOff, i, b, radixConverter);
            BigInteger c = radixConverter.fromEncoding(a).add(y).mod(modulus);
            short[] C = a;
            a = b;
            b = C;
            radixConverter.toEncoding(c, m, C);
        }
        SP80038G.rev(a);
        SP80038G.rev(b);
        return Arrays.concatenate(a, b);
    }

    protected static BigInteger num(byte[] buf, int off, int len) {
        return new BigInteger(1, Arrays.copyOfRange(buf, off, off + len));
    }

    protected static byte[] prf(BlockCipher c, byte[] x) {
        if (x.length % 16 != 0) {
            throw new IllegalArgumentException();
        }
        int m = x.length / 16;
        byte[] y = new byte[16];
        for (int i = 0; i < m; ++i) {
            SP80038G.xor(x, i * 16, y, 0, 16);
            c.processBlock(y, 0, y, 0);
        }
        return y;
    }

    protected static void rev(byte[] x) {
        int half = x.length / 2;
        int end = x.length - 1;
        for (int i = 0; i < half; ++i) {
            byte tmp = x[i];
            x[i] = x[end - i];
            x[end - i] = tmp;
        }
    }

    protected static void rev(short[] x) {
        int half = x.length / 2;
        int end = x.length - 1;
        for (int i = 0; i < half; ++i) {
            short tmp = x[i];
            x[i] = x[end - i];
            x[end - i] = tmp;
        }
    }

    protected static void xor(byte[] x, int xOff, byte[] y, int yOff, int len) {
        for (int i = 0; i < len; ++i) {
            int n = yOff + i;
            y[n] = (byte)(y[n] ^ x[xOff + i]);
        }
    }

    private static byte[] toByte(short[] buf) {
        byte[] s = new byte[buf.length];
        for (int i = 0; i != s.length; ++i) {
            s[i] = (byte)buf[i];
        }
        return s;
    }

    private static short[] toShort(byte[] buf, int off, int len) {
        short[] s = new short[len];
        for (int i = 0; i != s.length; ++i) {
            s[i] = (short)(buf[off + i] & 0xFF);
        }
        return s;
    }
}

