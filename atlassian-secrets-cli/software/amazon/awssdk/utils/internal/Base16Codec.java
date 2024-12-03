/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.internal.CodecUtils;

@SdkInternalApi
public final class Base16Codec {
    private static final int OFFSET_OF_LITTLE_A = 87;
    private static final int OFFSET_OF_A = 55;
    private static final int MASK_4BITS = 15;
    private final byte[] alphabets;

    Base16Codec() {
        this(true);
    }

    Base16Codec(boolean upperCase) {
        this.alphabets = upperCase ? CodecUtils.toBytesDirect("0123456789ABCDEF") : CodecUtils.toBytesDirect("0123456789abcdef");
    }

    public byte[] encode(byte[] src) {
        byte[] dest = new byte[src.length * 2];
        int j = 0;
        for (int i = 0; i < src.length; ++i) {
            byte p = src[i];
            dest[j++] = this.alphabets[p >>> 4 & 0xF];
            dest[j++] = this.alphabets[p & 0xF];
        }
        return dest;
    }

    public byte[] decode(byte[] src, int length) {
        if (length % 2 != 0) {
            throw new IllegalArgumentException("Input is expected to be encoded in multiple of 2 bytes but found: " + length);
        }
        byte[] dest = new byte[length / 2];
        int i = 0;
        for (int j = 0; j < dest.length; ++j) {
            dest[j] = (byte)(this.pos(src[i++]) << 4 | this.pos(src[i++]));
        }
        return dest;
    }

    protected int pos(byte in) {
        byte pos = LazyHolder.DECODED[in];
        if (pos > -1) {
            return pos;
        }
        throw new IllegalArgumentException("Invalid base 16 character: '" + (char)in + "'");
    }

    private static class LazyHolder {
        private static final byte[] DECODED = LazyHolder.decodeTable();

        private LazyHolder() {
        }

        private static byte[] decodeTable() {
            byte[] dest = new byte[103];
            for (int i = 0; i <= 102; ++i) {
                dest[i] = i >= 48 && i <= 57 ? (int)(i - 48) : (i >= 65 && i <= 70 ? (int)(i - 55) : (i >= 97 ? (int)(i - 87) : -1));
            }
            return dest;
        }
    }
}

