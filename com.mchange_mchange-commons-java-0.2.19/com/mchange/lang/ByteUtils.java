/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public final class ByteUtils {
    public static final short UNSIGNED_MAX_VALUE = 255;

    public static short toUnsigned(byte n) {
        return (short)(n < 0 ? 256 + n : n);
    }

    public static int unsignedPromote(byte by) {
        return by & 0xFF;
    }

    public static String toHexAscii(byte by) {
        StringWriter stringWriter = new StringWriter(2);
        ByteUtils.addHexAscii(by, stringWriter);
        return stringWriter.toString();
    }

    public static String toLowercaseHexAscii(byte by) {
        StringWriter stringWriter = new StringWriter(2);
        ByteUtils.addLowercaseHexAscii(by, stringWriter);
        return stringWriter.toString();
    }

    public static String toHexAscii(byte[] byArray) {
        int n = byArray.length;
        StringWriter stringWriter = new StringWriter(n * 2);
        for (int i = 0; i < n; ++i) {
            ByteUtils.addHexAscii(byArray[i], stringWriter);
        }
        return stringWriter.toString();
    }

    public static String toLowercaseHexAscii(byte[] byArray) {
        int n = byArray.length;
        StringWriter stringWriter = new StringWriter(n * 2);
        for (int i = 0; i < n; ++i) {
            ByteUtils.addLowercaseHexAscii(byArray[i], stringWriter);
        }
        return stringWriter.toString();
    }

    public static byte[] fromHexAscii(String string) throws NumberFormatException {
        try {
            int n = string.length();
            if (n % 2 != 0) {
                throw new NumberFormatException("Hex ascii must be exactly two digits per byte.");
            }
            int n2 = n / 2;
            byte[] byArray = new byte[n2];
            int n3 = 0;
            StringReader stringReader = new StringReader(string);
            while (n3 < n2) {
                int n4 = 16 * ByteUtils.fromHexDigit(stringReader.read()) + ByteUtils.fromHexDigit(stringReader.read());
                byArray[n3++] = (byte)n4;
            }
            return byArray;
        }
        catch (IOException iOException) {
            throw new InternalError("IOException reading from StringReader?!?!");
        }
    }

    static void addHexAscii(byte by, StringWriter stringWriter) {
        int n = ByteUtils.unsignedPromote(by);
        int n2 = n / 16;
        int n3 = n % 16;
        stringWriter.write(ByteUtils.toHexDigit(n2));
        stringWriter.write(ByteUtils.toHexDigit(n3));
    }

    static void addLowercaseHexAscii(byte by, StringWriter stringWriter) {
        int n = ByteUtils.unsignedPromote(by);
        int n2 = n / 16;
        int n3 = n % 16;
        stringWriter.write(ByteUtils.toLowercaseHexDigit(n2));
        stringWriter.write(ByteUtils.toLowercaseHexDigit(n3));
    }

    private static int fromHexDigit(int n) throws NumberFormatException {
        if (n >= 48 && n < 58) {
            return n - 48;
        }
        if (n >= 65 && n < 71) {
            return n - 55;
        }
        if (n >= 97 && n < 103) {
            return n - 87;
        }
        throw new NumberFormatException(39 + n + "' is not a valid hexadecimal digit.");
    }

    private static char toHexDigit(int n) {
        char c = n <= 9 ? (char)(n + 48) : (char)(n + 55);
        return c;
    }

    private static char toLowercaseHexDigit(int n) {
        char c = n <= 9 ? (char)(n + 48) : (char)(n + 87);
        return c;
    }

    private ByteUtils() {
    }
}

