/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.http.mime;

class StringUtils {
    StringUtils() {
    }

    static boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }
}

