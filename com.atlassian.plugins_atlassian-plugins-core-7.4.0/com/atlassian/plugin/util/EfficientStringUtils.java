/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

public class EfficientStringUtils {
    public static boolean endsWith(String src, String ... suffixes) {
        int pos = src.length();
        for (int i = suffixes.length - 1; i >= 0; --i) {
            String suffix = suffixes[i];
            if (src.startsWith(suffix, pos -= suffix.length())) continue;
            return false;
        }
        return true;
    }
}

