/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.BytesRef;
import com.atlassian.lucene36.util.CharsRef;

public final class UnicodeUtil {
    public static final int UNI_SUR_HIGH_START = 55296;
    public static final int UNI_SUR_HIGH_END = 56319;
    public static final int UNI_SUR_LOW_START = 56320;
    public static final int UNI_SUR_LOW_END = 57343;
    public static final int UNI_REPLACEMENT_CHAR = 65533;
    private static final long UNI_MAX_BMP = 65535L;
    private static final int HALF_BASE = 65536;
    private static final long HALF_SHIFT = 10L;
    private static final long HALF_MASK = 1023L;
    private static final int SURROGATE_OFFSET = -56613888;
    private static final int LEAD_SURROGATE_SHIFT_ = 10;
    private static final int TRAIL_SURROGATE_MASK_ = 1023;
    private static final int TRAIL_SURROGATE_MIN_VALUE = 56320;
    private static final int LEAD_SURROGATE_MIN_VALUE = 55296;
    private static final int SUPPLEMENTARY_MIN_VALUE = 65536;
    private static final int LEAD_SURROGATE_OFFSET_ = 55232;

    private UnicodeUtil() {
    }

    public static int UTF16toUTF8WithHash(char[] source, int offset, int length, BytesRef result) {
        int hash = 0;
        int upto = 0;
        int i = offset;
        int end = offset + length;
        byte[] out = result.bytes;
        int maxLen = length * 4;
        if (out.length < maxLen) {
            out = result.bytes = new byte[ArrayUtil.oversize(maxLen, 1)];
        }
        result.offset = 0;
        while (i < end) {
            int utf32;
            char code;
            if ((code = source[i++]) < '\u0080') {
                int n = upto++;
                byte by = (byte)code;
                out[n] = by;
                hash = 31 * hash + by;
                continue;
            }
            if (code < '\u0800') {
                int n = upto++;
                byte by = (byte)(0xC0 | code >> 6);
                out[n] = by;
                hash = 31 * hash + by;
                int n2 = upto++;
                byte by2 = (byte)(0x80 | code & 0x3F);
                out[n2] = by2;
                hash = 31 * hash + by2;
                continue;
            }
            if (code < '\ud800' || code > '\udfff') {
                int n = upto++;
                byte by = (byte)(0xE0 | code >> 12);
                out[n] = by;
                hash = 31 * hash + by;
                int n3 = upto++;
                byte by3 = (byte)(0x80 | code >> 6 & 0x3F);
                out[n3] = by3;
                hash = 31 * hash + by3;
                int n4 = upto++;
                byte by4 = (byte)(0x80 | code & 0x3F);
                out[n4] = by4;
                hash = 31 * hash + by4;
                continue;
            }
            if (code < '\udc00' && i < end && (utf32 = source[i]) >= 56320 && utf32 <= 57343) {
                utf32 = (code << 10) + utf32 + -56613888;
                ++i;
                int n = upto++;
                byte by = (byte)(0xF0 | utf32 >> 18);
                out[n] = by;
                hash = 31 * hash + by;
                int n5 = upto++;
                byte by5 = (byte)(0x80 | utf32 >> 12 & 0x3F);
                out[n5] = by5;
                hash = 31 * hash + by5;
                int n6 = upto++;
                byte by6 = (byte)(0x80 | utf32 >> 6 & 0x3F);
                out[n6] = by6;
                hash = 31 * hash + by6;
                int n7 = upto++;
                byte by7 = (byte)(0x80 | utf32 & 0x3F);
                out[n7] = by7;
                hash = 31 * hash + by7;
                continue;
            }
            out[upto++] = -17;
            hash = 31 * hash + -17;
            out[upto++] = -65;
            hash = 31 * hash + -65;
            out[upto++] = -67;
            hash = 31 * hash + -67;
        }
        result.length = upto;
        return hash;
    }

    public static void UTF16toUTF8(char[] source, int offset, UTF8Result result) {
        int upto = 0;
        int i = offset;
        byte[] out = result.result;
        while (true) {
            int utf32;
            char code = source[i++];
            if (upto + 4 > out.length) {
                out = result.result = ArrayUtil.grow(out, upto + 4);
            }
            if (code < '\u0080') {
                out[upto++] = (byte)code;
                continue;
            }
            if (code < '\u0800') {
                out[upto++] = (byte)(0xC0 | code >> 6);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\ud800' || code > '\udfff') {
                if (code == '\uffff') break;
                out[upto++] = (byte)(0xE0 | code >> 12);
                out[upto++] = (byte)(0x80 | code >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\udc00' && source[i] != '\uffff' && (utf32 = source[i]) >= 56320 && utf32 <= 57343) {
                utf32 = (code - 55232 << 10) + (utf32 & 0x3FF);
                ++i;
                out[upto++] = (byte)(0xF0 | utf32 >> 18);
                out[upto++] = (byte)(0x80 | utf32 >> 12 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 & 0x3F);
                continue;
            }
            out[upto++] = -17;
            out[upto++] = -65;
            out[upto++] = -67;
        }
        result.length = upto;
    }

    public static void UTF16toUTF8(char[] source, int offset, int length, UTF8Result result) {
        int upto = 0;
        int i = offset;
        int end = offset + length;
        byte[] out = result.result;
        while (i < end) {
            int utf32;
            char code = source[i++];
            if (upto + 4 > out.length) {
                out = result.result = ArrayUtil.grow(out, upto + 4);
            }
            if (code < '\u0080') {
                out[upto++] = (byte)code;
                continue;
            }
            if (code < '\u0800') {
                out[upto++] = (byte)(0xC0 | code >> 6);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\ud800' || code > '\udfff') {
                out[upto++] = (byte)(0xE0 | code >> 12);
                out[upto++] = (byte)(0x80 | code >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\udc00' && i < end && source[i] != '\uffff' && (utf32 = source[i]) >= 56320 && utf32 <= 57343) {
                utf32 = (code - 55232 << 10) + (utf32 & 0x3FF);
                ++i;
                out[upto++] = (byte)(0xF0 | utf32 >> 18);
                out[upto++] = (byte)(0x80 | utf32 >> 12 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 & 0x3F);
                continue;
            }
            out[upto++] = -17;
            out[upto++] = -65;
            out[upto++] = -67;
        }
        result.length = upto;
    }

    public static void UTF16toUTF8(String s, int offset, int length, UTF8Result result) {
        int end = offset + length;
        byte[] out = result.result;
        int upto = 0;
        for (int i = offset; i < end; ++i) {
            int utf32;
            char code = s.charAt(i);
            if (upto + 4 > out.length) {
                out = result.result = ArrayUtil.grow(out, upto + 4);
            }
            if (code < '\u0080') {
                out[upto++] = (byte)code;
                continue;
            }
            if (code < '\u0800') {
                out[upto++] = (byte)(0xC0 | code >> 6);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\ud800' || code > '\udfff') {
                out[upto++] = (byte)(0xE0 | code >> 12);
                out[upto++] = (byte)(0x80 | code >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\udc00' && i < end - 1 && (utf32 = s.charAt(i + 1)) >= 56320 && utf32 <= 57343) {
                utf32 = (code - 55232 << 10) + (utf32 & 0x3FF);
                ++i;
                out[upto++] = (byte)(0xF0 | utf32 >> 18);
                out[upto++] = (byte)(0x80 | utf32 >> 12 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 & 0x3F);
                continue;
            }
            out[upto++] = -17;
            out[upto++] = -65;
            out[upto++] = -67;
        }
        result.length = upto;
    }

    public static void UTF16toUTF8(CharSequence s, int offset, int length, BytesRef result) {
        int end = offset + length;
        byte[] out = result.bytes;
        result.offset = 0;
        int maxLen = length * 4;
        if (out.length < maxLen) {
            out = result.bytes = new byte[maxLen];
        }
        int upto = 0;
        for (int i = offset; i < end; ++i) {
            int utf32;
            char code = s.charAt(i);
            if (code < '\u0080') {
                out[upto++] = (byte)code;
                continue;
            }
            if (code < '\u0800') {
                out[upto++] = (byte)(0xC0 | code >> 6);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\ud800' || code > '\udfff') {
                out[upto++] = (byte)(0xE0 | code >> 12);
                out[upto++] = (byte)(0x80 | code >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\udc00' && i < end - 1 && (utf32 = s.charAt(i + 1)) >= 56320 && utf32 <= 57343) {
                utf32 = (code << 10) + utf32 + -56613888;
                ++i;
                out[upto++] = (byte)(0xF0 | utf32 >> 18);
                out[upto++] = (byte)(0x80 | utf32 >> 12 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 & 0x3F);
                continue;
            }
            out[upto++] = -17;
            out[upto++] = -65;
            out[upto++] = -67;
        }
        result.length = upto;
    }

    public static void UTF16toUTF8(char[] source, int offset, int length, BytesRef result) {
        int upto = 0;
        int i = offset;
        int end = offset + length;
        byte[] out = result.bytes;
        int maxLen = length * 4;
        if (out.length < maxLen) {
            out = result.bytes = new byte[maxLen];
        }
        result.offset = 0;
        while (i < end) {
            int utf32;
            char code;
            if ((code = source[i++]) < '\u0080') {
                out[upto++] = (byte)code;
                continue;
            }
            if (code < '\u0800') {
                out[upto++] = (byte)(0xC0 | code >> 6);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\ud800' || code > '\udfff') {
                out[upto++] = (byte)(0xE0 | code >> 12);
                out[upto++] = (byte)(0x80 | code >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | code & 0x3F);
                continue;
            }
            if (code < '\udc00' && i < end && (utf32 = source[i]) >= 56320 && utf32 <= 57343) {
                utf32 = (code << 10) + utf32 + -56613888;
                ++i;
                out[upto++] = (byte)(0xF0 | utf32 >> 18);
                out[upto++] = (byte)(0x80 | utf32 >> 12 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 >> 6 & 0x3F);
                out[upto++] = (byte)(0x80 | utf32 & 0x3F);
                continue;
            }
            out[upto++] = -17;
            out[upto++] = -65;
            out[upto++] = -67;
        }
        result.length = upto;
    }

    public static void UTF8toUTF16(byte[] utf8, int offset, int length, UTF16Result result) {
        int end = offset + length;
        char[] out = result.result;
        if (result.offsets.length <= end) {
            result.offsets = ArrayUtil.grow(result.offsets, end + 1);
        }
        int[] offsets = result.offsets;
        int upto = offset;
        while (offsets[upto] == -1) {
            --upto;
        }
        int outUpto = offsets[upto];
        if (outUpto + length >= out.length) {
            out = result.result = ArrayUtil.grow(out, outUpto + length + 1);
        }
        while (upto < end) {
            int ch;
            int b = utf8[upto] & 0xFF;
            offsets[upto++] = outUpto;
            if (b < 192) {
                assert (b < 128);
                ch = b;
            } else if (b < 224) {
                ch = ((b & 0x1F) << 6) + (utf8[upto] & 0x3F);
                offsets[upto++] = -1;
            } else if (b < 240) {
                ch = ((b & 0xF) << 12) + ((utf8[upto] & 0x3F) << 6) + (utf8[upto + 1] & 0x3F);
                offsets[upto++] = -1;
                offsets[upto++] = -1;
            } else {
                assert (b < 248);
                ch = ((b & 7) << 18) + ((utf8[upto] & 0x3F) << 12) + ((utf8[upto + 1] & 0x3F) << 6) + (utf8[upto + 2] & 0x3F);
                offsets[upto++] = -1;
                offsets[upto++] = -1;
                offsets[upto++] = -1;
            }
            if ((long)ch <= 65535L) {
                out[outUpto++] = (char)ch;
                continue;
            }
            int chHalf = ch - 65536;
            out[outUpto++] = (char)((chHalf >> 10) + 55296);
            out[outUpto++] = (char)(((long)chHalf & 0x3FFL) + 56320L);
        }
        offsets[upto] = outUpto;
        result.length = outUpto;
    }

    public static String newString(int[] codePoints, int offset, int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
        char[] chars = new char[count];
        int w = 0;
        int e = offset + count;
        block2: for (int r = offset; r < e; ++r) {
            int cp = codePoints[r];
            if (cp < 0 || cp > 0x10FFFF) {
                throw new IllegalArgumentException();
            }
            while (true) {
                try {
                    if (cp < 65536) {
                        chars[w] = (char)cp;
                        ++w;
                        continue block2;
                    }
                    chars[w] = (char)(55232 + (cp >> 10));
                    chars[w + 1] = (char)(56320 + (cp & 0x3FF));
                    w += 2;
                    continue block2;
                }
                catch (IndexOutOfBoundsException ex) {
                    int newlen = (int)Math.ceil((double)codePoints.length * (double)(w + 2) / (double)(r - offset + 1));
                    char[] temp = new char[newlen];
                    System.arraycopy(chars, 0, temp, 0, w);
                    chars = temp;
                    continue;
                }
                break;
            }
        }
        return new String(chars, 0, w);
    }

    public static void UTF8toUTF16(byte[] utf8, int offset, int length, CharsRef chars) {
        chars.offset = 0;
        int out_offset = 0;
        chars.chars = ArrayUtil.grow(chars.chars, length);
        char[] out = chars.chars;
        int limit = offset + length;
        while (offset < limit) {
            int b;
            if ((b = utf8[offset++] & 0xFF) < 192) {
                assert (b < 128);
                out[out_offset++] = (char)b;
                continue;
            }
            if (b < 224) {
                out[out_offset++] = (char)(((b & 0x1F) << 6) + (utf8[offset++] & 0x3F));
                continue;
            }
            if (b < 240) {
                out[out_offset++] = (char)(((b & 0xF) << 12) + ((utf8[offset] & 0x3F) << 6) + (utf8[offset + 1] & 0x3F));
                offset += 2;
                continue;
            }
            assert (b < 248) : "b=" + b;
            int ch = ((b & 7) << 18) + ((utf8[offset] & 0x3F) << 12) + ((utf8[offset + 1] & 0x3F) << 6) + (utf8[offset + 2] & 0x3F);
            offset += 3;
            if ((long)ch < 65535L) {
                out[out_offset++] = (char)ch;
                continue;
            }
            int chHalf = ch - 65536;
            out[out_offset++] = (char)((chHalf >> 10) + 55296);
            out[out_offset++] = (char)(((long)chHalf & 0x3FFL) + 56320L);
        }
        chars.length = out_offset - chars.offset;
    }

    public static void UTF8toUTF16(BytesRef bytesRef, CharsRef chars) {
        UnicodeUtil.UTF8toUTF16(bytesRef.bytes, bytesRef.offset, bytesRef.length, chars);
    }

    public static boolean validUTF16String(CharSequence s) {
        int size = s.length();
        for (int i = 0; i < size; ++i) {
            char ch = s.charAt(i);
            if (ch >= '\ud800' && ch <= '\udbff') {
                if (i < size - 1) {
                    char nextCH;
                    if ((nextCH = s.charAt(++i)) >= '\udc00' && nextCH <= '\udfff') continue;
                    return false;
                }
                return false;
            }
            if (ch < '\udc00' || ch > '\udfff') continue;
            return false;
        }
        return true;
    }

    public static final class UTF16Result {
        public char[] result = new char[10];
        public int[] offsets = new int[10];
        public int length;

        public void setLength(int newLength) {
            if (this.result.length < newLength) {
                this.result = ArrayUtil.grow(this.result, newLength);
            }
            this.length = newLength;
        }

        public void copyText(UTF16Result other) {
            this.setLength(other.length);
            System.arraycopy(other.result, 0, this.result, 0, this.length);
        }
    }

    public static final class UTF8Result {
        public byte[] result = new byte[10];
        public int length;

        public void setLength(int newLength) {
            if (this.result.length < newLength) {
                this.result = ArrayUtil.grow(this.result, newLength);
            }
            this.length = newLength;
        }
    }
}

