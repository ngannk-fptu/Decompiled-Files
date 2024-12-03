/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Properties;
import org.xerial.snappy.SnappyApi;
import org.xerial.snappy.SnappyError;
import org.xerial.snappy.SnappyErrorCode;
import org.xerial.snappy.SnappyLoader;

public class Snappy {
    private static SnappyApi impl;

    public static void cleanUp() {
        SnappyLoader.cleanUpExtractedNativeLib();
        SnappyLoader.setSnappyApi(null);
    }

    static void init() {
        try {
            impl = SnappyLoader.loadSnappyApi();
        }
        catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static void arrayCopy(Object object, int n, int n2, Object object2, int n3) throws IOException {
        impl.arrayCopy(object, n, n2, object2, n3);
    }

    public static byte[] compress(byte[] byArray) throws IOException {
        return Snappy.rawCompress(byArray, byArray.length);
    }

    public static int compress(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws IOException {
        return Snappy.rawCompress(byArray, n, n2, byArray2, n3);
    }

    public static int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws IOException {
        if (!byteBuffer.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "input is not a direct buffer");
        }
        if (!byteBuffer2.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "destination is not a direct buffer");
        }
        int n = byteBuffer.position();
        int n2 = byteBuffer.remaining();
        int n3 = byteBuffer2.position();
        int n4 = impl.rawCompress(byteBuffer, n, n2, byteBuffer2, n3);
        ((Buffer)byteBuffer2).limit(n3 + n4);
        return n4;
    }

    public static byte[] compress(char[] cArray) throws IOException {
        int n = cArray.length * 2;
        if (n < cArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + cArray.length);
        }
        return Snappy.rawCompress(cArray, n);
    }

    public static byte[] compress(double[] dArray) throws IOException {
        int n = dArray.length * 8;
        if (n < dArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + dArray.length);
        }
        return Snappy.rawCompress(dArray, n);
    }

    public static byte[] compress(float[] fArray) throws IOException {
        int n = fArray.length * 4;
        if (n < fArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + fArray.length);
        }
        return Snappy.rawCompress(fArray, n);
    }

    public static byte[] compress(int[] nArray) throws IOException {
        int n = nArray.length * 4;
        if (n < nArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + nArray.length);
        }
        return Snappy.rawCompress(nArray, n);
    }

    public static byte[] compress(long[] lArray) throws IOException {
        int n = lArray.length * 8;
        if (n < lArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + lArray.length);
        }
        return Snappy.rawCompress(lArray, n);
    }

    public static byte[] compress(short[] sArray) throws IOException {
        int n = sArray.length * 2;
        if (n < sArray.length) {
            throw new SnappyError(SnappyErrorCode.TOO_LARGE_INPUT, "input array size is too large: " + sArray.length);
        }
        return Snappy.rawCompress(sArray, n);
    }

    public static byte[] compress(String string) throws IOException {
        try {
            return Snappy.compress(string, "UTF-8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new IllegalStateException("UTF-8 encoder is not found");
        }
    }

    public static byte[] compress(String string, String string2) throws UnsupportedEncodingException, IOException {
        byte[] byArray = string.getBytes(string2);
        return Snappy.compress(byArray);
    }

    public static byte[] compress(String string, Charset charset) throws IOException {
        byte[] byArray = string.getBytes(charset);
        return Snappy.compress(byArray);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getNativeLibraryVersion() {
        String string;
        block7: {
            URL uRL = SnappyLoader.class.getResource("/org/xerial/snappy/VERSION");
            string = "unknown";
            try {
                if (uRL == null) break block7;
                try (InputStream inputStream = null;){
                    Properties properties = new Properties();
                    inputStream = uRL.openStream();
                    properties.load(inputStream);
                    string = properties.getProperty("version", string);
                    if (string.equals("unknown")) {
                        string = properties.getProperty("SNAPPY_VERSION", string);
                    }
                    string = string.trim().replaceAll("[^0-9\\.]", "");
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        return string;
    }

    public static boolean isValidCompressedBuffer(byte[] byArray, int n, int n2) throws IOException {
        if (byArray == null) {
            throw new NullPointerException("input is null");
        }
        return impl.isValidCompressedBuffer(byArray, n, n2);
    }

    public static boolean isValidCompressedBuffer(byte[] byArray) throws IOException {
        return Snappy.isValidCompressedBuffer(byArray, 0, byArray.length);
    }

    public static boolean isValidCompressedBuffer(ByteBuffer byteBuffer) throws IOException {
        return impl.isValidCompressedBuffer(byteBuffer, byteBuffer.position(), byteBuffer.remaining());
    }

    public static boolean isValidCompressedBuffer(long l, long l2, long l3) throws IOException {
        return impl.isValidCompressedBuffer(l, l2, l3);
    }

    public static int maxCompressedLength(int n) {
        return impl.maxCompressedLength(n);
    }

    public static long rawCompress(long l, long l2, long l3) throws IOException {
        return impl.rawCompress(l, l2, l3);
    }

    public static long rawUncompress(long l, long l2, long l3) throws IOException {
        return impl.rawUncompress(l, l2, l3);
    }

    public static byte[] rawCompress(Object object, int n) throws IOException {
        byte[] byArray = new byte[Snappy.maxCompressedLength(n)];
        int n2 = impl.rawCompress(object, 0, n, byArray, 0);
        byte[] byArray2 = new byte[n2];
        System.arraycopy(byArray, 0, byArray2, 0, n2);
        return byArray2;
    }

    public static int rawCompress(Object object, int n, int n2, byte[] byArray, int n3) throws IOException {
        if (object == null || byArray == null) {
            throw new NullPointerException("input or output is null");
        }
        int n4 = impl.rawCompress(object, n, n2, byArray, n3);
        return n4;
    }

    public static int rawUncompress(byte[] byArray, int n, int n2, Object object, int n3) throws IOException {
        if (byArray == null || object == null) {
            throw new NullPointerException("input or output is null");
        }
        return impl.rawUncompress(byArray, n, n2, object, n3);
    }

    public static byte[] uncompress(byte[] byArray) throws IOException {
        byte[] byArray2 = new byte[Snappy.uncompressedLength(byArray)];
        Snappy.uncompress(byArray, 0, byArray.length, byArray2, 0);
        return byArray2;
    }

    public static int uncompress(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws IOException {
        return Snappy.rawUncompress(byArray, n, n2, byArray2, n3);
    }

    public static int uncompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws IOException {
        if (!byteBuffer.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "input is not a direct buffer");
        }
        if (!byteBuffer2.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "destination is not a direct buffer");
        }
        int n = byteBuffer.position();
        int n2 = byteBuffer.remaining();
        int n3 = byteBuffer2.position();
        int n4 = impl.rawUncompress(byteBuffer, n, n2, byteBuffer2, n3);
        byteBuffer2.limit(n3 + n4);
        return n4;
    }

    public static char[] uncompressCharArray(byte[] byArray) throws IOException {
        return Snappy.uncompressCharArray(byArray, 0, byArray.length);
    }

    public static char[] uncompressCharArray(byte[] byArray, int n, int n2) throws IOException {
        int n3 = Snappy.uncompressedLength(byArray, n, n2);
        char[] cArray = new char[n3 / 2];
        impl.rawUncompress(byArray, n, n2, cArray, 0);
        return cArray;
    }

    public static double[] uncompressDoubleArray(byte[] byArray) throws IOException {
        return Snappy.uncompressDoubleArray(byArray, 0, byArray.length);
    }

    public static double[] uncompressDoubleArray(byte[] byArray, int n, int n2) throws IOException {
        int n3 = Snappy.uncompressedLength(byArray, n, n2);
        double[] dArray = new double[n3 / 8];
        impl.rawUncompress(byArray, n, n2, dArray, 0);
        return dArray;
    }

    public static int uncompressedLength(byte[] byArray) throws IOException {
        return impl.uncompressedLength(byArray, 0, byArray.length);
    }

    public static int uncompressedLength(byte[] byArray, int n, int n2) throws IOException {
        if (byArray == null) {
            throw new NullPointerException("input is null");
        }
        return impl.uncompressedLength(byArray, n, n2);
    }

    public static int uncompressedLength(ByteBuffer byteBuffer) throws IOException {
        if (!byteBuffer.isDirect()) {
            throw new SnappyError(SnappyErrorCode.NOT_A_DIRECT_BUFFER, "input is not a direct buffer");
        }
        return impl.uncompressedLength(byteBuffer, byteBuffer.position(), byteBuffer.remaining());
    }

    public static long uncompressedLength(long l, long l2) throws IOException {
        return impl.uncompressedLength(l, l2);
    }

    public static float[] uncompressFloatArray(byte[] byArray) throws IOException {
        return Snappy.uncompressFloatArray(byArray, 0, byArray.length);
    }

    public static float[] uncompressFloatArray(byte[] byArray, int n, int n2) throws IOException {
        int n3 = Snappy.uncompressedLength(byArray, n, n2);
        float[] fArray = new float[n3 / 4];
        impl.rawUncompress(byArray, n, n2, fArray, 0);
        return fArray;
    }

    public static int[] uncompressIntArray(byte[] byArray) throws IOException {
        return Snappy.uncompressIntArray(byArray, 0, byArray.length);
    }

    public static int[] uncompressIntArray(byte[] byArray, int n, int n2) throws IOException {
        int n3 = Snappy.uncompressedLength(byArray, n, n2);
        int[] nArray = new int[n3 / 4];
        impl.rawUncompress(byArray, n, n2, nArray, 0);
        return nArray;
    }

    public static long[] uncompressLongArray(byte[] byArray) throws IOException {
        return Snappy.uncompressLongArray(byArray, 0, byArray.length);
    }

    public static long[] uncompressLongArray(byte[] byArray, int n, int n2) throws IOException {
        int n3 = Snappy.uncompressedLength(byArray, n, n2);
        long[] lArray = new long[n3 / 8];
        impl.rawUncompress(byArray, n, n2, lArray, 0);
        return lArray;
    }

    public static short[] uncompressShortArray(byte[] byArray) throws IOException {
        return Snappy.uncompressShortArray(byArray, 0, byArray.length);
    }

    public static short[] uncompressShortArray(byte[] byArray, int n, int n2) throws IOException {
        int n3 = Snappy.uncompressedLength(byArray, n, n2);
        short[] sArray = new short[n3 / 2];
        impl.rawUncompress(byArray, n, n2, sArray, 0);
        return sArray;
    }

    public static String uncompressString(byte[] byArray) throws IOException {
        try {
            return Snappy.uncompressString(byArray, "UTF-8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new IllegalStateException("UTF-8 decoder is not found");
        }
    }

    public static String uncompressString(byte[] byArray, int n, int n2) throws IOException {
        try {
            return Snappy.uncompressString(byArray, n, n2, "UTF-8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new IllegalStateException("UTF-8 decoder is not found");
        }
    }

    public static String uncompressString(byte[] byArray, int n, int n2, String string) throws IOException, UnsupportedEncodingException {
        byte[] byArray2 = new byte[Snappy.uncompressedLength(byArray, n, n2)];
        Snappy.uncompress(byArray, n, n2, byArray2, 0);
        return new String(byArray2, string);
    }

    public static String uncompressString(byte[] byArray, int n, int n2, Charset charset) throws IOException, UnsupportedEncodingException {
        byte[] byArray2 = new byte[Snappy.uncompressedLength(byArray, n, n2)];
        Snappy.uncompress(byArray, n, n2, byArray2, 0);
        return new String(byArray2, charset);
    }

    public static String uncompressString(byte[] byArray, String string) throws IOException, UnsupportedEncodingException {
        byte[] byArray2 = Snappy.uncompress(byArray);
        return new String(byArray2, string);
    }

    public static String uncompressString(byte[] byArray, Charset charset) throws IOException, UnsupportedEncodingException {
        byte[] byArray2 = Snappy.uncompress(byArray);
        return new String(byArray2, charset);
    }

    static {
        Snappy.init();
    }
}

