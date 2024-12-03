/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aws.utility;

public final class StringUtil {
    private StringUtil() {
    }

    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }
        return s.trim().isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return !StringUtil.isEmpty(s);
    }
}

