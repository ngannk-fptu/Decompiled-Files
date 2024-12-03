/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

final class StringHelper {
    static String EMPTY_STRING = "";

    StringHelper() {
    }

    static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    static String createBase64EncodedSha256Hash(String stringToHash) {
        return StringHelper.createSha256Hash(stringToHash, true);
    }

    static String createSha256Hash(String stringToHash) {
        return StringHelper.createSha256Hash(stringToHash, false);
    }

    private static String createSha256Hash(String stringToHash, boolean base64Encode) {
        String res;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
            res = base64Encode ? Base64.getUrlEncoder().withoutPadding().encodeToString(hash) : new String(hash, StandardCharsets.UTF_8);
        }
        catch (NoSuchAlgorithmException e) {
            res = null;
        }
        return res;
    }
}

