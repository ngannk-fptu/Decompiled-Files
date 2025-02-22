/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5Util {
    private MD5Util() {
    }

    public static String toMD5String(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (md == null || str == null) {
                return null;
            }
            byte[] byteData = md.digest(str.getBytes(Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xFF) + 256, 16).substring(1));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException ignored) {
            return null;
        }
    }
}

