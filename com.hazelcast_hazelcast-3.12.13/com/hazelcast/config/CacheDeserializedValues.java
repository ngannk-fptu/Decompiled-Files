/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.util.StringUtil;
import java.util.Arrays;

public enum CacheDeserializedValues {
    NEVER,
    INDEX_ONLY,
    ALWAYS;


    public static CacheDeserializedValues parseString(String string) {
        String upperCase = StringUtil.upperCaseInternal(string);
        if ("NEVER".equals(upperCase)) {
            return NEVER;
        }
        if ("INDEX_ONLY".equals(upperCase) || "INDEX-ONLY".equals(upperCase)) {
            return INDEX_ONLY;
        }
        if ("ALWAYS".equals(upperCase)) {
            return ALWAYS;
        }
        throw new IllegalArgumentException("Unknown CacheDeserializedValues option '" + string + "'. Possible options: " + Arrays.toString((Object[])CacheDeserializedValues.values()));
    }
}

