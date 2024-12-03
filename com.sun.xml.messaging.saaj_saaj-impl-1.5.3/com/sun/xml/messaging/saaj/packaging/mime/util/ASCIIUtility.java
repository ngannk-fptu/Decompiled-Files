/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.util;

import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ASCIIUtility {
    private ASCIIUtility() {
    }

    public static int parseInt(byte[] b, int start, int end, int radix) throws NumberFormatException {
        if (b == null) {
            throw new NumberFormatException("null");
        }
        int result = 0;
        boolean negative = false;
        int i = start;
        if (end > start) {
            int digit;
            int limit;
            if (b[i] == 45) {
                negative = true;
                limit = Integer.MIN_VALUE;
                ++i;
            } else {
                limit = -2147483647;
            }
            int multmin = limit / radix;
            if (i < end) {
                if ((digit = Character.digit((char)b[i++], radix)) < 0) {
                    throw new NumberFormatException("illegal number: " + ASCIIUtility.toString(b, start, end));
                }
                result = -digit;
            }
            while (i < end) {
                if ((digit = Character.digit((char)b[i++], radix)) < 0) {
                    throw new NumberFormatException("illegal number");
                }
                if (result < multmin) {
                    throw new NumberFormatException("illegal number");
                }
                if ((result *= radix) < limit + digit) {
                    throw new NumberFormatException("illegal number");
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException("illegal number");
        }
        if (negative) {
            if (i > start + 1) {
                return result;
            }
            throw new NumberFormatException("illegal number");
        }
        return -result;
    }

    public static String toString(byte[] b, int start, int end) {
        int size = end - start;
        char[] theChars = new char[size];
        int i = 0;
        int j = start;
        while (i < size) {
            theChars[i++] = (char)(b[j++] & 0xFF);
        }
        return new String(theChars);
    }

    public static byte[] getBytes(String s) {
        char[] chars = s.toCharArray();
        int size = chars.length;
        byte[] bytes = new byte[size];
        int i = 0;
        while (i < size) {
            bytes[i] = (byte)chars[i++];
        }
        return bytes;
    }

    @Deprecated
    public static byte[] getBytes(InputStream is) throws IOException {
        ByteOutputStream bos = null;
        try {
            bos = new ByteOutputStream();
            bos.write(is);
        }
        finally {
            if (bos != null) {
                bos.close();
            }
            is.close();
        }
        return bos.toByteArray();
    }
}

