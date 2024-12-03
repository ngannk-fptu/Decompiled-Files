/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.random;

import com.opensymphony.module.random.Rijndael_Properties;
import java.io.PrintWriter;
import java.security.InvalidKeyException;

public final class Rijndael_Algorithm {
    static final String NAME = "Rijndael_Algorithm";
    static final boolean IN = true;
    static final boolean OUT = false;
    static final boolean DEBUG = false;
    static final int debuglevel;
    static final PrintWriter err;
    static final boolean TRACE;
    static final int BLOCK_SIZE = 16;
    static final int[] alog;
    static final int[] log;
    static final byte[] S;
    static final byte[] Si;
    static final int[] T1;
    static final int[] T2;
    static final int[] T3;
    static final int[] T4;
    static final int[] T5;
    static final int[] T6;
    static final int[] T7;
    static final int[] T8;
    static final int[] U1;
    static final int[] U2;
    static final int[] U3;
    static final int[] U4;
    static final byte[] rcon;
    static final int[][][] shifts;
    private static final char[] HEX_DIGITS;

    public static int getRounds(int keySize, int blockSize) {
        switch (keySize) {
            case 16: {
                return blockSize == 16 ? 10 : (blockSize == 24 ? 12 : 14);
            }
            case 24: {
                return blockSize != 32 ? 12 : 14;
            }
        }
        return 14;
    }

    public static byte[] blockDecrypt(byte[] in, int inOffset, Object sessionKey) {
        int[][] Kd = (int[][])((Object[])sessionKey)[1];
        int ROUNDS = Kd.length - 1;
        int[] Kdr = Kd[0];
        int t0 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Kdr[0];
        int t1 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Kdr[1];
        int t2 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Kdr[2];
        int t3 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Kdr[3];
        for (int r = 1; r < ROUNDS; ++r) {
            Kdr = Kd[r];
            int a0 = T5[t0 >>> 24 & 0xFF] ^ T6[t3 >>> 16 & 0xFF] ^ T7[t2 >>> 8 & 0xFF] ^ T8[t1 & 0xFF] ^ Kdr[0];
            int a1 = T5[t1 >>> 24 & 0xFF] ^ T6[t0 >>> 16 & 0xFF] ^ T7[t3 >>> 8 & 0xFF] ^ T8[t2 & 0xFF] ^ Kdr[1];
            int a2 = T5[t2 >>> 24 & 0xFF] ^ T6[t1 >>> 16 & 0xFF] ^ T7[t0 >>> 8 & 0xFF] ^ T8[t3 & 0xFF] ^ Kdr[2];
            int a3 = T5[t3 >>> 24 & 0xFF] ^ T6[t2 >>> 16 & 0xFF] ^ T7[t1 >>> 8 & 0xFF] ^ T8[t0 & 0xFF] ^ Kdr[3];
            t0 = a0;
            t1 = a1;
            t2 = a2;
            t3 = a3;
        }
        byte[] result = new byte[16];
        Kdr = Kd[ROUNDS];
        int tt = Kdr[0];
        result[0] = (byte)(Si[t0 >>> 24 & 0xFF] ^ tt >>> 24);
        result[1] = (byte)(Si[t3 >>> 16 & 0xFF] ^ tt >>> 16);
        result[2] = (byte)(Si[t2 >>> 8 & 0xFF] ^ tt >>> 8);
        result[3] = (byte)(Si[t1 & 0xFF] ^ tt);
        tt = Kdr[1];
        result[4] = (byte)(Si[t1 >>> 24 & 0xFF] ^ tt >>> 24);
        result[5] = (byte)(Si[t0 >>> 16 & 0xFF] ^ tt >>> 16);
        result[6] = (byte)(Si[t3 >>> 8 & 0xFF] ^ tt >>> 8);
        result[7] = (byte)(Si[t2 & 0xFF] ^ tt);
        tt = Kdr[2];
        result[8] = (byte)(Si[t2 >>> 24 & 0xFF] ^ tt >>> 24);
        result[9] = (byte)(Si[t1 >>> 16 & 0xFF] ^ tt >>> 16);
        result[10] = (byte)(Si[t0 >>> 8 & 0xFF] ^ tt >>> 8);
        result[11] = (byte)(Si[t3 & 0xFF] ^ tt);
        tt = Kdr[3];
        result[12] = (byte)(Si[t3 >>> 24 & 0xFF] ^ tt >>> 24);
        result[13] = (byte)(Si[t2 >>> 16 & 0xFF] ^ tt >>> 16);
        result[14] = (byte)(Si[t1 >>> 8 & 0xFF] ^ tt >>> 8);
        result[15] = (byte)(Si[t0 & 0xFF] ^ tt);
        return result;
    }

    public static byte[] blockDecrypt(byte[] in, int inOffset, Object sessionKey, int blockSize) {
        int i;
        if (blockSize == 16) {
            return Rijndael_Algorithm.blockDecrypt(in, inOffset, sessionKey);
        }
        Object[] sKey = (Object[])sessionKey;
        int[][] Kd = (int[][])sKey[1];
        int BC = blockSize / 4;
        int ROUNDS = Kd.length - 1;
        int SC = BC == 4 ? 0 : (BC == 6 ? 1 : 2);
        int s1 = shifts[SC][1][1];
        int s2 = shifts[SC][2][1];
        int s3 = shifts[SC][3][1];
        int[] a = new int[BC];
        int[] t = new int[BC];
        byte[] result = new byte[blockSize];
        int j = 0;
        for (i = 0; i < BC; ++i) {
            t[i] = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Kd[0][i];
        }
        for (int r = 1; r < ROUNDS; ++r) {
            for (i = 0; i < BC; ++i) {
                a[i] = T5[t[i] >>> 24 & 0xFF] ^ T6[t[(i + s1) % BC] >>> 16 & 0xFF] ^ T7[t[(i + s2) % BC] >>> 8 & 0xFF] ^ T8[t[(i + s3) % BC] & 0xFF] ^ Kd[r][i];
            }
            System.arraycopy(a, 0, t, 0, BC);
        }
        for (i = 0; i < BC; ++i) {
            int tt = Kd[ROUNDS][i];
            result[j++] = (byte)(Si[t[i] >>> 24 & 0xFF] ^ tt >>> 24);
            result[j++] = (byte)(Si[t[(i + s1) % BC] >>> 16 & 0xFF] ^ tt >>> 16);
            result[j++] = (byte)(Si[t[(i + s2) % BC] >>> 8 & 0xFF] ^ tt >>> 8);
            result[j++] = (byte)(Si[t[(i + s3) % BC] & 0xFF] ^ tt);
        }
        return result;
    }

    public static byte[] blockEncrypt(byte[] in, int inOffset, Object sessionKey) {
        int[][] Ke = (int[][])((Object[])sessionKey)[0];
        int ROUNDS = Ke.length - 1;
        int[] Ker = Ke[0];
        int t0 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Ker[0];
        int t1 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Ker[1];
        int t2 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Ker[2];
        int t3 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Ker[3];
        for (int r = 1; r < ROUNDS; ++r) {
            Ker = Ke[r];
            int a0 = T1[t0 >>> 24 & 0xFF] ^ T2[t1 >>> 16 & 0xFF] ^ T3[t2 >>> 8 & 0xFF] ^ T4[t3 & 0xFF] ^ Ker[0];
            int a1 = T1[t1 >>> 24 & 0xFF] ^ T2[t2 >>> 16 & 0xFF] ^ T3[t3 >>> 8 & 0xFF] ^ T4[t0 & 0xFF] ^ Ker[1];
            int a2 = T1[t2 >>> 24 & 0xFF] ^ T2[t3 >>> 16 & 0xFF] ^ T3[t0 >>> 8 & 0xFF] ^ T4[t1 & 0xFF] ^ Ker[2];
            int a3 = T1[t3 >>> 24 & 0xFF] ^ T2[t0 >>> 16 & 0xFF] ^ T3[t1 >>> 8 & 0xFF] ^ T4[t2 & 0xFF] ^ Ker[3];
            t0 = a0;
            t1 = a1;
            t2 = a2;
            t3 = a3;
        }
        byte[] result = new byte[16];
        Ker = Ke[ROUNDS];
        int tt = Ker[0];
        result[0] = (byte)(S[t0 >>> 24 & 0xFF] ^ tt >>> 24);
        result[1] = (byte)(S[t1 >>> 16 & 0xFF] ^ tt >>> 16);
        result[2] = (byte)(S[t2 >>> 8 & 0xFF] ^ tt >>> 8);
        result[3] = (byte)(S[t3 & 0xFF] ^ tt);
        tt = Ker[1];
        result[4] = (byte)(S[t1 >>> 24 & 0xFF] ^ tt >>> 24);
        result[5] = (byte)(S[t2 >>> 16 & 0xFF] ^ tt >>> 16);
        result[6] = (byte)(S[t3 >>> 8 & 0xFF] ^ tt >>> 8);
        result[7] = (byte)(S[t0 & 0xFF] ^ tt);
        tt = Ker[2];
        result[8] = (byte)(S[t2 >>> 24 & 0xFF] ^ tt >>> 24);
        result[9] = (byte)(S[t3 >>> 16 & 0xFF] ^ tt >>> 16);
        result[10] = (byte)(S[t0 >>> 8 & 0xFF] ^ tt >>> 8);
        result[11] = (byte)(S[t1 & 0xFF] ^ tt);
        tt = Ker[3];
        result[12] = (byte)(S[t3 >>> 24 & 0xFF] ^ tt >>> 24);
        result[13] = (byte)(S[t0 >>> 16 & 0xFF] ^ tt >>> 16);
        result[14] = (byte)(S[t1 >>> 8 & 0xFF] ^ tt >>> 8);
        result[15] = (byte)(S[t2 & 0xFF] ^ tt);
        return result;
    }

    public static byte[] blockEncrypt(byte[] in, int inOffset, Object sessionKey, int blockSize) {
        int i;
        if (blockSize == 16) {
            return Rijndael_Algorithm.blockEncrypt(in, inOffset, sessionKey);
        }
        Object[] sKey = (Object[])sessionKey;
        int[][] Ke = (int[][])sKey[0];
        int BC = blockSize / 4;
        int ROUNDS = Ke.length - 1;
        int SC = BC == 4 ? 0 : (BC == 6 ? 1 : 2);
        int s1 = shifts[SC][1][0];
        int s2 = shifts[SC][2][0];
        int s3 = shifts[SC][3][0];
        int[] a = new int[BC];
        int[] t = new int[BC];
        byte[] result = new byte[blockSize];
        int j = 0;
        for (i = 0; i < BC; ++i) {
            t[i] = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | in[inOffset++] & 0xFF) ^ Ke[0][i];
        }
        for (int r = 1; r < ROUNDS; ++r) {
            for (i = 0; i < BC; ++i) {
                a[i] = T1[t[i] >>> 24 & 0xFF] ^ T2[t[(i + s1) % BC] >>> 16 & 0xFF] ^ T3[t[(i + s2) % BC] >>> 8 & 0xFF] ^ T4[t[(i + s3) % BC] & 0xFF] ^ Ke[r][i];
            }
            System.arraycopy(a, 0, t, 0, BC);
        }
        for (i = 0; i < BC; ++i) {
            int tt = Ke[ROUNDS][i];
            result[j++] = (byte)(S[t[i] >>> 24 & 0xFF] ^ tt >>> 24);
            result[j++] = (byte)(S[t[(i + s1) % BC] >>> 16 & 0xFF] ^ tt >>> 16);
            result[j++] = (byte)(S[t[(i + s2) % BC] >>> 8 & 0xFF] ^ tt >>> 8);
            result[j++] = (byte)(S[t[(i + s3) % BC] & 0xFF] ^ tt);
        }
        return result;
    }

    public static int blockSize() {
        return 16;
    }

    public static void main(String[] args) {
        Rijndael_Algorithm.self_test(16);
        Rijndael_Algorithm.self_test(24);
        Rijndael_Algorithm.self_test(32);
    }

    public static Object makeKey(byte[] k) throws InvalidKeyException {
        return Rijndael_Algorithm.makeKey(k, 16);
    }

    public static synchronized Object makeKey(byte[] k, int blockSize) throws InvalidKeyException {
        int tt;
        if (k == null) {
            throw new InvalidKeyException("Empty key");
        }
        if (k.length != 16 && k.length != 24 && k.length != 32) {
            throw new InvalidKeyException("Incorrect key length");
        }
        int ROUNDS = Rijndael_Algorithm.getRounds(k.length, blockSize);
        int BC = blockSize / 4;
        int[][] Ke = new int[ROUNDS + 1][BC];
        int[][] Kd = new int[ROUNDS + 1][BC];
        int ROUND_KEY_COUNT = (ROUNDS + 1) * BC;
        int KC = k.length / 4;
        int[] tk = new int[KC];
        int i = 0;
        int j = 0;
        while (i < KC) {
            tk[i++] = (k[j++] & 0xFF) << 24 | (k[j++] & 0xFF) << 16 | (k[j++] & 0xFF) << 8 | k[j++] & 0xFF;
        }
        int t = 0;
        for (j = 0; j < KC && t < ROUND_KEY_COUNT; ++j, ++t) {
            Ke[t / BC][t % BC] = tk[j];
            Kd[ROUNDS - t / BC][t % BC] = tk[j];
        }
        int rconpointer = 0;
        while (t < ROUND_KEY_COUNT) {
            tt = tk[KC - 1];
            tk[0] = tk[0] ^ ((S[tt >>> 16 & 0xFF] & 0xFF) << 24 ^ (S[tt >>> 8 & 0xFF] & 0xFF) << 16 ^ (S[tt & 0xFF] & 0xFF) << 8 ^ S[tt >>> 24 & 0xFF] & 0xFF ^ (rcon[rconpointer++] & 0xFF) << 24);
            if (KC != 8) {
                i = 1;
                j = 0;
                while (i < KC) {
                    int n = i++;
                    tk[n] = tk[n] ^ tk[j++];
                }
            } else {
                i = 1;
                j = 0;
                while (i < KC / 2) {
                    int n = i++;
                    tk[n] = tk[n] ^ tk[j++];
                }
                tt = tk[KC / 2 - 1];
                int n = KC / 2;
                tk[n] = tk[n] ^ (S[tt & 0xFF] & 0xFF ^ (S[tt >>> 8 & 0xFF] & 0xFF) << 8 ^ (S[tt >>> 16 & 0xFF] & 0xFF) << 16 ^ (S[tt >>> 24 & 0xFF] & 0xFF) << 24);
                j = KC / 2;
                i = j + 1;
                while (i < KC) {
                    int n2 = i++;
                    tk[n2] = tk[n2] ^ tk[j++];
                }
            }
            for (j = 0; j < KC && t < ROUND_KEY_COUNT; ++j, ++t) {
                Ke[t / BC][t % BC] = tk[j];
                Kd[ROUNDS - t / BC][t % BC] = tk[j];
            }
        }
        for (int r = 1; r < ROUNDS; ++r) {
            for (j = 0; j < BC; ++j) {
                tt = Kd[r][j];
                Kd[r][j] = U1[tt >>> 24 & 0xFF] ^ U2[tt >>> 16 & 0xFF] ^ U3[tt >>> 8 & 0xFF] ^ U4[tt & 0xFF];
            }
        }
        Object[] sessionKey = new Object[]{Ke, Kd};
        return sessionKey;
    }

    public static boolean self_test() {
        return Rijndael_Algorithm.self_test(16);
    }

    static void debug(String s) {
        err.println(">>> Rijndael_Algorithm: " + s);
    }

    static final int mul(int a, int b) {
        return a != 0 && b != 0 ? alog[(log[a & 0xFF] + log[b & 0xFF]) % 255] : 0;
    }

    static final int mul4(int a, byte[] b) {
        if (a == 0) {
            return 0;
        }
        a = log[a & 0xFF];
        int a0 = b[0] != 0 ? alog[(a + log[b[0] & 0xFF]) % 255] & 0xFF : 0;
        int a1 = b[1] != 0 ? alog[(a + log[b[1] & 0xFF]) % 255] & 0xFF : 0;
        int a2 = b[2] != 0 ? alog[(a + log[b[2] & 0xFF]) % 255] & 0xFF : 0;
        int a3 = b[3] != 0 ? alog[(a + log[b[3] & 0xFF]) % 255] & 0xFF : 0;
        return a0 << 24 | a1 << 16 | a2 << 8 | a3;
    }

    static void trace(boolean in, String s) {
        if (TRACE) {
            err.println((in ? "==> " : "<== ") + NAME + "." + s);
        }
    }

    static void trace(String s) {
        if (TRACE) {
            err.println("<=> Rijndael_Algorithm." + s);
        }
    }

    private static boolean areEqual(byte[] a, byte[] b) {
        int aLength = a.length;
        if (aLength != b.length) {
            return false;
        }
        for (int i = 0; i < aLength; ++i) {
            if (a[i] == b[i]) continue;
            return false;
        }
        return true;
    }

    private static String byteToString(int n) {
        char[] buf = new char[]{HEX_DIGITS[n >>> 4 & 0xF], HEX_DIGITS[n & 0xF]};
        return new String(buf);
    }

    private static String intToString(int n) {
        char[] buf = new char[8];
        for (int i = 7; i >= 0; --i) {
            buf[i] = HEX_DIGITS[n & 0xF];
            n >>>= 4;
        }
        return new String(buf);
    }

    private static boolean self_test(int keysize) {
        boolean ok = false;
        try {
            int i;
            byte[] kb = new byte[keysize];
            byte[] pt = new byte[16];
            for (i = 0; i < keysize; ++i) {
                kb[i] = (byte)i;
            }
            for (i = 0; i < 16; ++i) {
                pt[i] = (byte)i;
            }
            Object key = Rijndael_Algorithm.makeKey(kb, 16);
            byte[] ct = Rijndael_Algorithm.blockEncrypt(pt, 0, key, 16);
            byte[] cpt = Rijndael_Algorithm.blockDecrypt(ct, 0, key, 16);
            ok = Rijndael_Algorithm.areEqual(pt, cpt);
            if (!ok) {
                throw new RuntimeException("Symmetric operation failed");
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return ok;
    }

    private static String toString(byte[] ba) {
        int length = ba.length;
        char[] buf = new char[length * 2];
        int i = 0;
        int j = 0;
        while (i < length) {
            byte k = ba[i++];
            buf[j++] = HEX_DIGITS[k >>> 4 & 0xF];
            buf[j++] = HEX_DIGITS[k & 0xF];
        }
        return new String(buf);
    }

    private static String toString(int[] ia) {
        int length = ia.length;
        char[] buf = new char[length * 8];
        int j = 0;
        for (int i = 0; i < length; ++i) {
            int k = ia[i];
            buf[j++] = HEX_DIGITS[k >>> 28 & 0xF];
            buf[j++] = HEX_DIGITS[k >>> 24 & 0xF];
            buf[j++] = HEX_DIGITS[k >>> 20 & 0xF];
            buf[j++] = HEX_DIGITS[k >>> 16 & 0xF];
            buf[j++] = HEX_DIGITS[k >>> 12 & 0xF];
            buf[j++] = HEX_DIGITS[k >>> 8 & 0xF];
            buf[j++] = HEX_DIGITS[k >>> 4 & 0xF];
            buf[j++] = HEX_DIGITS[k & 0xF];
        }
        return new String(buf);
    }

    static {
        int t;
        int i;
        debuglevel = 0;
        err = null;
        TRACE = Rijndael_Properties.isTraceable(NAME);
        alog = new int[256];
        log = new int[256];
        S = new byte[256];
        Si = new byte[256];
        T1 = new int[256];
        T2 = new int[256];
        T3 = new int[256];
        T4 = new int[256];
        T5 = new int[256];
        T6 = new int[256];
        T7 = new int[256];
        T8 = new int[256];
        U1 = new int[256];
        U2 = new int[256];
        U3 = new int[256];
        U4 = new int[256];
        rcon = new byte[30];
        shifts = new int[][][]{new int[][]{{0, 0}, {1, 3}, {2, 2}, {3, 1}}, new int[][]{{0, 0}, {1, 5}, {2, 4}, {3, 3}}, new int[][]{{0, 0}, {1, 7}, {3, 5}, {4, 4}}};
        HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        long time = System.currentTimeMillis();
        int ROOT = 283;
        int j = 0;
        Rijndael_Algorithm.alog[0] = 1;
        for (i = 1; i < 256; ++i) {
            j = alog[i - 1] << 1 ^ alog[i - 1];
            if ((j & 0x100) != 0) {
                j ^= ROOT;
            }
            Rijndael_Algorithm.alog[i] = j;
        }
        for (i = 1; i < 255; ++i) {
            Rijndael_Algorithm.log[Rijndael_Algorithm.alog[i]] = i;
        }
        byte[][] A = new byte[][]{{1, 1, 1, 1, 1, 0, 0, 0}, {0, 1, 1, 1, 1, 1, 0, 0}, {0, 0, 1, 1, 1, 1, 1, 0}, {0, 0, 0, 1, 1, 1, 1, 1}, {1, 0, 0, 0, 1, 1, 1, 1}, {1, 1, 0, 0, 0, 1, 1, 1}, {1, 1, 1, 0, 0, 0, 1, 1}, {1, 1, 1, 1, 0, 0, 0, 1}};
        byte[] B = new byte[]{0, 1, 1, 0, 0, 0, 1, 1};
        byte[][] box = new byte[256][8];
        box[1][7] = 1;
        for (i = 2; i < 256; ++i) {
            j = alog[255 - log[i]];
            for (t = 0; t < 8; ++t) {
                box[i][t] = (byte)(j >>> 7 - t & 1);
            }
        }
        byte[][] cox = new byte[256][8];
        for (i = 0; i < 256; ++i) {
            for (t = 0; t < 8; ++t) {
                cox[i][t] = B[t];
                for (j = 0; j < 8; ++j) {
                    byte[] byArray = cox[i];
                    int n = t;
                    byArray[n] = (byte)(byArray[n] ^ A[t][j] * box[i][j]);
                }
            }
        }
        for (i = 0; i < 256; ++i) {
            Rijndael_Algorithm.S[i] = (byte)(cox[i][0] << 7);
            for (t = 1; t < 8; ++t) {
                int n = i;
                S[n] = (byte)(S[n] ^ cox[i][t] << 7 - t);
            }
            Rijndael_Algorithm.Si[Rijndael_Algorithm.S[i] & 0xFF] = (byte)i;
        }
        byte[][] G = new byte[][]{{2, 1, 1, 3}, {3, 2, 1, 1}, {1, 3, 2, 1}, {1, 1, 3, 2}};
        byte[][] AA = new byte[4][8];
        for (i = 0; i < 4; ++i) {
            for (j = 0; j < 4; ++j) {
                AA[i][j] = G[i][j];
            }
            AA[i][i + 4] = 1;
        }
        byte[][] iG = new byte[4][4];
        for (i = 0; i < 4; ++i) {
            byte pivot = AA[i][i];
            if (pivot == 0) {
                for (t = i + 1; AA[t][i] == 0 && t < 4; ++t) {
                }
                if (t == 4) {
                    throw new RuntimeException("G matrix is not invertible");
                }
                for (j = 0; j < 8; ++j) {
                    byte tmp = AA[i][j];
                    AA[i][j] = AA[t][j];
                    AA[t][j] = tmp;
                }
                pivot = AA[i][i];
            }
            for (j = 0; j < 8; ++j) {
                if (AA[i][j] == 0) continue;
                AA[i][j] = (byte)alog[(255 + log[AA[i][j] & 0xFF] - log[pivot & 0xFF]) % 255];
            }
            for (t = 0; t < 4; ++t) {
                if (i == t) continue;
                for (j = i + 1; j < 8; ++j) {
                    byte[] byArray = AA[t];
                    int n = j;
                    byArray[n] = (byte)(byArray[n] ^ Rijndael_Algorithm.mul(AA[i][j], AA[t][i]));
                }
                AA[t][i] = 0;
            }
        }
        for (i = 0; i < 4; ++i) {
            for (j = 0; j < 4; ++j) {
                iG[i][j] = AA[i][j + 4];
            }
        }
        for (t = 0; t < 256; ++t) {
            byte s = S[t];
            Rijndael_Algorithm.T1[t] = Rijndael_Algorithm.mul4(s, G[0]);
            Rijndael_Algorithm.T2[t] = Rijndael_Algorithm.mul4(s, G[1]);
            Rijndael_Algorithm.T3[t] = Rijndael_Algorithm.mul4(s, G[2]);
            Rijndael_Algorithm.T4[t] = Rijndael_Algorithm.mul4(s, G[3]);
            s = Si[t];
            Rijndael_Algorithm.T5[t] = Rijndael_Algorithm.mul4(s, iG[0]);
            Rijndael_Algorithm.T6[t] = Rijndael_Algorithm.mul4(s, iG[1]);
            Rijndael_Algorithm.T7[t] = Rijndael_Algorithm.mul4(s, iG[2]);
            Rijndael_Algorithm.T8[t] = Rijndael_Algorithm.mul4(s, iG[3]);
            Rijndael_Algorithm.U1[t] = Rijndael_Algorithm.mul4(t, iG[0]);
            Rijndael_Algorithm.U2[t] = Rijndael_Algorithm.mul4(t, iG[1]);
            Rijndael_Algorithm.U3[t] = Rijndael_Algorithm.mul4(t, iG[2]);
            Rijndael_Algorithm.U4[t] = Rijndael_Algorithm.mul4(t, iG[3]);
        }
        Rijndael_Algorithm.rcon[0] = 1;
        int r = 1;
        t = 1;
        while (t < 30) {
            int n = t++;
            r = Rijndael_Algorithm.mul(2, r);
            Rijndael_Algorithm.rcon[n] = (byte)r;
        }
        long l = System.currentTimeMillis() - time;
    }
}

