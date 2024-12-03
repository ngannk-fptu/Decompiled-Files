/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.util.NameValuePair;
import com.amazonaws.util.StringUtils;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.List;

@Immutable
class URLEncodedUtils {
    private static final char QP_SEP_A = '&';
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final BitSet UNRESERVED;
    private static final BitSet PUNCT;
    private static final BitSet USERINFO;
    private static final BitSet PATHSAFE;
    private static final BitSet URIC;
    private static final BitSet RESERVED;
    private static final BitSet URLENCODER;
    private static final int RADIX = 16;

    URLEncodedUtils() {
    }

    public static String format(List<? extends NameValuePair> parameters, String charset) {
        return URLEncodedUtils.format(parameters, '&', charset);
    }

    public static String format(List<? extends NameValuePair> parameters, char parameterSeparator, String charset) {
        StringBuilder result = new StringBuilder();
        for (NameValuePair nameValuePair : parameters) {
            String encodedName = URLEncodedUtils.encodeFormFields(nameValuePair.getName(), charset);
            String encodedValue = URLEncodedUtils.encodeFormFields(nameValuePair.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue == null) continue;
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    public static String format(Iterable<? extends NameValuePair> parameters, Charset charset) {
        return URLEncodedUtils.format(parameters, '&', charset);
    }

    public static String format(Iterable<? extends NameValuePair> parameters, char parameterSeparator, Charset charset) {
        StringBuilder result = new StringBuilder();
        for (NameValuePair nameValuePair : parameters) {
            String encodedName = URLEncodedUtils.encodeFormFields(nameValuePair.getName(), charset);
            String encodedValue = URLEncodedUtils.encodeFormFields(nameValuePair.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue == null) continue;
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    private static String urlEncode(String content, Charset charset, BitSet safechars, boolean blankAsPlus) {
        if (content == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        ByteBuffer bb = charset.encode(content);
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xFF;
            if (safechars.get(b)) {
                buf.append((char)b);
                continue;
            }
            if (blankAsPlus && b == 32) {
                buf.append('+');
                continue;
            }
            buf.append("%");
            char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
            char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
            buf.append(hex1);
            buf.append(hex2);
        }
        return buf.toString();
    }

    private static String encodeFormFields(String content, String charset) {
        if (content == null) {
            return null;
        }
        return URLEncodedUtils.urlEncode(content, charset != null ? Charset.forName(charset) : StringUtils.UTF8, URLENCODER, true);
    }

    private static String encodeFormFields(String content, Charset charset) {
        if (content == null) {
            return null;
        }
        return URLEncodedUtils.urlEncode(content, charset != null ? charset : StringUtils.UTF8, URLENCODER, true);
    }

    static String encUserInfo(String content, Charset charset) {
        return URLEncodedUtils.urlEncode(content, charset, USERINFO, false);
    }

    static String encUric(String content, Charset charset) {
        return URLEncodedUtils.urlEncode(content, charset, URIC, false);
    }

    static String encPath(String content, Charset charset) {
        return URLEncodedUtils.urlEncode(content, charset, PATHSAFE, false);
    }

    static {
        int i;
        UNRESERVED = new BitSet(256);
        PUNCT = new BitSet(256);
        USERINFO = new BitSet(256);
        PATHSAFE = new BitSet(256);
        URIC = new BitSet(256);
        RESERVED = new BitSet(256);
        URLENCODER = new BitSet(256);
        for (i = 97; i <= 122; ++i) {
            UNRESERVED.set(i);
        }
        for (i = 65; i <= 90; ++i) {
            UNRESERVED.set(i);
        }
        for (i = 48; i <= 57; ++i) {
            UNRESERVED.set(i);
        }
        UNRESERVED.set(95);
        UNRESERVED.set(45);
        UNRESERVED.set(46);
        UNRESERVED.set(42);
        URLENCODER.or(UNRESERVED);
        UNRESERVED.set(33);
        UNRESERVED.set(126);
        UNRESERVED.set(39);
        UNRESERVED.set(40);
        UNRESERVED.set(41);
        PUNCT.set(44);
        PUNCT.set(59);
        PUNCT.set(58);
        PUNCT.set(36);
        PUNCT.set(38);
        PUNCT.set(43);
        PUNCT.set(61);
        USERINFO.or(UNRESERVED);
        USERINFO.or(PUNCT);
        PATHSAFE.or(UNRESERVED);
        PATHSAFE.set(47);
        PATHSAFE.set(59);
        PATHSAFE.set(58);
        PATHSAFE.set(64);
        PATHSAFE.set(38);
        PATHSAFE.set(61);
        PATHSAFE.set(43);
        PATHSAFE.set(36);
        PATHSAFE.set(44);
        RESERVED.set(59);
        RESERVED.set(47);
        RESERVED.set(63);
        RESERVED.set(58);
        RESERVED.set(64);
        RESERVED.set(38);
        RESERVED.set(61);
        RESERVED.set(43);
        RESERVED.set(36);
        RESERVED.set(44);
        RESERVED.set(91);
        RESERVED.set(93);
        URIC.or(RESERVED);
        URIC.or(UNRESERVED);
    }
}

