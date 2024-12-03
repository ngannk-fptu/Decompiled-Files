/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.util;

public final class TextUtils {
    private TextUtils() {
    }

    public static boolean isEmpty(CharSequence s) {
        return TextUtils.length(s) == 0;
    }

    public static boolean isBlank(CharSequence s) {
        int strLen = TextUtils.length(s);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static boolean containsBlanks(CharSequence s) {
        int strLen = TextUtils.length(s);
        if (strLen == 0) {
            return false;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) continue;
            return true;
        }
        return false;
    }

    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            int unsignedB = bytes[i] & 0xFF;
            if (unsignedB < 16) {
                buffer.append('0');
            }
            buffer.append(Integer.toHexString(unsignedB));
        }
        return buffer.toString();
    }
}

