/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.util.Arrays;
import org.apache.xml.utils.XMLChar;

public class XML11Char {
    private static final byte[] XML11CHARS = new byte[65536];
    public static final int MASK_XML11_VALID = 1;
    public static final int MASK_XML11_SPACE = 2;
    public static final int MASK_XML11_NAME_START = 4;
    public static final int MASK_XML11_NAME = 8;
    public static final int MASK_XML11_CONTROL = 16;
    public static final int MASK_XML11_CONTENT = 32;
    public static final int MASK_XML11_NCNAME_START = 64;
    public static final int MASK_XML11_NCNAME = 128;
    public static final int MASK_XML11_CONTENT_INTERNAL = 48;

    public static boolean isXML11Space(int c) {
        return c < 65536 && (XML11CHARS[c] & 2) != 0;
    }

    public static boolean isXML11Valid(int c) {
        return c < 65536 && (XML11CHARS[c] & 1) != 0 || 65536 <= c && c <= 0x10FFFF;
    }

    public static boolean isXML11Invalid(int c) {
        return !XML11Char.isXML11Valid(c);
    }

    public static boolean isXML11ValidLiteral(int c) {
        return c < 65536 && (XML11CHARS[c] & 1) != 0 && (XML11CHARS[c] & 0x10) == 0 || 65536 <= c && c <= 0x10FFFF;
    }

    public static boolean isXML11Content(int c) {
        return c < 65536 && (XML11CHARS[c] & 0x20) != 0 || 65536 <= c && c <= 0x10FFFF;
    }

    public static boolean isXML11InternalEntityContent(int c) {
        return c < 65536 && (XML11CHARS[c] & 0x30) != 0 || 65536 <= c && c <= 0x10FFFF;
    }

    public static boolean isXML11NameStart(int c) {
        return c < 65536 && (XML11CHARS[c] & 4) != 0 || 65536 <= c && c < 983040;
    }

    public static boolean isXML11Name(int c) {
        return c < 65536 && (XML11CHARS[c] & 8) != 0 || c >= 65536 && c < 983040;
    }

    public static boolean isXML11NCNameStart(int c) {
        return c < 65536 && (XML11CHARS[c] & 0x40) != 0 || 65536 <= c && c < 983040;
    }

    public static boolean isXML11NCName(int c) {
        return c < 65536 && (XML11CHARS[c] & 0x80) != 0 || 65536 <= c && c < 983040;
    }

    public static boolean isXML11NameHighSurrogate(int c) {
        return 55296 <= c && c <= 56191;
    }

    public static boolean isXML11ValidName(String name) {
        char ch2;
        int length = name.length();
        if (length == 0) {
            return false;
        }
        int i = 1;
        char ch = name.charAt(0);
        if (!XML11Char.isXML11NameStart(ch)) {
            if (length > 1 && XML11Char.isXML11NameHighSurrogate(ch)) {
                ch2 = name.charAt(1);
                if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NameStart(XMLChar.supplemental(ch, ch2))) {
                    return false;
                }
                i = 2;
            } else {
                return false;
            }
        }
        while (i < length) {
            ch = name.charAt(i);
            if (!XML11Char.isXML11Name(ch)) {
                if (++i < length && XML11Char.isXML11NameHighSurrogate(ch)) {
                    ch2 = name.charAt(i);
                    if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    public static boolean isXML11ValidNCName(String ncName) {
        char ch2;
        int length = ncName.length();
        if (length == 0) {
            return false;
        }
        int i = 1;
        char ch = ncName.charAt(0);
        if (!XML11Char.isXML11NCNameStart(ch)) {
            if (length > 1 && XML11Char.isXML11NameHighSurrogate(ch)) {
                ch2 = ncName.charAt(1);
                if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(ch, ch2))) {
                    return false;
                }
                i = 2;
            } else {
                return false;
            }
        }
        while (i < length) {
            ch = ncName.charAt(i);
            if (!XML11Char.isXML11NCName(ch)) {
                if (++i < length && XML11Char.isXML11NameHighSurrogate(ch)) {
                    ch2 = ncName.charAt(i);
                    if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NCName(XMLChar.supplemental(ch, ch2))) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    public static boolean isXML11ValidNmtoken(String nmtoken) {
        int length = nmtoken.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            char ch = nmtoken.charAt(i);
            if (XML11Char.isXML11Name(ch)) continue;
            if (++i < length && XML11Char.isXML11NameHighSurrogate(ch)) {
                char ch2 = nmtoken.charAt(i);
                if (XMLChar.isLowSurrogate(ch2) && XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    public static boolean isXML11ValidQName(String str) {
        int colon = str.indexOf(58);
        if (colon == 0 || colon == str.length() - 1) {
            return false;
        }
        if (colon > 0) {
            String prefix = str.substring(0, colon);
            String localPart = str.substring(colon + 1);
            return XML11Char.isXML11ValidNCName(prefix) && XML11Char.isXML11ValidNCName(localPart);
        }
        return XML11Char.isXML11ValidNCName(str);
    }

    static {
        Arrays.fill(XML11CHARS, 1, 9, (byte)17);
        XML11Char.XML11CHARS[9] = 35;
        XML11Char.XML11CHARS[10] = 3;
        Arrays.fill(XML11CHARS, 11, 13, (byte)17);
        XML11Char.XML11CHARS[13] = 3;
        Arrays.fill(XML11CHARS, 14, 32, (byte)17);
        XML11Char.XML11CHARS[32] = 35;
        Arrays.fill(XML11CHARS, 33, 38, (byte)33);
        XML11Char.XML11CHARS[38] = 1;
        Arrays.fill(XML11CHARS, 39, 45, (byte)33);
        Arrays.fill(XML11CHARS, 45, 47, (byte)-87);
        XML11Char.XML11CHARS[47] = 33;
        Arrays.fill(XML11CHARS, 48, 58, (byte)-87);
        XML11Char.XML11CHARS[58] = 45;
        XML11Char.XML11CHARS[59] = 33;
        XML11Char.XML11CHARS[60] = 1;
        Arrays.fill(XML11CHARS, 61, 65, (byte)33);
        Arrays.fill(XML11CHARS, 65, 91, (byte)-19);
        Arrays.fill(XML11CHARS, 91, 93, (byte)33);
        XML11Char.XML11CHARS[93] = 1;
        XML11Char.XML11CHARS[94] = 33;
        XML11Char.XML11CHARS[95] = -19;
        XML11Char.XML11CHARS[96] = 33;
        Arrays.fill(XML11CHARS, 97, 123, (byte)-19);
        Arrays.fill(XML11CHARS, 123, 127, (byte)33);
        Arrays.fill(XML11CHARS, 127, 133, (byte)17);
        XML11Char.XML11CHARS[133] = 35;
        Arrays.fill(XML11CHARS, 134, 160, (byte)17);
        Arrays.fill(XML11CHARS, 160, 183, (byte)33);
        XML11Char.XML11CHARS[183] = -87;
        Arrays.fill(XML11CHARS, 184, 192, (byte)33);
        Arrays.fill(XML11CHARS, 192, 215, (byte)-19);
        XML11Char.XML11CHARS[215] = 33;
        Arrays.fill(XML11CHARS, 216, 247, (byte)-19);
        XML11Char.XML11CHARS[247] = 33;
        Arrays.fill(XML11CHARS, 248, 768, (byte)-19);
        Arrays.fill(XML11CHARS, 768, 880, (byte)-87);
        Arrays.fill(XML11CHARS, 880, 894, (byte)-19);
        XML11Char.XML11CHARS[894] = 33;
        Arrays.fill(XML11CHARS, 895, 8192, (byte)-19);
        Arrays.fill(XML11CHARS, 8192, 8204, (byte)33);
        Arrays.fill(XML11CHARS, 8204, 8206, (byte)-19);
        Arrays.fill(XML11CHARS, 8206, 8232, (byte)33);
        XML11Char.XML11CHARS[8232] = 35;
        Arrays.fill(XML11CHARS, 8233, 8255, (byte)33);
        Arrays.fill(XML11CHARS, 8255, 8257, (byte)-87);
        Arrays.fill(XML11CHARS, 8257, 8304, (byte)33);
        Arrays.fill(XML11CHARS, 8304, 8592, (byte)-19);
        Arrays.fill(XML11CHARS, 8592, 11264, (byte)33);
        Arrays.fill(XML11CHARS, 11264, 12272, (byte)-19);
        Arrays.fill(XML11CHARS, 12272, 12289, (byte)33);
        Arrays.fill(XML11CHARS, 12289, 55296, (byte)-19);
        Arrays.fill(XML11CHARS, 57344, 63744, (byte)33);
        Arrays.fill(XML11CHARS, 63744, 64976, (byte)-19);
        Arrays.fill(XML11CHARS, 64976, 65008, (byte)33);
        Arrays.fill(XML11CHARS, 65008, 65534, (byte)-19);
    }
}

