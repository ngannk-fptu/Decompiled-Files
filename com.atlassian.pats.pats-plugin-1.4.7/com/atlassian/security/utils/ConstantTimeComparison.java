/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.utils;

import java.security.MessageDigest;
import org.apache.commons.codec.binary.StringUtils;

public class ConstantTimeComparison {
    private ConstantTimeComparison() {
    }

    public static boolean isEqual(byte[] a, byte[] b) throws NullPointerException {
        if (a == null || b == null) {
            throw new NullPointerException("ConstantTimeComparison.isEqual does not accept null values.");
        }
        return MessageDigest.isEqual(a, b);
    }

    public static boolean isEqual(String a, String b) throws NullPointerException {
        return ConstantTimeComparison.isEqual(StringUtils.getBytesUtf16(a), StringUtils.getBytesUtf16(b));
    }
}

