/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import com.mchange.lang.ByteUtils;
import java.io.StringWriter;

public final class CharUtils {
    public static int charFromByteArray(byte[] byArray, int n) {
        int n2 = 0;
        n2 |= ByteUtils.toUnsigned(byArray[n + 0]) << 8;
        return n2 |= ByteUtils.toUnsigned(byArray[n + 1]) << 0;
    }

    public static byte[] byteArrayFromChar(char c) {
        byte[] byArray = new byte[2];
        CharUtils.charIntoByteArray(c, 0, byArray);
        return byArray;
    }

    public static void charIntoByteArray(int n, int n2, byte[] byArray) {
        byArray[n2 + 0] = (byte)(n >>> 8 & 0xFF);
        byArray[n2 + 1] = (byte)(n >>> 0 & 0xFF);
    }

    public static String toHexAscii(char c) {
        StringWriter stringWriter = new StringWriter(4);
        ByteUtils.addHexAscii((byte)(c >>> 8 & 0xFF), stringWriter);
        ByteUtils.addHexAscii((byte)(c & 0xFF), stringWriter);
        return stringWriter.toString();
    }

    public static char[] fromHexAscii(String string) {
        int n = string.length();
        if (n % 4 != 0) {
            throw new NumberFormatException("Hex ascii must be exactly four digits per char.");
        }
        byte[] byArray = ByteUtils.fromHexAscii(string);
        int n2 = n / 4;
        char[] cArray = new char[n2];
        int n3 = 0;
        while (n < n2) {
            cArray[n3] = (char)CharUtils.charFromByteArray(byArray, n3 * 2);
            ++n3;
        }
        return cArray;
    }

    private CharUtils() {
    }
}

