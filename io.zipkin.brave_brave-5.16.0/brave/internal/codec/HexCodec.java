/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.codec;

import brave.internal.RecyclableBuffers;

public final class HexCodec {
    static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static long lowerHexToUnsignedLong(CharSequence lowerHex) {
        int length = lowerHex.length();
        if (length < 1 || length > 32) {
            throw HexCodec.isntLowerHexLong(lowerHex);
        }
        int beginIndex = length > 16 ? length - 16 : 0;
        return HexCodec.lowerHexToUnsignedLong(lowerHex, beginIndex);
    }

    public static long lowerHexToUnsignedLong(CharSequence value, int beginIndex) {
        int endIndex = Math.min(beginIndex + 16, value.length());
        long result = HexCodec.lenientLowerHexToUnsignedLong(value, beginIndex, endIndex);
        if (result == 0L) {
            throw HexCodec.isntLowerHexLong(value);
        }
        return result;
    }

    public static long lenientLowerHexToUnsignedLong(CharSequence value, int beginIndex, int endIndex) {
        long result = 0L;
        int pos = beginIndex;
        while (pos < endIndex) {
            char c = value.charAt(pos++);
            result <<= 4;
            if (c >= '0' && c <= '9') {
                result |= (long)(c - 48);
                continue;
            }
            if (c >= 'a' && c <= 'f') {
                result |= (long)(c - 97 + 10);
                continue;
            }
            return 0L;
        }
        return result;
    }

    static NumberFormatException isntLowerHexLong(CharSequence lowerHex) {
        throw new NumberFormatException(lowerHex + " should be a 1 to 32 character lower-hex string with no prefix");
    }

    public static String toLowerHex(long v) {
        char[] data = RecyclableBuffers.parseBuffer();
        HexCodec.writeHexLong(data, 0, v);
        return new String(data, 0, 16);
    }

    public static void writeHexLong(char[] data, int pos, long v) {
        HexCodec.writeHexByte(data, pos + 0, (byte)(v >>> 56 & 0xFFL));
        HexCodec.writeHexByte(data, pos + 2, (byte)(v >>> 48 & 0xFFL));
        HexCodec.writeHexByte(data, pos + 4, (byte)(v >>> 40 & 0xFFL));
        HexCodec.writeHexByte(data, pos + 6, (byte)(v >>> 32 & 0xFFL));
        HexCodec.writeHexByte(data, pos + 8, (byte)(v >>> 24 & 0xFFL));
        HexCodec.writeHexByte(data, pos + 10, (byte)(v >>> 16 & 0xFFL));
        HexCodec.writeHexByte(data, pos + 12, (byte)(v >>> 8 & 0xFFL));
        HexCodec.writeHexByte(data, pos + 14, (byte)(v & 0xFFL));
    }

    static void writeHexByte(char[] data, int pos, byte b) {
        data[pos + 0] = HEX_DIGITS[b >> 4 & 0xF];
        data[pos + 1] = HEX_DIGITS[b & 0xF];
    }

    HexCodec() {
    }
}

