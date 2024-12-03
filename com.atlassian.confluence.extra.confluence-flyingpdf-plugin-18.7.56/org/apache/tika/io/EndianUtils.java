/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils {
    private static final int LONG_SIZE = 8;

    public static short readShortLE(InputStream stream) throws IOException, BufferUnderrunException {
        return (short)EndianUtils.readUShortLE(stream);
    }

    public static short readShortBE(InputStream stream) throws IOException, BufferUnderrunException {
        return (short)EndianUtils.readUShortBE(stream);
    }

    public static int readUShortLE(InputStream stream) throws IOException, BufferUnderrunException {
        int ch2;
        int ch1 = stream.read();
        if ((ch1 | (ch2 = stream.read())) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch2 << 8) + (ch1 << 0);
    }

    public static int readUShortBE(InputStream stream) throws IOException, BufferUnderrunException {
        int ch2;
        int ch1 = stream.read();
        if ((ch1 | (ch2 = stream.read())) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch1 << 8) + (ch2 << 0);
    }

    public static long readUIntLE(InputStream stream) throws IOException, BufferUnderrunException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = stream.read();
        if ((ch1 | (ch2 = stream.read()) | (ch3 = stream.read()) | (ch4 = stream.read())) < 0) {
            throw new BufferUnderrunException();
        }
        return (long)((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0)) & 0xFFFFFFFFL;
    }

    public static long readUIntBE(InputStream stream) throws IOException, BufferUnderrunException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = stream.read();
        if ((ch1 | (ch2 = stream.read()) | (ch3 = stream.read()) | (ch4 = stream.read())) < 0) {
            throw new BufferUnderrunException();
        }
        return (long)((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0)) & 0xFFFFFFFFL;
    }

    public static int readIntLE(InputStream stream) throws IOException, BufferUnderrunException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = stream.read();
        if ((ch1 | (ch2 = stream.read()) | (ch3 = stream.read()) | (ch4 = stream.read())) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    public static int readIntBE(InputStream stream) throws IOException, BufferUnderrunException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = stream.read();
        if ((ch1 | (ch2 = stream.read()) | (ch3 = stream.read()) | (ch4 = stream.read())) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }

    public static long readLongLE(InputStream stream) throws IOException, BufferUnderrunException {
        int ch8;
        int ch7;
        int ch6;
        int ch5;
        int ch4;
        int ch3;
        int ch2;
        int ch1 = stream.read();
        if ((ch1 | (ch2 = stream.read()) | (ch3 = stream.read()) | (ch4 = stream.read()) | (ch5 = stream.read()) | (ch6 = stream.read()) | (ch7 = stream.read()) | (ch8 = stream.read())) < 0) {
            throw new BufferUnderrunException();
        }
        return ((long)ch8 << 56) + ((long)ch7 << 48) + ((long)ch6 << 40) + ((long)ch5 << 32) + ((long)ch4 << 24) + (long)(ch3 << 16) + (long)(ch2 << 8) + (long)(ch1 << 0);
    }

    public static long readLongBE(InputStream stream) throws IOException, BufferUnderrunException {
        int ch8;
        int ch7;
        int ch6;
        int ch5;
        int ch4;
        int ch3;
        int ch2;
        int ch1 = stream.read();
        if ((ch1 | (ch2 = stream.read()) | (ch3 = stream.read()) | (ch4 = stream.read()) | (ch5 = stream.read()) | (ch6 = stream.read()) | (ch7 = stream.read()) | (ch8 = stream.read())) < 0) {
            throw new BufferUnderrunException();
        }
        return ((long)ch1 << 56) + ((long)ch2 << 48) + ((long)ch3 << 40) + ((long)ch4 << 32) + ((long)ch5 << 24) + (long)(ch6 << 16) + (long)(ch7 << 8) + (long)(ch8 << 0);
    }

    public static long readUE7(InputStream stream) throws IOException {
        int i;
        long v = 0L;
        int max = 6;
        int read = 0;
        while ((i = stream.read()) >= 0 && read++ < 6) {
            v <<= 7;
            if ((i & 0x80) == 128) {
                v += (long)(i & 0x7F);
                continue;
            }
            v += (long)i;
            break;
        }
        if (i < 0) {
            throw new IOException("Buffer underun; expected one more byte");
        }
        return v;
    }

    public static short getShortLE(byte[] data) {
        return EndianUtils.getShortLE(data, 0);
    }

    public static short getShortLE(byte[] data, int offset) {
        return (short)EndianUtils.getUShortLE(data, offset);
    }

    public static int getUShortLE(byte[] data) {
        return EndianUtils.getUShortLE(data, 0);
    }

    public static int getUShortLE(byte[] data, int offset) {
        int b0 = data[offset] & 0xFF;
        int b1 = data[offset + 1] & 0xFF;
        return (b1 << 8) + (b0 << 0);
    }

    public static short getShortBE(byte[] data) {
        return EndianUtils.getShortBE(data, 0);
    }

    public static short getShortBE(byte[] data, int offset) {
        return (short)EndianUtils.getUShortBE(data, offset);
    }

    public static int getUShortBE(byte[] data) {
        return EndianUtils.getUShortBE(data, 0);
    }

    public static int getUShortBE(byte[] data, int offset) {
        int b0 = data[offset] & 0xFF;
        int b1 = data[offset + 1] & 0xFF;
        return (b0 << 8) + (b1 << 0);
    }

    public static int getIntLE(byte[] data) {
        return EndianUtils.getIntLE(data, 0);
    }

    public static int getIntLE(byte[] data, int offset) {
        int i = offset;
        int b0 = data[i++] & 0xFF;
        int b1 = data[i++] & 0xFF;
        int b2 = data[i++] & 0xFF;
        int b3 = data[i++] & 0xFF;
        return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
    }

    public static int getIntBE(byte[] data) {
        return EndianUtils.getIntBE(data, 0);
    }

    public static int getIntBE(byte[] data, int offset) {
        int i = offset;
        int b0 = data[i++] & 0xFF;
        int b1 = data[i++] & 0xFF;
        int b2 = data[i++] & 0xFF;
        int b3 = data[i++] & 0xFF;
        return (b0 << 24) + (b1 << 16) + (b2 << 8) + (b3 << 0);
    }

    public static long getUIntLE(byte[] data) {
        return EndianUtils.getUIntLE(data, 0);
    }

    public static long getUIntLE(byte[] data, int offset) {
        long retNum = EndianUtils.getIntLE(data, offset);
        return retNum & 0xFFFFFFFFL;
    }

    public static long getUIntBE(byte[] data) {
        return EndianUtils.getUIntBE(data, 0);
    }

    public static long getUIntBE(byte[] data, int offset) {
        long retNum = EndianUtils.getIntBE(data, offset);
        return retNum & 0xFFFFFFFFL;
    }

    public static long getLongLE(byte[] data, int offset) {
        long result = 0L;
        for (int j = offset + 8 - 1; j >= offset; --j) {
            result <<= 8;
            result |= (long)(0xFF & data[j]);
        }
        return result;
    }

    public static int ubyteToInt(byte b) {
        return b & 0xFF;
    }

    public static short getUByte(byte[] data, int offset) {
        return (short)(data[offset] & 0xFF);
    }

    public static class BufferUnderrunException
    extends TikaException {
        private static final long serialVersionUID = 8358288231138076276L;

        public BufferUnderrunException() {
            super("Insufficient data left in stream for required read");
        }
    }
}

