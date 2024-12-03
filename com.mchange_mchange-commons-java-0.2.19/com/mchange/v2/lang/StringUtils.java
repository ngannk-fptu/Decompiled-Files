/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lang;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class StringUtils {
    static final Pattern COMMA_SEP_TRIM_REGEX;
    static final Pattern COMMA_SEP_NO_TRIM_REGEX;
    public static final String[] EMPTY_STRING_ARRAY;

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

    public static String[] splitCommaSeparated(String string, boolean bl) {
        Pattern pattern = bl ? COMMA_SEP_TRIM_REGEX : COMMA_SEP_NO_TRIM_REGEX;
        return pattern.split(string);
    }

    static {
        try {
            COMMA_SEP_TRIM_REGEX = Pattern.compile("\\s*\\,\\s*");
            COMMA_SEP_NO_TRIM_REGEX = Pattern.compile("\\,");
        }
        catch (PatternSyntaxException patternSyntaxException) {
            patternSyntaxException.printStackTrace();
            throw new InternalError(patternSyntaxException.toString());
        }
        EMPTY_STRING_ARRAY = new String[0];
    }
}

