/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.StringUtils
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
        return ConstantTimeComparison.isEqual(StringUtils.getBytesUtf16((String)a), StringUtils.getBytesUtf16((String)b));
    }
}

