/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.analytics.client;

import org.apache.commons.codec.digest.DigestUtils;

public class AnalyticsMd5Hasher {
    private static final String SALT_WORD = "atlassian-analytics";

    public static String md5Hex(String input, String salt) {
        return input != null && salt != null ? DigestUtils.md5Hex((String)(salt + input)) : null;
    }

    public static String md5Hex(String input) {
        return AnalyticsMd5Hasher.md5Hex(input, SALT_WORD);
    }
}

