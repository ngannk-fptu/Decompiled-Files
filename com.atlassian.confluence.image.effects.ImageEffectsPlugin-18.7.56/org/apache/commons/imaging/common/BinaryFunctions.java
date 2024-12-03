/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryConstant;

public final class BinaryFunctions {
    private static final Logger LOGGER = Logger.getLogger(BinaryFunctions.class.getName());

    private BinaryFunctions() {
    }

    public static boolean startsWith(byte[] haystack, byte[] needle) {
        if (needle == null) {
            return false;
        }
        if (haystack == null) {
            return false;
        }
        if (needle.length > haystack.length) {
            return false;
        }
        for (int i = 0; i < needle.length; ++i) {
            if (needle[i] == haystack[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean startsWith(byte[] haystack, BinaryConstant needle) {
        if (haystack == null || haystack.length < needle.size()) {
            return false;
        }
        for (int i = 0; i < needle.size(); ++i) {
            if (haystack[i] == needle.get(i)) continue;
            return false;
        }
        return true;
    }

    public static byte readByte(String name, InputStream is, String exception) throws IOException {
        int result = is.read();
        if (result < 0) {
            throw new IOException(exception);
        }
        return (byte)(0xFF & result);
    }

    public static byte[] readBytes(String name, InputStream is, int length) throws IOException {
        String exception = name + " could not be read.";
        return BinaryFunctions.readBytes(name, is, length, exception);
    }

    public static byte[] readBytes(String name, InputStream is, int length, String exception) throws IOException {
        int count;
        byte[] result = new byte[length];
        for (int read = 0; read < length; read += count) {
            count = is.read(result, read, length - read);
            if (count >= 0) continue;
            throw new IOException(exception + " count: " + count + " read: " + read + " length: " + length);
        }
        return result;
    }

    public static byte[] readBytes(InputStream is, int count) throws IOException {
        return BinaryFunctions.readBytes("", is, count, "Unexpected EOF");
    }

    public static void readAndVerifyBytes(InputStream is, byte[] expected, String exception) throws ImageReadException, IOException {
        for (byte element : expected) {
            int data = is.read();
            byte b = (byte)(0xFF & data);
            if (data < 0) {
                throw new ImageReadException("Unexpected EOF.");
            }
            if (b == element) continue;
            throw new ImageReadException(exception);
        }
    }

    public static void readAndVerifyBytes(InputStream is, BinaryConstant expected, String exception) throws ImageReadException, IOException {
        for (int i = 0; i < expected.size(); ++i) {
            int data = is.read();
            byte b = (byte)(0xFF & data);
            if (data < 0) {
                throw new ImageReadException("Unexpected EOF.");
            }
            if (b == expected.get(i)) continue;
            throw new ImageReadException(exception);
        }
    }

    public static void skipBytes(InputStream is, long length, String exception) throws IOException {
        long skipped;
        for (long total = 0L; length != total; total += skipped) {
            skipped = is.skip(length - total);
            if (skipped >= 1L) continue;
            throw new IOException(exception + " (" + skipped + ")");
        }
    }

    public static byte[] remainingBytes(String name, byte[] bytes, int count) {
        return BinaryFunctions.slice(bytes, count, bytes.length - count);
    }

    public static byte[] slice(byte[] bytes, int start, int count) {
        byte[] result = new byte[count];
        System.arraycopy(bytes, start, result, 0, count);
        return result;
    }

    public static byte[] head(byte[] bytes, int count) {
        if (count > bytes.length) {
            count = bytes.length;
        }
        return BinaryFunctions.slice(bytes, 0, count);
    }

    public static boolean compareBytes(byte[] a, int aStart, byte[] b, int bStart, int length) {
        if (a.length < aStart + length) {
            return false;
        }
        if (b.length < bStart + length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (a[aStart + i] == b[bStart + i]) continue;
            return false;
        }
        return true;
    }

    public static int read4Bytes(String name, InputStream is, String exception, ByteOrder byteOrder) throws IOException {
        int byte3;
        int byte2;
        int byte1;
        int byte0 = is.read();
        if ((byte0 | (byte1 = is.read()) | (byte2 = is.read()) | (byte3 = is.read())) < 0) {
            throw new IOException(exception);
        }
        int result = byteOrder == ByteOrder.BIG_ENDIAN ? byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3 << 0 : byte3 << 24 | byte2 << 16 | byte1 << 8 | byte0 << 0;
        return result;
    }

    public static int read3Bytes(String name, InputStream is, String exception, ByteOrder byteOrder) throws IOException {
        int byte2;
        int byte1;
        int byte0 = is.read();
        if ((byte0 | (byte1 = is.read()) | (byte2 = is.read())) < 0) {
            throw new IOException(exception);
        }
        int result = byteOrder == ByteOrder.BIG_ENDIAN ? byte0 << 16 | byte1 << 8 | byte2 << 0 : byte2 << 16 | byte1 << 8 | byte0 << 0;
        return result;
    }

    public static int read2Bytes(String name, InputStream is, String exception, ByteOrder byteOrder) throws IOException {
        int byte1;
        int byte0 = is.read();
        if ((byte0 | (byte1 = is.read())) < 0) {
            throw new IOException(exception);
        }
        int result = byteOrder == ByteOrder.BIG_ENDIAN ? byte0 << 8 | byte1 : byte1 << 8 | byte0;
        return result;
    }

    public static void printCharQuad(String msg, int i) {
        LOGGER.finest(msg + ": '" + (char)(0xFF & i >> 24) + (char)(0xFF & i >> 16) + (char)(0xFF & i >> 8) + (char)(0xFF & i >> 0) + "'");
    }

    public static void printCharQuad(PrintWriter pw, String msg, int i) {
        pw.println(msg + ": '" + (char)(0xFF & i >> 24) + (char)(0xFF & i >> 16) + (char)(0xFF & i >> 8) + (char)(0xFF & i >> 0) + "'");
    }

    public static void printByteBits(String msg, byte i) {
        LOGGER.finest(msg + ": '" + Integer.toBinaryString(0xFF & i));
    }

    public static int charsToQuad(char c1, char c2, char c3, char c4) {
        return (0xFF & c1) << 24 | (0xFF & c2) << 16 | (0xFF & c3) << 8 | (0xFF & c4) << 0;
    }

    public static byte[] quadsToByteArray(int quad) {
        byte[] arr = new byte[]{(byte)(quad >> 24), (byte)(quad >> 16), (byte)(quad >> 8), (byte)quad};
        return arr;
    }

    public static boolean searchQuad(int quad, InputStream bis) throws IOException {
        byte[] needle = BinaryFunctions.quadsToByteArray(quad);
        byte b = -1;
        int position = 0;
        while ((b = bis.read()) != -1) {
            if (needle[position] == b) {
                if (++position != needle.length) continue;
                return true;
            }
            position = 0;
        }
        return false;
    }

    public static int findNull(byte[] src) {
        return BinaryFunctions.findNull(src, 0);
    }

    public static int findNull(byte[] src, int start) {
        for (int i = start; i < src.length; ++i) {
            if (src[i] != 0) continue;
            return i;
        }
        return -1;
    }

    public static byte[] getRAFBytes(RandomAccessFile raf, long pos, int length, String exception) throws IOException {
        int count;
        byte[] result = new byte[length];
        raf.seek(pos);
        for (int read = 0; read < length; read += count) {
            count = raf.read(result, read, length - read);
            if (count >= 0) continue;
            throw new IOException(exception);
        }
        return result;
    }

    public static void skipBytes(InputStream is, long length) throws IOException {
        BinaryFunctions.skipBytes(is, length, "Couldn't skip bytes");
    }

    public static void copyStreamToStream(InputStream is, OutputStream os) throws IOException {
        int read;
        byte[] buffer = new byte[1024];
        while ((read = is.read(buffer)) > 0) {
            os.write(buffer, 0, read);
        }
    }

    public static byte[] getStreamBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BinaryFunctions.copyStreamToStream(is, os);
        return os.toByteArray();
    }
}

