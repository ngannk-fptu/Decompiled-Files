/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import zipkin2.internal.HexCodec;

public final class WriteBuffer {
    final byte[] buf;
    int pos;

    public static WriteBuffer wrap(byte[] bytes) {
        return WriteBuffer.wrap(bytes, 0);
    }

    public static WriteBuffer wrap(byte[] bytes, int pos) {
        return new WriteBuffer(bytes, pos);
    }

    WriteBuffer(byte[] buf, int pos) {
        this.buf = buf;
        this.pos = pos;
    }

    public void writeByte(int v) {
        this.buf[this.pos++] = (byte)(v & 0xFF);
    }

    public void write(byte[] v) {
        System.arraycopy(v, 0, this.buf, this.pos, v.length);
        this.pos += v.length;
    }

    void writeBackwards(long v) {
        int lastPos;
        this.pos = lastPos = this.pos + WriteBuffer.asciiSizeInBytes(v);
        while (v != 0L) {
            int digit = (int)(v % 10L);
            this.buf[--lastPos] = (byte)HexCodec.HEX_DIGITS[digit];
            v /= 10L;
        }
    }

    public void writeLongHex(long v) {
        int pos = this.pos;
        WriteBuffer.writeHexByte(this.buf, pos + 0, (byte)(v >>> 56 & 0xFFL));
        WriteBuffer.writeHexByte(this.buf, pos + 2, (byte)(v >>> 48 & 0xFFL));
        WriteBuffer.writeHexByte(this.buf, pos + 4, (byte)(v >>> 40 & 0xFFL));
        WriteBuffer.writeHexByte(this.buf, pos + 6, (byte)(v >>> 32 & 0xFFL));
        WriteBuffer.writeHexByte(this.buf, pos + 8, (byte)(v >>> 24 & 0xFFL));
        WriteBuffer.writeHexByte(this.buf, pos + 10, (byte)(v >>> 16 & 0xFFL));
        WriteBuffer.writeHexByte(this.buf, pos + 12, (byte)(v >>> 8 & 0xFFL));
        WriteBuffer.writeHexByte(this.buf, pos + 14, (byte)(v & 0xFFL));
        this.pos = pos + 16;
    }

    static void writeHexByte(byte[] data, int pos, byte b) {
        data[pos + 0] = (byte)HexCodec.HEX_DIGITS[b >> 4 & 0xF];
        data[pos + 1] = (byte)HexCodec.HEX_DIGITS[b & 0xF];
    }

    final int pos() {
        return this.pos;
    }

    public void writeAscii(String v) {
        int length = v.length();
        for (int i = 0; i < length; ++i) {
            this.writeByte(v.charAt(i) & 0xFF);
        }
    }

    public void writeUtf8(CharSequence string) {
        int len = string.length();
        for (int i = 0; i < len; ++i) {
            char low;
            char ch = string.charAt(i);
            if (ch < '\u0080') {
                this.writeByte(ch);
                while (i < len - 1 && (ch = string.charAt(i + 1)) < '\u0080') {
                    ++i;
                    this.writeByte(ch);
                }
                continue;
            }
            if (ch < '\u0800') {
                this.writeByte(0xC0 | ch >> 6);
                this.writeByte(0x80 | ch & 0x3F);
                continue;
            }
            if (ch < '\ud800' || ch > '\udfff') {
                this.writeByte(0xE0 | ch >> 12);
                this.writeByte(0x80 | ch >> 6 & 0x3F);
                this.writeByte(0x80 | ch & 0x3F);
                continue;
            }
            if (!Character.isHighSurrogate(ch)) {
                this.writeByte(63);
                continue;
            }
            if (i == len - 1) {
                this.writeByte(63);
                break;
            }
            if (!Character.isLowSurrogate(low = string.charAt(++i))) {
                this.writeByte(63);
                this.writeByte(Character.isHighSurrogate(low) ? 63 : (int)low);
                continue;
            }
            int codePoint = Character.toCodePoint(ch, low);
            this.writeByte(0xF0 | codePoint >> 18);
            this.writeByte(0x80 | codePoint >> 12 & 0x3F);
            this.writeByte(0x80 | codePoint >> 6 & 0x3F);
            this.writeByte(0x80 | codePoint & 0x3F);
        }
    }

    public void writeAscii(long v) {
        if (v == 0L) {
            this.writeByte(48);
            return;
        }
        if (v == Long.MIN_VALUE) {
            this.writeAscii("-9223372036854775808");
            return;
        }
        if (v < 0L) {
            this.writeByte(45);
            v = -v;
        }
        this.writeBackwards(v);
    }

    void writeVarint(int v) {
        while ((v & 0xFFFFFF80) != 0) {
            this.writeByte((byte)(v & 0x7F | 0x80));
            v >>>= 7;
        }
        this.writeByte((byte)v);
    }

    void writeVarint(long v) {
        while ((v & 0xFFFFFFFFFFFFFF80L) != 0L) {
            this.writeByte((byte)(v & 0x7FL | 0x80L));
            v >>>= 7;
        }
        this.writeByte((byte)v);
    }

    void writeLongLe(long v) {
        this.writeByte((byte)(v & 0xFFL));
        this.writeByte((byte)(v >> 8 & 0xFFL));
        this.writeByte((byte)(v >> 16 & 0xFFL));
        this.writeByte((byte)(v >> 24 & 0xFFL));
        this.writeByte((byte)(v >> 32 & 0xFFL));
        this.writeByte((byte)(v >> 40 & 0xFFL));
        this.writeByte((byte)(v >> 48 & 0xFFL));
        this.writeByte((byte)(v >> 56 & 0xFFL));
    }

    public static int utf8SizeInBytes(CharSequence string) {
        int sizeInBytes = 0;
        int len = string.length();
        for (int i = 0; i < len; ++i) {
            char low;
            char ch = string.charAt(i);
            if (ch < '\u0080') {
                ++sizeInBytes;
                while (i < len - 1 && (ch = string.charAt(i + 1)) < '\u0080') {
                    ++i;
                    ++sizeInBytes;
                }
                continue;
            }
            if (ch < '\u0800') {
                sizeInBytes += 2;
                continue;
            }
            if (ch < '\ud800' || ch > '\udfff') {
                sizeInBytes += 3;
                continue;
            }
            char c = low = i + 1 < len ? string.charAt(i + 1) : (char)'\u0000';
            if (ch > '\udbff' || low < '\udc00' || low > '\udfff') {
                ++sizeInBytes;
                continue;
            }
            sizeInBytes += 4;
            ++i;
        }
        return sizeInBytes;
    }

    public static int asciiSizeInBytes(long v) {
        if (v == 0L) {
            return 1;
        }
        if (v == Long.MIN_VALUE) {
            return 20;
        }
        boolean negative = false;
        if (v < 0L) {
            v = -v;
            negative = true;
        }
        int width = v < 100000000L ? (v < 10000L ? (v < 100L ? (v < 10L ? 1 : 2) : (v < 1000L ? 3 : 4)) : (v < 1000000L ? (v < 100000L ? 5 : 6) : (v < 10000000L ? 7 : 8))) : (v < 1000000000000L ? (v < 10000000000L ? (v < 1000000000L ? 9 : 10) : (v < 100000000000L ? 11 : 12)) : (v < 1000000000000000L ? (v < 10000000000000L ? 13 : (v < 100000000000000L ? 14 : 15)) : (v < 100000000000000000L ? (v < 10000000000000000L ? 16 : 17) : (v < 1000000000000000000L ? 18 : 19))));
        return negative ? width + 1 : width;
    }

    public static int varintSizeInBytes(int value) {
        if ((value & 0xFFFFFF80) == 0) {
            return 1;
        }
        if ((value & 0xFFFFC000) == 0) {
            return 2;
        }
        if ((value & 0xFFE00000) == 0) {
            return 3;
        }
        if ((value & 0xF0000000) == 0) {
            return 4;
        }
        return 5;
    }

    public static int varintSizeInBytes(long v) {
        if ((v & 0xFFFFFFFFFFFFFF80L) == 0L) {
            return 1;
        }
        if ((v & 0xFFFFFFFFFFFFC000L) == 0L) {
            return 2;
        }
        if ((v & 0xFFFFFFFFFFE00000L) == 0L) {
            return 3;
        }
        if ((v & 0xFFFFFFFFF0000000L) == 0L) {
            return 4;
        }
        if ((v & 0xFFFFFFF800000000L) == 0L) {
            return 5;
        }
        if ((v & 0xFFFFFC0000000000L) == 0L) {
            return 6;
        }
        if ((v & 0xFFFE000000000000L) == 0L) {
            return 7;
        }
        if ((v & 0xFF00000000000000L) == 0L) {
            return 8;
        }
        if ((v & Long.MIN_VALUE) == 0L) {
            return 9;
        }
        return 10;
    }

    public static interface Writer<T> {
        public int sizeInBytes(T var1);

        public void write(T var1, WriteBuffer var2);
    }
}

