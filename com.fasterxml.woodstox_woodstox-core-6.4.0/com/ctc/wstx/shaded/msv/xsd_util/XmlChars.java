/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.xsd_util;

public class XmlChars {
    private XmlChars() {
    }

    public static boolean isChar(int ucs4char) {
        return ucs4char >= 32 && ucs4char <= 55295 || ucs4char == 10 || ucs4char == 9 || ucs4char == 13 || ucs4char >= 57344 && ucs4char <= 65533 || ucs4char >= 65536 && ucs4char <= 0x10FFFF;
    }

    public static boolean isNameChar(char c) {
        if (XmlChars.isLetter2(c)) {
            return true;
        }
        if (c == '>') {
            return false;
        }
        return c == '.' || c == '-' || c == '_' || c == ':' || XmlChars.isExtender(c);
    }

    public static boolean isNCNameChar(char c) {
        return c != ':' && XmlChars.isNameChar(c);
    }

    public static boolean isSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static boolean isLetter(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c == '/') {
            return false;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        switch (Character.getType(c)) {
            case 1: 
            case 2: 
            case 3: 
            case 5: 
            case 10: {
                return !XmlChars.isCompatibilityChar(c) && (c < '\u20dd' || c > '\u20e0');
            }
        }
        return c >= '\u02bb' && c <= '\u02c1' || c == '\u0559' || c == '\u06e5' || c == '\u06e6';
    }

    private static boolean isCompatibilityChar(char c) {
        switch (c >> 8 & 0xFF) {
            case 0: {
                return c == '\u00aa' || c == '\u00b5' || c == '\u00ba';
            }
            case 1: {
                return c >= '\u0132' && c <= '\u0133' || c >= '\u013f' && c <= '\u0140' || c == '\u0149' || c == '\u017f' || c >= '\u01c4' && c <= '\u01cc' || c >= '\u01f1' && c <= '\u01f3';
            }
            case 2: {
                return c >= '\u02b0' && c <= '\u02b8' || c >= '\u02e0' && c <= '\u02e4';
            }
            case 3: {
                return c == '\u037a';
            }
            case 5: {
                return c == '\u0587';
            }
            case 14: {
                return c >= '\u0edc' && c <= '\u0edd';
            }
            case 17: {
                return c == '\u1101' || c == '\u1104' || c == '\u1108' || c == '\u110a' || c == '\u110d' || c >= '\u1113' && c <= '\u113b' || c == '\u113d' || c == '\u113f' || c >= '\u1141' && c <= '\u114b' || c == '\u114d' || c == '\u114f' || c >= '\u1151' && c <= '\u1153' || c >= '\u1156' && c <= '\u1158' || c == '\u1162' || c == '\u1164' || c == '\u1166' || c == '\u1168' || c >= '\u116a' && c <= '\u116c' || c >= '\u116f' && c <= '\u1171' || c == '\u1174' || c >= '\u1176' && c <= '\u119d' || c >= '\u119f' && c <= '\u11a2' || c >= '\u11a9' && c <= '\u11aa' || c >= '\u11ac' && c <= '\u11ad' || c >= '\u11b0' && c <= '\u11b6' || c == '\u11b9' || c == '\u11bb' || c >= '\u11c3' && c <= '\u11ea' || c >= '\u11ec' && c <= '\u11ef' || c >= '\u11f1' && c <= '\u11f8';
            }
            case 32: {
                return c == '\u207f';
            }
            case 33: {
                return c == '\u2102' || c == '\u2107' || c >= '\u210a' && c <= '\u2113' || c == '\u2115' || c >= '\u2118' && c <= '\u211d' || c == '\u2124' || c == '\u2128' || c >= '\u212c' && c <= '\u212d' || c >= '\u212f' && c <= '\u2138' || c >= '\u2160' && c <= '\u217f';
            }
            case 48: {
                return c >= '\u309b' && c <= '\u309c';
            }
            case 49: {
                return c >= '\u3131' && c <= '\u318e';
            }
            case 249: 
            case 250: 
            case 251: 
            case 252: 
            case 253: 
            case 254: 
            case 255: {
                return true;
            }
        }
        return false;
    }

    private static boolean isLetter2(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c == '>') {
            return false;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        switch (Character.getType(c)) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: {
                return !XmlChars.isCompatibilityChar(c) && (c < '\u20dd' || c > '\u20e0');
            }
            case 16: {
                return c == '\u06dd';
            }
        }
        return c == '\u0387' || c == '\u212e';
    }

    private static boolean isExtender(char c) {
        return c == '\u00b7' || c == '\u02d0' || c == '\u02d1' || c == '\u0387' || c == '\u0640' || c == '\u0e46' || c == '\u0ec6' || c == '\u3005' || c >= '\u3031' && c <= '\u3035' || c >= '\u309d' && c <= '\u309e' || c >= '\u30fc' && c <= '\u30fe';
    }
}

