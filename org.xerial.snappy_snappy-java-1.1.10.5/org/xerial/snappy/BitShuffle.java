/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xerial.snappy.BitShuffleNative;
import org.xerial.snappy.BitShuffleType;
import org.xerial.snappy.SnappyError;
import org.xerial.snappy.SnappyErrorCode;
import org.xerial.snappy.SnappyLoader;

public class BitShuffle {
    private static BitShuffleNative impl;

    public static int shuffle(ByteBuffer byteBuffer, BitShuffleType bitShuffleType, ByteBuffer byteBuffer2) throws IOException {
        int n;
        if (!byteBuffer.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "input is not a direct buffer");
        }
        if (!byteBuffer2.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "destination is not a direct buffer");
        }
        int n2 = byteBuffer.position();
        int n3 = byteBuffer.remaining();
        if (n3 % (n = bitShuffleType.getTypeSize()) != 0) {
            throw new IllegalArgumentException("input length must be a multiple of the given type size: " + n);
        }
        if (byteBuffer2.remaining() < n3) {
            throw new IllegalArgumentException("not enough space for output");
        }
        int n4 = impl.shuffleDirectBuffer(byteBuffer, n2, n, n3, byteBuffer2, byteBuffer2.position());
        assert (n4 == n3);
        byteBuffer2.limit(byteBuffer2.position() + n4);
        return n4;
    }

    public static byte[] shuffle(short[] sArray) throws IOException {
        if (sArray.length * 2 < sArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + sArray.length);
        }
        byte[] byArray = new byte[sArray.length * 2];
        int n = impl.shuffle(sArray, 0, 2, sArray.length * 2, byArray, 0);
        assert (n == sArray.length * 2);
        return byArray;
    }

    public static byte[] shuffle(int[] nArray) throws IOException {
        if (nArray.length * 4 < nArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + nArray.length);
        }
        byte[] byArray = new byte[nArray.length * 4];
        int n = impl.shuffle(nArray, 0, 4, nArray.length * 4, byArray, 0);
        assert (n == nArray.length * 4);
        return byArray;
    }

    public static byte[] shuffle(long[] lArray) throws IOException {
        if (lArray.length * 8 < lArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + lArray.length);
        }
        byte[] byArray = new byte[lArray.length * 8];
        int n = impl.shuffle(lArray, 0, 8, lArray.length * 8, byArray, 0);
        assert (n == lArray.length * 8);
        return byArray;
    }

    public static byte[] shuffle(float[] fArray) throws IOException {
        if (fArray.length * 4 < fArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + fArray.length);
        }
        byte[] byArray = new byte[fArray.length * 4];
        int n = impl.shuffle(fArray, 0, 4, fArray.length * 4, byArray, 0);
        assert (n == fArray.length * 4);
        return byArray;
    }

    public static byte[] shuffle(double[] dArray) throws IOException {
        if (dArray.length * 8 < dArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + dArray.length);
        }
        byte[] byArray = new byte[dArray.length * 8];
        int n = impl.shuffle(dArray, 0, 8, dArray.length * 8, byArray, 0);
        assert (n == dArray.length * 8);
        return byArray;
    }

    public static int unshuffle(ByteBuffer byteBuffer, BitShuffleType bitShuffleType, ByteBuffer byteBuffer2) throws IOException {
        int n;
        if (!byteBuffer.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "input is not a direct buffer");
        }
        if (!byteBuffer2.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "destination is not a direct buffer");
        }
        int n2 = byteBuffer.position();
        int n3 = byteBuffer.remaining();
        if (n3 % (n = bitShuffleType.getTypeSize()) != 0) {
            throw new IllegalArgumentException("length of input shuffled data must be a multiple of the given type size: " + n);
        }
        if (byteBuffer2.remaining() < n3) {
            throw new IllegalArgumentException("not enough space for output");
        }
        int n4 = impl.unshuffleDirectBuffer(byteBuffer, n2, n, n3, byteBuffer2, byteBuffer.position());
        assert (n4 == n3);
        byteBuffer.limit(byteBuffer.position() + n4);
        return n4;
    }

    public static short[] unshuffleShortArray(byte[] byArray) throws IOException {
        short[] sArray = new short[byArray.length / 2];
        int n = impl.unshuffle(byArray, 0, 2, byArray.length, sArray, 0);
        assert (n == byArray.length);
        return sArray;
    }

    public static int[] unshuffleIntArray(byte[] byArray) throws IOException {
        int[] nArray = new int[byArray.length / 4];
        int n = impl.unshuffle(byArray, 0, 4, byArray.length, nArray, 0);
        assert (n == byArray.length);
        return nArray;
    }

    public static long[] unshuffleLongArray(byte[] byArray) throws IOException {
        long[] lArray = new long[byArray.length / 8];
        int n = impl.unshuffle(byArray, 0, 8, byArray.length, lArray, 0);
        assert (n == byArray.length);
        return lArray;
    }

    public static float[] unshuffleFloatArray(byte[] byArray) throws IOException {
        float[] fArray = new float[byArray.length / 4];
        int n = impl.unshuffle(byArray, 0, 4, byArray.length, fArray, 0);
        assert (n == byArray.length);
        return fArray;
    }

    public static double[] unshuffleDoubleArray(byte[] byArray) throws IOException {
        double[] dArray = new double[byArray.length / 8];
        int n = impl.unshuffle(byArray, 0, 8, byArray.length, dArray, 0);
        assert (n == byArray.length);
        return dArray;
    }

    static {
        try {
            impl = SnappyLoader.loadBitShuffleApi();
        }
        catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}

