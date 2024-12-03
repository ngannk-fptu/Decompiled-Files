/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import org.apache.axis.utils.Messages;

public class HexBinary
implements Serializable {
    byte[] m_value = null;
    public static final String ERROR_ODD_NUMBER_OF_DIGITS = Messages.getMessage("oddDigits00");
    public static final String ERROR_BAD_CHARACTER_IN_HEX_STRING = Messages.getMessage("badChars01");
    public static final int[] DEC = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    public HexBinary() {
    }

    public HexBinary(String string) {
        this.m_value = HexBinary.decode(string);
    }

    public HexBinary(byte[] bytes) {
        this.m_value = bytes;
    }

    public byte[] getBytes() {
        return this.m_value;
    }

    public String toString() {
        return HexBinary.encode(this.m_value);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object object) {
        String s1 = object.toString();
        String s2 = this.toString();
        return s1.equals(s2);
    }

    public static byte[] decode(String digits) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < digits.length(); i += 2) {
            char c1 = digits.charAt(i);
            if (i + 1 >= digits.length()) {
                throw new IllegalArgumentException(ERROR_ODD_NUMBER_OF_DIGITS);
            }
            char c2 = digits.charAt(i + 1);
            int b = 0;
            if (c1 >= '0' && c1 <= '9') {
                b = (byte)(b + (c1 - 48) * 16);
            } else if (c1 >= 'a' && c1 <= 'f') {
                b = (byte)(b + (c1 - 97 + 10) * 16);
            } else if (c1 >= 'A' && c1 <= 'F') {
                b = (byte)(b + (c1 - 65 + 10) * 16);
            } else {
                throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
            }
            if (c2 >= '0' && c2 <= '9') {
                b = (byte)(b + (c2 - 48));
            } else if (c2 >= 'a' && c2 <= 'f') {
                b = (byte)(b + (c2 - 97 + 10));
            } else if (c2 >= 'A' && c2 <= 'F') {
                b = (byte)(b + (c2 - 65 + 10));
            } else {
                throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
            }
            baos.write(b);
        }
        return baos.toByteArray();
    }

    public static String encode(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(HexBinary.convertDigit(bytes[i] >> 4));
            sb.append(HexBinary.convertDigit(bytes[i] & 0xF));
        }
        return sb.toString();
    }

    public static int convert2Int(byte[] hex) {
        if (hex.length < 4) {
            return 0;
        }
        if (DEC[hex[0]] < 0) {
            throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
        }
        int len = DEC[hex[0]];
        len <<= 4;
        if (DEC[hex[1]] < 0) {
            throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
        }
        len += DEC[hex[1]];
        len <<= 4;
        if (DEC[hex[2]] < 0) {
            throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
        }
        len += DEC[hex[2]];
        len <<= 4;
        if (DEC[hex[3]] < 0) {
            throw new IllegalArgumentException(ERROR_BAD_CHARACTER_IN_HEX_STRING);
        }
        return len += DEC[hex[3]];
    }

    private static char convertDigit(int value) {
        if ((value &= 0xF) >= 10) {
            return (char)(value - 10 + 97);
        }
        return (char)(value + 48);
    }
}

