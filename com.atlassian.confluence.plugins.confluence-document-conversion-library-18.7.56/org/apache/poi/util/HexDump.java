/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.poi.util.Internal;

@Internal
public final class HexDump {
    public static final String EOL = System.getProperty("line.separator");
    public static final Charset UTF8 = StandardCharsets.UTF_8;

    private HexDump() {
    }

    public static void dump(byte[] data, long offset, OutputStream stream, int index, int length) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (stream == null) {
            throw new IllegalArgumentException("cannot write to nullstream");
        }
        OutputStreamWriter osw = new OutputStreamWriter(stream, UTF8);
        osw.write(HexDump.dump(data, offset, index, length));
        osw.flush();
    }

    public static synchronized void dump(byte[] data, long offset, OutputStream stream, int index) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        HexDump.dump(data, offset, stream, index, Integer.MAX_VALUE);
    }

    public static String dump(byte[] data, long offset, int index) {
        return HexDump.dump(data, offset, index, Integer.MAX_VALUE);
    }

    public static String dump(byte[] data, long offset, int index, int length) {
        int data_length;
        if (data == null || data.length == 0) {
            return "No Data" + EOL;
        }
        int n = data_length = length == Integer.MAX_VALUE || length < 0 || index + length < 0 ? data.length : Math.min(data.length, index + length);
        if (index < 0 || index >= data.length) {
            String err = "illegal index: " + index + " into array of length " + data.length;
            throw new ArrayIndexOutOfBoundsException(err);
        }
        long display_offset = offset + (long)index;
        StringBuilder buffer = new StringBuilder(74);
        for (int j = index; j < data_length; j += 16) {
            int k;
            int chars_read = data_length - j;
            if (chars_read > 16) {
                chars_read = 16;
            }
            HexDump.writeHex(buffer, display_offset, 8, "");
            for (k = 0; k < 16; ++k) {
                if (k < chars_read) {
                    HexDump.writeHex(buffer, data[k + j], 2, " ");
                    continue;
                }
                buffer.append("   ");
            }
            buffer.append(' ');
            for (k = 0; k < chars_read; ++k) {
                buffer.append(HexDump.toAscii(data[k + j]));
            }
            buffer.append(EOL);
            display_offset += (long)chars_read;
        }
        return buffer.toString();
    }

    public static char toAscii(int dataB) {
        int charB = dataB & 0xFF;
        if (Character.isISOControl((char)charB)) {
            return '.';
        }
        switch (charB) {
            case 221: 
            case 255: {
                charB = 46;
                break;
            }
        }
        return (char)charB;
    }

    public static String toHex(byte[] value) {
        StringBuilder retVal = new StringBuilder();
        retVal.append('[');
        if (value != null && value.length > 0) {
            for (int x = 0; x < value.length; ++x) {
                if (x > 0) {
                    retVal.append(", ");
                }
                retVal.append(HexDump.toHex(value[x]));
            }
        }
        retVal.append(']');
        return retVal.toString();
    }

    public static String toHex(short value) {
        StringBuilder sb = new StringBuilder(4);
        HexDump.writeHex(sb, value & 0xFFFF, 4, "");
        return sb.toString();
    }

    public static String toHex(byte value) {
        StringBuilder sb = new StringBuilder(2);
        HexDump.writeHex(sb, value & 0xFF, 2, "");
        return sb.toString();
    }

    public static String toHex(int value) {
        StringBuilder sb = new StringBuilder(8);
        HexDump.writeHex(sb, (long)value & 0xFFFFFFFFL, 8, "");
        return sb.toString();
    }

    public static String toHex(long value) {
        StringBuilder sb = new StringBuilder(16);
        HexDump.writeHex(sb, value, 16, "");
        return sb.toString();
    }

    public static String longToHex(long value) {
        StringBuilder sb = new StringBuilder(18);
        HexDump.writeHex(sb, value, 16, "0x");
        return sb.toString();
    }

    public static String intToHex(int value) {
        StringBuilder sb = new StringBuilder(10);
        HexDump.writeHex(sb, (long)value & 0xFFFFFFFFL, 8, "0x");
        return sb.toString();
    }

    public static String shortToHex(int value) {
        StringBuilder sb = new StringBuilder(6);
        HexDump.writeHex(sb, (long)value & 0xFFFFL, 4, "0x");
        return sb.toString();
    }

    public static String byteToHex(int value) {
        StringBuilder sb = new StringBuilder(4);
        HexDump.writeHex(sb, (long)value & 0xFFL, 2, "0x");
        return sb.toString();
    }

    private static void writeHex(StringBuilder sb, long value, int nDigits, String prefix) {
        sb.append(prefix);
        char[] buf = new char[nDigits];
        long acc = value;
        for (int i = nDigits - 1; i >= 0; --i) {
            int digit = Math.toIntExact(acc & 0xFL);
            buf[i] = (char)(digit < 10 ? 48 + digit : 65 + digit - 10);
            acc >>>= 4;
        }
        sb.append(buf);
    }
}

