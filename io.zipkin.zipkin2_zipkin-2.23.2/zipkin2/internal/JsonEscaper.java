/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import zipkin2.internal.WriteBuffer;

public final class JsonEscaper {
    private static final String[] REPLACEMENT_CHARS = new String[128];
    private static final String U2028 = "\\u2028";
    private static final String U2029 = "\\u2029";

    public static CharSequence jsonEscape(CharSequence v) {
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
        return builder;
    }

    public static int jsonEscapedSizeInBytes(CharSequence v) {
        boolean ascii = true;
        int escapingOverhead = 0;
        int length = v.length();
        for (int i = 0; i < length; ++i) {
            char c = v.charAt(i);
            if (c == '\u2028' || c == '\u2029') {
                escapingOverhead += 5;
                continue;
            }
            if (c >= '\u0080') {
                ascii = false;
                continue;
            }
            String maybeReplacement = REPLACEMENT_CHARS[c];
            if (maybeReplacement == null) continue;
            escapingOverhead += maybeReplacement.length() - 1;
        }
        if (ascii) {
            return v.length() + escapingOverhead;
        }
        return WriteBuffer.utf8SizeInBytes(v) + escapingOverhead;
    }

    static {
        for (int i = 0; i <= 31; ++i) {
            JsonEscaper.REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
        }
        JsonEscaper.REPLACEMENT_CHARS[34] = "\\\"";
        JsonEscaper.REPLACEMENT_CHARS[92] = "\\\\";
        JsonEscaper.REPLACEMENT_CHARS[9] = "\\t";
        JsonEscaper.REPLACEMENT_CHARS[8] = "\\b";
        JsonEscaper.REPLACEMENT_CHARS[10] = "\\n";
        JsonEscaper.REPLACEMENT_CHARS[13] = "\\r";
        JsonEscaper.REPLACEMENT_CHARS[12] = "\\f";
    }
}

