/*
 * Decompiled with CFR 0.152.
 */
package org.dom4j.util;

public final class StringUtils {
    private StringUtils() {
    }

    public static boolean startsWithWhitespace(CharSequence charSeq) {
        if (charSeq.length() == 0) {
            return false;
        }
        return Character.isWhitespace(charSeq.charAt(0));
    }

    public static boolean endsWithWhitespace(CharSequence charSeq) {
        if (charSeq.length() == 0) {
            return false;
        }
        return Character.isWhitespace(charSeq.charAt(charSeq.length() - 1));
    }
}

