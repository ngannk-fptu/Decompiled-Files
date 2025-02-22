/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Vector;
import org.bouncycastle.util.StringList;
import org.bouncycastle.util.encoders.UTF8;

public final class Strings {
    private static String LINE_SEPARATOR;

    public static String fromUTF8ByteArray(byte[] byArray) {
        char[] cArray = new char[byArray.length];
        int n = UTF8.transcodeToUTF16(byArray, cArray);
        if (n < 0) {
            throw new IllegalArgumentException("Invalid UTF-8 input");
        }
        return new String(cArray, 0, n);
    }

    public static byte[] toUTF8ByteArray(String string) {
        return Strings.toUTF8ByteArray(string.toCharArray());
    }

    public static byte[] toUTF8ByteArray(char[] cArray) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Strings.toUTF8ByteArray(cArray, byteArrayOutputStream);
        }
        catch (IOException iOException) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void toUTF8ByteArray(char[] cArray, OutputStream outputStream) throws IOException {
        char[] cArray2 = cArray;
        for (int i = 0; i < cArray2.length; ++i) {
            char c = cArray2[i];
            if (c < '\u0080') {
                outputStream.write(c);
                continue;
            }
            if (c < '\u0800') {
                outputStream.write(0xC0 | c >> 6);
                outputStream.write(0x80 | c & 0x3F);
                continue;
            }
            if (c >= '\ud800' && c <= '\udfff') {
                if (i + 1 >= cArray2.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                char c2 = c;
                char c3 = c = cArray2[++i];
                if (c2 > '\udbff') {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                int n = ((c2 & 0x3FF) << 10 | c3 & 0x3FF) + 65536;
                outputStream.write(0xF0 | n >> 18);
                outputStream.write(0x80 | n >> 12 & 0x3F);
                outputStream.write(0x80 | n >> 6 & 0x3F);
                outputStream.write(0x80 | n & 0x3F);
                continue;
            }
            outputStream.write(0xE0 | c >> 12);
            outputStream.write(0x80 | c >> 6 & 0x3F);
            outputStream.write(0x80 | c & 0x3F);
        }
    }

    public static String toUpperCase(String string) {
        boolean bl = false;
        char[] cArray = string.toCharArray();
        for (int i = 0; i != cArray.length; ++i) {
            char c = cArray[i];
            if ('a' > c || 'z' < c) continue;
            bl = true;
            cArray[i] = (char)(c - 97 + 65);
        }
        if (bl) {
            return new String(cArray);
        }
        return string;
    }

    public static String toLowerCase(String string) {
        boolean bl = false;
        char[] cArray = string.toCharArray();
        for (int i = 0; i != cArray.length; ++i) {
            char c = cArray[i];
            if ('A' > c || 'Z' < c) continue;
            bl = true;
            cArray[i] = (char)(c - 65 + 97);
        }
        if (bl) {
            return new String(cArray);
        }
        return string;
    }

    public static byte[] toByteArray(char[] cArray) {
        byte[] byArray = new byte[cArray.length];
        for (int i = 0; i != byArray.length; ++i) {
            byArray[i] = (byte)cArray[i];
        }
        return byArray;
    }

    public static byte[] toByteArray(String string) {
        byte[] byArray = new byte[string.length()];
        for (int i = 0; i != byArray.length; ++i) {
            char c = string.charAt(i);
            byArray[i] = (byte)c;
        }
        return byArray;
    }

    public static int toByteArray(String string, byte[] byArray, int n) {
        int n2 = string.length();
        for (int i = 0; i < n2; ++i) {
            char c = string.charAt(i);
            byArray[n + i] = (byte)c;
        }
        return n2;
    }

    public static boolean constantTimeAreEqual(String string, String string2) {
        boolean bl = string.length() == string2.length();
        int n = string.length();
        for (int i = 0; i != n; ++i) {
            bl &= string.charAt(i) == string2.charAt(i);
        }
        return bl;
    }

    public static String fromByteArray(byte[] byArray) {
        return new String(Strings.asCharArray(byArray));
    }

    public static char[] asCharArray(byte[] byArray) {
        char[] cArray = new char[byArray.length];
        for (int i = 0; i != cArray.length; ++i) {
            cArray[i] = (char)(byArray[i] & 0xFF);
        }
        return cArray;
    }

    public static String[] split(String string, char c) {
        Vector<String> vector = new Vector<String>();
        boolean bl = true;
        while (bl) {
            int n = string.indexOf(c);
            if (n > 0) {
                String string2 = string.substring(0, n);
                vector.addElement(string2);
                string = string.substring(n + 1);
                continue;
            }
            bl = false;
            vector.addElement(string);
        }
        String[] stringArray = new String[vector.size()];
        for (int i = 0; i != stringArray.length; ++i) {
            stringArray[i] = (String)vector.elementAt(i);
        }
        return stringArray;
    }

    public static StringList newList() {
        return new StringListImpl();
    }

    public static String lineSeparator() {
        return LINE_SEPARATOR;
    }

    static {
        try {
            LINE_SEPARATOR = AccessController.doPrivileged(new PrivilegedAction<String>(){

                @Override
                public String run() {
                    return System.getProperty("line.separator");
                }
            });
        }
        catch (Exception exception) {
            try {
                LINE_SEPARATOR = String.format("%n", new Object[0]);
            }
            catch (Exception exception2) {
                LINE_SEPARATOR = "\n";
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class StringListImpl
    extends ArrayList<String>
    implements StringList {
        private StringListImpl() {
        }

        @Override
        public boolean add(String string) {
            return super.add(string);
        }

        @Override
        public String set(int n, String string) {
            return super.set(n, string);
        }

        @Override
        public void add(int n, String string) {
            super.add(n, string);
        }

        @Override
        public String[] toStringArray() {
            String[] stringArray = new String[this.size()];
            for (int i = 0; i != stringArray.length; ++i) {
                stringArray[i] = (String)this.get(i);
            }
            return stringArray;
        }

        @Override
        public String[] toStringArray(int n, int n2) {
            String[] stringArray = new String[n2 - n];
            for (int i = n; i != this.size() && i != n2; ++i) {
                stringArray[i - n] = (String)this.get(i);
            }
            return stringArray;
        }
    }
}

