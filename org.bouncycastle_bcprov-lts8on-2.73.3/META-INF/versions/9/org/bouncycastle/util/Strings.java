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

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public final class Strings {
    private static String LINE_SEPARATOR;

    public static String fromUTF8ByteArray(byte[] bytes) {
        char[] chars = new char[bytes.length];
        int len = UTF8.transcodeToUTF16(bytes, chars);
        if (len < 0) {
            throw new IllegalArgumentException("Invalid UTF-8 input");
        }
        return new String(chars, 0, len);
    }

    public static String fromUTF8ByteArray(byte[] bytes, int off, int length) {
        char[] chars = new char[length];
        int len = UTF8.transcodeToUTF16(bytes, off, length, chars);
        if (len < 0) {
            throw new IllegalArgumentException("Invalid UTF-8 input");
        }
        return new String(chars, 0, len);
    }

    public static byte[] toUTF8ByteArray(String string) {
        return Strings.toUTF8ByteArray(string.toCharArray());
    }

    public static byte[] toUTF8ByteArray(char[] string) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            Strings.toUTF8ByteArray(string, bOut);
        }
        catch (IOException e) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }
        return bOut.toByteArray();
    }

    public static void toUTF8ByteArray(char[] string, OutputStream sOut) throws IOException {
        char[] c = string;
        for (int i = 0; i < c.length; ++i) {
            char ch = c[i];
            if (ch < '\u0080') {
                sOut.write(ch);
                continue;
            }
            if (ch < '\u0800') {
                sOut.write(0xC0 | ch >> 6);
                sOut.write(0x80 | ch & 0x3F);
                continue;
            }
            if (ch >= '\ud800' && ch <= '\udfff') {
                if (i + 1 >= c.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                char W1 = ch;
                char W2 = ch = c[++i];
                if (W1 > '\udbff') {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                int codePoint = ((W1 & 0x3FF) << 10 | W2 & 0x3FF) + 65536;
                sOut.write(0xF0 | codePoint >> 18);
                sOut.write(0x80 | codePoint >> 12 & 0x3F);
                sOut.write(0x80 | codePoint >> 6 & 0x3F);
                sOut.write(0x80 | codePoint & 0x3F);
                continue;
            }
            sOut.write(0xE0 | ch >> 12);
            sOut.write(0x80 | ch >> 6 & 0x3F);
            sOut.write(0x80 | ch & 0x3F);
        }
    }

    public static String toUpperCase(String string) {
        boolean changed = false;
        char[] chars = string.toCharArray();
        for (int i = 0; i != chars.length; ++i) {
            char ch = chars[i];
            if ('a' > ch || 'z' < ch) continue;
            changed = true;
            chars[i] = (char)(ch - 97 + 65);
        }
        if (changed) {
            return new String(chars);
        }
        return string;
    }

    public static String toLowerCase(String string) {
        boolean changed = false;
        char[] chars = string.toCharArray();
        for (int i = 0; i != chars.length; ++i) {
            char ch = chars[i];
            if ('A' > ch || 'Z' < ch) continue;
            changed = true;
            chars[i] = (char)(ch - 65 + 97);
        }
        if (changed) {
            return new String(chars);
        }
        return string;
    }

    public static byte[] toByteArray(char[] chars) {
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i != bytes.length; ++i) {
            bytes[i] = (byte)chars[i];
        }
        return bytes;
    }

    public static byte[] toByteArray(String string) {
        byte[] bytes = new byte[string.length()];
        for (int i = 0; i != bytes.length; ++i) {
            char ch = string.charAt(i);
            bytes[i] = (byte)ch;
        }
        return bytes;
    }

    public static int toByteArray(String s, byte[] buf, int off) {
        int count = s.length();
        for (int i = 0; i < count; ++i) {
            char c = s.charAt(i);
            buf[off + i] = (byte)c;
        }
        return count;
    }

    public static boolean constantTimeAreEqual(String a, String b) {
        boolean isEqual = a.length() == b.length();
        int len = a.length();
        if (isEqual) {
            for (int i = 0; i != len; ++i) {
                isEqual &= a.charAt(i) == b.charAt(i);
            }
        } else {
            for (int i = 0; i != len; ++i) {
                isEqual &= a.charAt(i) == ' ';
            }
        }
        return isEqual;
    }

    public static String fromByteArray(byte[] bytes) {
        return new String(Strings.asCharArray(bytes));
    }

    public static char[] asCharArray(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i != chars.length; ++i) {
            chars[i] = (char)(bytes[i] & 0xFF);
        }
        return chars;
    }

    public static String[] split(String input, char delimiter) {
        Vector<String> v = new Vector<String>();
        boolean moreTokens = true;
        while (moreTokens) {
            int tokenLocation = input.indexOf(delimiter);
            if (tokenLocation > 0) {
                String subString = input.substring(0, tokenLocation);
                v.addElement(subString);
                input = input.substring(tokenLocation + 1);
                continue;
            }
            moreTokens = false;
            v.addElement(input);
        }
        String[] res = new String[v.size()];
        for (int i = 0; i != res.length; ++i) {
            res[i] = (String)v.elementAt(i);
        }
        return res;
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
        catch (Exception e) {
            try {
                LINE_SEPARATOR = String.format("%n", new Object[0]);
            }
            catch (Exception ef) {
                LINE_SEPARATOR = "\n";
            }
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class StringListImpl
    extends ArrayList<String>
    implements StringList {
        private StringListImpl() {
        }

        @Override
        public boolean add(String s) {
            return super.add(s);
        }

        @Override
        public String set(int index, String element) {
            return super.set(index, element);
        }

        @Override
        public void add(int index, String element) {
            super.add(index, element);
        }

        @Override
        public String[] toStringArray() {
            String[] strs = new String[this.size()];
            for (int i = 0; i != strs.length; ++i) {
                strs[i] = (String)this.get(i);
            }
            return strs;
        }

        @Override
        public String[] toStringArray(int from, int to) {
            String[] strs = new String[to - from];
            for (int i = from; i != this.size() && i != to; ++i) {
                strs[i - from] = (String)this.get(i);
            }
            return strs;
        }
    }
}

