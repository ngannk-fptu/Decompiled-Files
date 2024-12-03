/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.codec;

import brave.internal.codec.WriteBuffer;

public final class JsonEscaper {
    private static final String[] REPLACEMENT_CHARS = new String[128];
    private static final String U2028 = "\\u2028";
    private static final String U2029 = "\\u2029";

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

    public static void jsonEscape(CharSequence in, WriteBuffer out) {
        int length = in.length();
        if (length == 0) {
            return;
        }
        int afterReplacement = 0;
        for (int i = 0; i < length; ++i) {
            String replacement;
            char c = in.charAt(i);
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
                out.writeUtf8(in, afterReplacement, i);
            }
            out.writeUtf8(replacement, 0, replacement.length());
            afterReplacement = i + 1;
        }
        if (afterReplacement < length) {
            out.writeUtf8(in, afterReplacement, length);
        }
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

