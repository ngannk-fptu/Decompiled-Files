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

    public Base64Variant(String name, String base64Alphabet, boolean usesPadding, char paddingChar, int maxLineLength) {
        this._name = name;
        this._usesPadding = usesPadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
        int alphaLen = base64Alphabet.length();
        if (alphaLen != 64) {
            throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + alphaLen + ")");
        }
        base64Alphabet.getChars(0, alphaLen, this._base64ToAsciiC, 0);
        Arrays.fill(this._asciiToBase64, -1);
        int i = 0;
        while (i < alphaLen) {
            char alpha = this._base64ToAsciiC[i];
            this._base64ToAsciiB[i] = (byte)alpha;
            this._asciiToBase64[alpha] = i++;
        }
        if (usesPadding) {
            this._asciiToBase64[paddingChar] = -2;
        }
    }

    public Base64Variant(Base64Variant base, String name, int maxLineLength) {
        this(base, name, base._usesPadding, base._paddingChar, maxLineLength);
    }

    public Base64Variant(Base64Variant base, String name, boolean usesPadding, char paddingChar, int maxLineLength) {
        this._name = name;
        byte[] srcB = base._base64ToAsciiB;
        System.arraycopy(srcB, 0, this._base64ToAsciiB, 0, srcB.length);
        char[] srcC = base._base64ToAsciiC;
        System.arraycopy(srcC, 0, this._base64ToAsciiC, 0, srcC.length);
        int[] srcV = base._asciiToBase64;
        System.arraycopy(srcV, 0, this._asciiToBase64, 0, srcV.length);
        this._usesPadding = usesPadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
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
        char ch = c;
        return ch <= '\u007f' ? this._asciiToBase64[ch] : -1;
    }

    public int decodeBase64Byte(byte b) {
        byte ch = b;
        return ch <= 127 ? this._asciiToBase64[ch] : -1;
    }

    public char encodeBase64BitsAsChar(int value) {
        return this._base64ToAsciiC[value];
    }

    public int encodeBase64Chunk(int b24, char[] buffer, int ptr) {
        buffer[ptr++] = this._base64ToAsciiC[b24 >> 18 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiC[b24 >> 12 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiC[b24 >> 6 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiC[b24 & 0x3F];
        return ptr;
    }

    public int encodeBase64Partial(int bits, int outputBytes, char[] buffer, int outPtr) {
        buffer[outPtr++] = this._base64ToAsciiC[bits >> 18 & 0x3F];
        buffer[outPtr++] = this._base64ToAsciiC[bits >> 12 & 0x3F];
        if (this._usesPadding) {
            buffer[outPtr++] = outputBytes == 2 ? this._base64ToAsciiC[bits >> 6 & 0x3F] : this._paddingChar;
            buffer[outPtr++] = this._paddingChar;
        } else if (outputBytes == 2) {
            buffer[outPtr++] = this._base64ToAsciiC[bits >> 6 & 0x3F];
        }
        return outPtr;
    }

    public byte encodeBase64BitsAsByte(int value) {
        return this._base64ToAsciiB[value];
    }

    public int encodeBase64Chunk(int b24, byte[] buffer, int ptr) {
        buffer[ptr++] = this._base64ToAsciiB[b24 >> 18 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiB[b24 >> 12 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiB[b24 >> 6 & 0x3F];
        buffer[ptr++] = this._base64ToAsciiB[b24 & 0x3F];
        return ptr;
    }

    public int encodeBase64Partial(int bits, int outputBytes, byte[] buffer, int outPtr) {
        buffer[outPtr++] = this._base64ToAsciiB[bits >> 18 & 0x3F];
        buffer[outPtr++] = this._base64ToAsciiB[bits >> 12 & 0x3F];
        if (this._usesPadding) {
            byte pb = (byte)this._paddingChar;
            buffer[outPtr++] = outputBytes == 2 ? this._base64ToAsciiB[bits >> 6 & 0x3F] : pb;
            buffer[outPtr++] = pb;
        } else if (outputBytes == 2) {
            buffer[outPtr++] = this._base64ToAsciiB[bits >> 6 & 0x3F];
        }
        return outPtr;
    }

    public String toString() {
        return this._name;
    }
}

