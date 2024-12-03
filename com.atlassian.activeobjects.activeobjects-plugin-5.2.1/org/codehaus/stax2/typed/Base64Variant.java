/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.typed;

import java.util.Arrays;

public final class Base64Variant {
    static final char PADDING_CHAR_NONE = '\u0000';
    public static final int BASE64_VALUE_INVALID = -1;
    public static final int BASE64_VALUE_PADDING = -2;
    private final int[] _asciiToBase64 = new int[128];
    private final char[] _base64ToAsciiC = new char[64];
    private final byte[] _base64ToAsciiB = new byte[64];
    final String _name;
    final boolean _usesPadding;
    final char _paddingChar;
    final int _maxLineLength;

    public Base64Variant(String string, String string2, boolean bl, char c, int n) {
        this._name = string;
        this._usesPadding = bl;
        this._paddingChar = c;
        this._maxLineLength = n;
        int n2 = string2.length();
        if (n2 != 64) {
            throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + n2 + ")");
        }
        string2.getChars(0, n2, this._base64ToAsciiC, 0);
        Arrays.fill(this._asciiToBase64, -1);
        int n3 = 0;
        while (n3 < n2) {
            char c2 = this._base64ToAsciiC[n3];
            this._base64ToAsciiB[n3] = (byte)c2;
            this._asciiToBase64[c2] = n3++;
        }
        if (bl) {
            this._asciiToBase64[c] = -2;
        }
    }

    public Base64Variant(Base64Variant base64Variant, String string, int n) {
        this(base64Variant, string, base64Variant._usesPadding, base64Variant._paddingChar, n);
    }

    public Base64Variant(Base64Variant base64Variant, String string, boolean bl, char c, int n) {
        this._name = string;
        byte[] byArray = base64Variant._base64ToAsciiB;
        System.arraycopy(byArray, 0, this._base64ToAsciiB, 0, byArray.length);
        char[] cArray = base64Variant._base64ToAsciiC;
        System.arraycopy(cArray, 0, this._base64ToAsciiC, 0, cArray.length);
        int[] nArray = base64Variant._asciiToBase64;
        System.arraycopy(nArray, 0, this._asciiToBase64, 0, nArray.length);
        this._usesPadding = bl;
        this._paddingChar = c;
        this._maxLineLength = n;
    }

    public String getName() {
        return this._name;
    }

    public boolean usesPadding() {
        return this._usesPadding;
    }

    public boolean usesPaddingChar(char c) {
        return c == this._paddingChar;
    }

    public char getPaddingChar() {
        return this._paddingChar;
    }

    public byte getPaddingByte() {
        return (byte)this._paddingChar;
    }

    public int getMaxLineLength() {
        return this._maxLineLength;
    }

    public int decodeBase64Char(char c) {
        char c2 = c;
        return c2 <= '\u007f' ? this._asciiToBase64[c2] : -1;
    }

    public int decodeBase64Byte(byte by) {
        byte by2 = by;
        return by2 <= 127 ? this._asciiToBase64[by2] : -1;
    }

    public char encodeBase64BitsAsChar(int n) {
        return this._base64ToAsciiC[n];
    }

    public int encodeBase64Chunk(int n, char[] cArray, int n2) {
        cArray[n2++] = this._base64ToAsciiC[n >> 18 & 0x3F];
        cArray[n2++] = this._base64ToAsciiC[n >> 12 & 0x3F];
        cArray[n2++] = this._base64ToAsciiC[n >> 6 & 0x3F];
        cArray[n2++] = this._base64ToAsciiC[n & 0x3F];
        return n2;
    }

    public int encodeBase64Partial(int n, int n2, char[] cArray, int n3) {
        cArray[n3++] = this._base64ToAsciiC[n >> 18 & 0x3F];
        cArray[n3++] = this._base64ToAsciiC[n >> 12 & 0x3F];
        if (this._usesPadding) {
            cArray[n3++] = n2 == 2 ? this._base64ToAsciiC[n >> 6 & 0x3F] : this._paddingChar;
            cArray[n3++] = this._paddingChar;
        } else if (n2 == 2) {
            cArray[n3++] = this._base64ToAsciiC[n >> 6 & 0x3F];
        }
        return n3;
    }

    public byte encodeBase64BitsAsByte(int n) {
        return this._base64ToAsciiB[n];
    }

    public int encodeBase64Chunk(int n, byte[] byArray, int n2) {
        byArray[n2++] = this._base64ToAsciiB[n >> 18 & 0x3F];
        byArray[n2++] = this._base64ToAsciiB[n >> 12 & 0x3F];
        byArray[n2++] = this._base64ToAsciiB[n >> 6 & 0x3F];
        byArray[n2++] = this._base64ToAsciiB[n & 0x3F];
        return n2;
    }

    public int encodeBase64Partial(int n, int n2, byte[] byArray, int n3) {
        byArray[n3++] = this._base64ToAsciiB[n >> 18 & 0x3F];
        byArray[n3++] = this._base64ToAsciiB[n >> 12 & 0x3F];
        if (this._usesPadding) {
            byte by = (byte)this._paddingChar;
            byArray[n3++] = n2 == 2 ? this._base64ToAsciiB[n >> 6 & 0x3F] : by;
            byArray[n3++] = by;
        } else if (n2 == 2) {
            byArray[n3++] = this._base64ToAsciiB[n >> 6 & 0x3F];
        }
        return n3;
    }

    public String toString() {
        return this._name;
    }
}

