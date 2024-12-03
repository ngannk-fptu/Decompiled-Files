/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import java.nio.ByteBuffer;

class ByteUtil {
    private ByteUtil() {
    }

    static boolean isBit7Set(byte input) {
        return (input & 0x80) != 0;
    }

    static int get31Bits(byte[] input, int firstByte) {
        return ((input[firstByte] & 0x7F) << 24) + ((input[firstByte + 1] & 0xFF) << 16) + ((input[firstByte + 2] & 0xFF) << 8) + (input[firstByte + 3] & 0xFF);
    }

    static int get31Bits(ByteBuffer input, int firstByte) {
        return ((input.get(firstByte) & 0x7F) << 24) + ((input.get(firstByte + 1) & 0xFF) << 16) + ((input.get(firstByte + 2) & 0xFF) << 8) + (input.get(firstByte + 3) & 0xFF);
    }

    static void set31Bits(byte[] output, int firstByte, int value) {
        output[firstByte] = (byte)((value & 0x7F000000) >> 24);
        output[firstByte + 1] = (byte)((value & 0xFF0000) >> 16);
        output[firstByte + 2] = (byte)((value & 0xFF00) >> 8);
        output[firstByte + 3] = (byte)(value & 0xFF);
    }

    static int getOneByte(byte[] input, int pos) {
        return input[pos] & 0xFF;
    }

    static int getOneByte(ByteBuffer input, int pos) {
        return input.get(pos) & 0xFF;
    }

    static int getTwoBytes(byte[] input, int firstByte) {
        return ((input[firstByte] & 0xFF) << 8) + (input[firstByte + 1] & 0xFF);
    }

    static int getThreeBytes(byte[] input, int firstByte) {
        return ((input[firstByte] & 0xFF) << 16) + ((input[firstByte + 1] & 0xFF) << 8) + (input[firstByte + 2] & 0xFF);
    }

    static int getThreeBytes(ByteBuffer input, int firstByte) {
        return ((input.get(firstByte) & 0xFF) << 16) + ((input.get(firstByte + 1) & 0xFF) << 8) + (input.get(firstByte + 2) & 0xFF);
    }

    static void setTwoBytes(byte[] output, int firstByte, int value) {
        output[firstByte] = (byte)((value & 0xFF00) >> 8);
        output[firstByte + 1] = (byte)(value & 0xFF);
    }

    static void setThreeBytes(byte[] output, int firstByte, int value) {
        output[firstByte] = (byte)((value & 0xFF0000) >> 16);
        output[firstByte + 1] = (byte)((value & 0xFF00) >> 8);
        output[firstByte + 2] = (byte)(value & 0xFF);
    }

    static long getFourBytes(byte[] input, int firstByte) {
        return ((long)(input[firstByte] & 0xFF) << 24) + (long)((input[firstByte + 1] & 0xFF) << 16) + (long)((input[firstByte + 2] & 0xFF) << 8) + (long)(input[firstByte + 3] & 0xFF);
    }

    static void setFourBytes(byte[] output, int firstByte, long value) {
        output[firstByte] = (byte)((value & 0xFFFFFFFFFF000000L) >> 24);
        output[firstByte + 1] = (byte)((value & 0xFF0000L) >> 16);
        output[firstByte + 2] = (byte)((value & 0xFF00L) >> 8);
        output[firstByte + 3] = (byte)(value & 0xFFL);
    }
}

