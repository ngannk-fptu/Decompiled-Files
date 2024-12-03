/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class SCrypt {
    private SCrypt() {
    }

    public static byte[] generate(byte[] P, byte[] S, int N, int r, int p, int dkLen) {
        if (P == null) {
            throw new IllegalArgumentException("Passphrase P must be provided.");
        }
        if (S == null) {
            throw new IllegalArgumentException("Salt S must be provided.");
        }
        if (N <= 1 || !SCrypt.isPowerOf2(N)) {
            throw new IllegalArgumentException("Cost parameter N must be > 1 and a power of 2");
        }
        if (r == 1 && N >= 65536) {
            throw new IllegalArgumentException("Cost parameter N must be > 1 and < 65536.");
        }
        if (r < 1) {
            throw new IllegalArgumentException("Block size r must be >= 1.");
        }
        int maxParallel = Integer.MAX_VALUE / (128 * r * 8);
        if (p < 1 || p > maxParallel) {
            throw new IllegalArgumentException("Parallelisation parameter p must be >= 1 and <= " + maxParallel + " (based on block size r of " + r + ")");
        }
        if (dkLen < 1) {
            throw new IllegalArgumentException("Generated key length dkLen must be >= 1.");
        }
        return SCrypt.MFcrypt(P, S, N, r, p, dkLen);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static byte[] MFcrypt(byte[] P, byte[] S, int N, int r, int p, int dkLen) {
        byte[] byArray;
        int MFLenBytes = r * 128;
        byte[] bytes = SCrypt.SingleIterationPBKDF2(P, S, p * MFLenBytes);
        int[] B = null;
        try {
            int BLen = bytes.length >>> 2;
            B = new int[BLen];
            Pack.littleEndianToInt(bytes, 0, B);
            int d = 0;
            for (int total = N * r; N - d > 2 && total > 1024; total >>>= 1) {
                ++d;
            }
            int MFLenWords = MFLenBytes >>> 2;
            for (int BOff = 0; BOff < BLen; BOff += MFLenWords) {
                SCrypt.SMix(B, BOff, N, d, r);
            }
            Pack.intToLittleEndian(B, bytes, 0);
            byArray = SCrypt.SingleIterationPBKDF2(P, bytes, dkLen);
        }
        catch (Throwable throwable) {
            SCrypt.Clear(bytes);
            SCrypt.Clear(B);
            throw throwable;
        }
        SCrypt.Clear(bytes);
        SCrypt.Clear(B);
        return byArray;
    }

    private static byte[] SingleIterationPBKDF2(byte[] P, byte[] S, int dkLen) {
        PKCS5S2ParametersGenerator pGen = new PKCS5S2ParametersGenerator(SHA256Digest.newInstance());
        pGen.init(P, S, 1);
        KeyParameter key = (KeyParameter)((PBEParametersGenerator)pGen).generateDerivedMacParameters(dkLen * 8);
        return key.getKey();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void SMix(int[] B, int BOff, int N, int d, int r) {
        int powN = Integers.numberOfTrailingZeros(N);
        int blocksPerChunk = N >>> d;
        int chunkCount = 1 << d;
        int chunkMask = blocksPerChunk - 1;
        int chunkPow = powN - d;
        int BCount = r * 32;
        int[] blockX1 = new int[16];
        int[] blockX2 = new int[16];
        int[] blockY = new int[BCount];
        int[] X = new int[BCount];
        int[][] VV = new int[chunkCount][];
        try {
            System.arraycopy(B, BOff, X, 0, BCount);
            for (int c = 0; c < chunkCount; ++c) {
                int[] V = new int[blocksPerChunk * BCount];
                VV[c] = V;
                int off = 0;
                for (int i = 0; i < blocksPerChunk; i += 2) {
                    System.arraycopy(X, 0, V, off, BCount);
                    SCrypt.BlockMix(X, blockX1, blockX2, blockY, r);
                    System.arraycopy(blockY, 0, V, off += BCount, BCount);
                    off += BCount;
                    SCrypt.BlockMix(blockY, blockX1, blockX2, X, r);
                }
            }
            int mask = N - 1;
            for (int i = 0; i < N; ++i) {
                int j = X[BCount - 16] & mask;
                int[] V = VV[j >>> chunkPow];
                int VOff = (j & chunkMask) * BCount;
                System.arraycopy(V, VOff, blockY, 0, BCount);
                SCrypt.Xor(blockY, X, 0, blockY);
                SCrypt.BlockMix(blockY, blockX1, blockX2, X, r);
            }
            System.arraycopy(X, 0, B, BOff, BCount);
        }
        catch (Throwable throwable) {
            SCrypt.ClearAll(VV);
            SCrypt.ClearAll(new int[][]{X, blockX1, blockX2, blockY});
            throw throwable;
        }
        SCrypt.ClearAll(VV);
        SCrypt.ClearAll(new int[][]{X, blockX1, blockX2, blockY});
    }

    private static void BlockMix(int[] B, int[] X1, int[] X2, int[] Y, int r) {
        System.arraycopy(B, B.length - 16, X1, 0, 16);
        int BOff = 0;
        int YOff = 0;
        int halfLen = B.length >>> 1;
        for (int i = 2 * r; i > 0; --i) {
            SCrypt.Xor(X1, B, BOff, X2);
            Salsa20Engine.salsaCore(8, X2, X1);
            System.arraycopy(X1, 0, Y, YOff, 16);
            YOff = halfLen + BOff - YOff;
            BOff += 16;
        }
    }

    private static void Xor(int[] a, int[] b, int bOff, int[] output) {
        for (int i = output.length - 1; i >= 0; --i) {
            output[i] = a[i] ^ b[bOff + i];
        }
    }

    private static void Clear(byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte)0);
        }
    }

    private static void Clear(int[] array) {
        if (array != null) {
            Arrays.fill(array, 0);
        }
    }

    private static void ClearAll(int[][] arrays) {
        for (int i = 0; i < arrays.length; ++i) {
            SCrypt.Clear(arrays[i]);
        }
    }

    private static boolean isPowerOf2(int x) {
        return (x & x - 1) == 0;
    }
}

