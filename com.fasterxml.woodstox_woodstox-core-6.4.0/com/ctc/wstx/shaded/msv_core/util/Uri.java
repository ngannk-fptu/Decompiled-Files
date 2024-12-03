/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class Uri {
    private static String utf8 = "UTF-8";
    private static final String HEX_DIGITS = "0123456789abcdef";
    private static String excluded = "<>\"{}|\\^`";

    public static boolean isValid(String s) {
        return Uri.isValidPercent(s) && Uri.isValidFragment(s) && Uri.isValidScheme(s);
    }

    public static String escapeDisallowedChars(String s) {
        StringBuffer buf = null;
        int len = s.length();
        int done = 0;
        while (true) {
            byte[] bytes;
            int i = done;
            while (true) {
                if (i == len) {
                    if (done != 0) break;
                    return s;
                }
                if (Uri.isExcluded(s.charAt(i))) break;
                ++i;
            }
            if (buf == null) {
                buf = new StringBuffer();
            }
            if (i > done) {
                buf.append(s.substring(done, i));
                done = i;
            }
            if (i == len) break;
            ++i;
            while (i < len && Uri.isExcluded(s.charAt(i))) {
                ++i;
            }
            String tem = s.substring(done, i);
            try {
                bytes = tem.getBytes(utf8);
            }
            catch (UnsupportedEncodingException e) {
                utf8 = "UTF8";
                try {
                    bytes = tem.getBytes(utf8);
                }
                catch (UnsupportedEncodingException e2) {
                    return s;
                }
            }
            for (int j = 0; j < bytes.length; ++j) {
                buf.append('%');
                buf.append(HEX_DIGITS.charAt((bytes[j] & 0xFF) >> 4));
                buf.append(HEX_DIGITS.charAt(bytes[j] & 0xF));
            }
            done = i;
        }
        return buf.toString();
    }

    private static boolean isExcluded(char c) {
        return c <= ' ' || c >= '\u007f' || excluded.indexOf(c) >= 0;
    }

    private static boolean isAlpha(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
    }

    private static boolean isHexDigit(char c) {
        return 'a' <= c && c <= 'f' || 'A' <= c && c <= 'F' || Uri.isDigit(c);
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isSchemeChar(char c) {
        return Uri.isAlpha(c) || Uri.isDigit(c) || c == '+' || c == '-' || c == '.';
    }

    private static boolean isValidPercent(String s) {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            if (s.charAt(i) != '%') continue;
            if (i + 2 >= len) {
                return false;
            }
            if (Uri.isHexDigit(s.charAt(i + 1)) && Uri.isHexDigit(s.charAt(i + 2))) continue;
            return false;
        }
        return true;
    }

    private static boolean isValidFragment(String s) {
        int i = s.indexOf(35);
        return i < 0 || s.indexOf(35, i + 1) < 0;
    }

    private static boolean isValidScheme(String s) {
        if (!Uri.isAbsolute(s)) {
            return true;
        }
        int i = s.indexOf(58);
        if (i == 0 || i + 1 == s.length() || !Uri.isAlpha(s.charAt(0))) {
            return false;
        }
        while (--i > 0) {
            if (Uri.isSchemeChar(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static String resolve(String baseUri, String uriReference) {
        if (!Uri.isAbsolute(uriReference) && baseUri != null && Uri.isAbsolute(baseUri)) {
            try {
                return new URL(new URL(baseUri), uriReference).toString();
            }
            catch (MalformedURLException malformedURLException) {
                // empty catch block
            }
        }
        return uriReference;
    }

    public static boolean hasFragmentId(String uri) {
        return uri.indexOf(35) >= 0;
    }

    public static boolean isAbsolute(String uri) {
        int i = uri.indexOf(58);
        if (i < 0) {
            return false;
        }
        while (--i >= 0) {
            switch (uri.charAt(i)) {
                case '#': 
                case '/': 
                case '?': {
                    return false;
                }
            }
        }
        return true;
    }
}

