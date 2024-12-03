/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

public final class HexCodec {
    public static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static long lowerHexToUnsignedLong(String lowerHex) {
        int length = lowerHex.length();
        if (length < 1 || length > 32) {
            throw HexCodec.isntLowerHexLong(lowerHex);
        }
        int beginIndex = length > 16 ? length - 16 : 0;
        return HexCodec.lowerHexToUnsignedLong(lowerHex, beginIndex);
    }

    public static long lowerHexToUnsignedLong(String lowerHex, int index) {
        long result = 0L;
        int endIndex = Math.min(index + 16, lowerHex.length());
        while (index < endIndex) {
            char c = lowerHex.charAt(index);
            result <<= 4;
            if (c >= '0' && c <= '9') {
                result |= (long)(c - 48);
            } else if (c >= 'a' && c <= 'f') {
                result |= (long)(c - 97 + 10);
            } else {
                throw HexCodec.isntLowerHexLong(lowerHex);
            }
            ++index;
        }
        return result;
    }

    static NumberFormatException isntLowerHexLong(String lowerHex) {
        throw new NumberFormatException(lowerHex + " should be a 1 to 32 character lower-hex string with no prefix");
    }

    HexCodec() {
    }
}

