/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 */
package com.atlassian.diagnostics.internal.analytics;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class AnalyticsUtils {
    public static String toUuidFormat(String value) {
        if (value == null) {
            return null;
        }
        String md5 = Hashing.md5().hashString((CharSequence)value, StandardCharsets.UTF_8).toString();
        return md5.substring(0, 8) + '-' + md5.substring(8, 12) + "-" + md5.substring(12, 16) + "-" + md5.substring(16, 20) + "-" + md5.substring(20, 32);
    }
}

