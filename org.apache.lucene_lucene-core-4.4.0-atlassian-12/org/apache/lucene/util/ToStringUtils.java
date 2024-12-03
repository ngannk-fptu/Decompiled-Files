/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

public final class ToStringUtils {
    private ToStringUtils() {
    }

    public static String boost(float boost) {
        if (boost != 1.0f) {
            return "^" + Float.toString(boost);
        }
        return "";
    }

    public static void byteArray(StringBuilder buffer, byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            buffer.append("b[").append(i).append("]=").append(bytes[i]);
            if (i >= bytes.length - 1) continue;
            buffer.append(',');
        }
    }
}

