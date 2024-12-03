/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.util;

import io.micrometer.common.lang.Nullable;

public final class StringEscapeUtils {
    private static final String[] REPLACEMENT_CHARS = new String[128];
    private static final String U2028 = "\\u2028";
    private static final String U2029 = "\\u2029";

    public static String escapeJson(@Nullable String v) {
        if (v == null) {
            return "";
        }
        int length = v.length();
        if (length == 0) {
            return v;
        }
        int afterReplacement = 0;
        StringBuilder builder = null;
        for (int i = 0; i < length; ++i) {
            String replacement;
            char c = v.charAt(i);
            if (c < '\u0080') {
                replacement = REPLACEMENT_CHARS[c];
                if (replacement == null) {
                    continue;
                }
            } else if (c == '\u2028') {
                replacement = U2028;
            } else {
                if (c != '\u2029') continue;
                replacement = U2029;
            }
            if (afterReplacement < i) {
                if (builder == null) {
                    builder = new StringBuilder(length);
                }
                builder.append(v, afterReplacement, i);
            }
            if (builder == null) {
                builder = new StringBuilder(length);
            }
            builder.append(replacement);
            afterReplacement = i + 1;
        }
        if (builder == null) {
            return v;
        }
        if (afterReplacement < length) {
            builder.append(v, afterReplacement, length);
        }
        return builder.toString();
    }

    private StringEscapeUtils() {
    }

    static {
        for (int i = 0; i <= 31; ++i) {
            StringEscapeUtils.REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
        }
        StringEscapeUtils.REPLACEMENT_CHARS[34] = "\\\"";
        StringEscapeUtils.REPLACEMENT_CHARS[92] = "\\\\";
        StringEscapeUtils.REPLACEMENT_CHARS[9] = "\\t";
        StringEscapeUtils.REPLACEMENT_CHARS[8] = "\\b";
        StringEscapeUtils.REPLACEMENT_CHARS[10] = "\\n";
        StringEscapeUtils.REPLACEMENT_CHARS[13] = "\\r";
        StringEscapeUtils.REPLACEMENT_CHARS[12] = "\\f";
    }
}

