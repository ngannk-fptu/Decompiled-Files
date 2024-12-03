/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.upm.impl;

import org.apache.commons.codec.digest.DigestUtils;

public class LongKeyHasher {
    private static final Integer MAX_KEY_LENGTH = 100;

    public static String hashKeyIfTooLong(String key) {
        if (key.length() > MAX_KEY_LENGTH) {
            String keyHash = DigestUtils.md5Hex((String)key);
            String keptOriginalKey = key.substring(0, MAX_KEY_LENGTH - keyHash.length());
            String hashedKey = keptOriginalKey + keyHash;
            return hashedKey;
        }
        return key;
    }
}

