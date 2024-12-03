/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.eventstreaming;

import com.amazonaws.util.StringUtils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

final class Utils {
    private Utils() {
    }

    static int toIntExact(long headersLength) {
        if ((long)((int)headersLength) != headersLength) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)headersLength;
    }

    static long toUnsignedLong(int x) {
        return (long)x & 0xFFFFFFFFL;
    }

    static String readShortString(ByteBuffer buf) {
        int length = buf.get() & 0xFF;
        Utils.checkStringBounds(length, 255);
        byte[] bytes = new byte[length];
        buf.get(bytes);
        return new String(bytes, StringUtils.UTF8);
    }

    static String readString(ByteBuffer buf) {
        int length = buf.getShort() & 0xFFFF;
        Utils.checkStringBounds(length, Short.MAX_VALUE);
        byte[] bytes = new byte[length];
        buf.get(bytes);
        return new String(bytes, StringUtils.UTF8);
    }

    static byte[] readBytes(ByteBuffer buf) {
        int length = buf.getShort() & 0xFFFF;
        Utils.checkByteArrayBounds(length);
        byte[] bytes = new byte[length];
        buf.get(bytes);
        return bytes;
    }

    static void writeShortString(DataOutputStream dos, String string) throws IOException {
        byte[] bytes = string.getBytes(StringUtils.UTF8);
        Utils.checkStringBounds(bytes.length, 255);
        dos.writeByte(bytes.length);
        dos.write(bytes);
    }

    static void writeString(DataOutputStream dos, String string) throws IOException {
        byte[] bytes = string.getBytes(StringUtils.UTF8);
        Utils.checkStringBounds(bytes.length, Short.MAX_VALUE);
        Utils.writeBytes(dos, bytes);
    }

    static void writeBytes(DataOutputStream dos, byte[] bytes) throws IOException {
        Utils.checkByteArrayBounds(bytes.length);
        dos.writeShort((short)bytes.length);
        dos.write(bytes);
    }

    private static void checkByteArrayBounds(int length) {
        if (length == 0) {
            throw new IllegalArgumentException("Byte arrays may not be empty");
        }
        if (length > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Illegal byte array length: " + length);
        }
    }

    private static void checkStringBounds(int length, int maxLength) {
        if (length == 0) {
            throw new IllegalArgumentException("Strings may not be empty");
        }
        if (length > maxLength) {
            throw new IllegalArgumentException("Illegal string length: " + length);
        }
    }
}

