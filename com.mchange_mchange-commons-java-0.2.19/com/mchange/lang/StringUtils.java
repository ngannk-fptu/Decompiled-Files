/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.lang;

import java.io.UnsupportedEncodingException;

public final class StringUtils {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String normalString(String string) {
        return StringUtils.nonEmptyTrimmedOrNull(string);
    }

    public static boolean nonEmptyString(String string) {
        return string != null && string.length() > 0;
    }

    public static boolean nonWhitespaceString(String string) {
        return string != null && string.trim().length() > 0;
    }

    public static String nonEmptyOrNull(String string) {
        return StringUtils.nonEmptyString(string) ? string : null;
    }

    public static String nonNullOrBlank(String string) {
        return string != null ? string : "";
    }

    public static String nonEmptyTrimmedOrNull(String string) {
        String string2 = string;
        if (string2 != null) {
            string2 = (string2 = string2.trim()).length() > 0 ? string2 : null;
        }
        return string2;
    }

    public static byte[] getUTF8Bytes(String string) {
        try {
            return string.getBytes("UTF8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
            throw new InternalError("UTF8 is an unsupported encoding?!?");
        }
    }
}

