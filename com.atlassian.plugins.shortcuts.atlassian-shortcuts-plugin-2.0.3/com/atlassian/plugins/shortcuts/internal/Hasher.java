/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.shortcuts.internal;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    public static String getHash(Object shortcuts) {
        MessageDigest messageDigest = Hasher.getMessageDigest();
        messageDigest.update(shortcuts.toString().getBytes());
        byte[] digest = messageDigest.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hash = bigInt.toString(16);
        return hash;
    }

    private static MessageDigest getMessageDigest() {
        MessageDigest messageDigest = Hasher.getMessageDigest("MD5");
        if (messageDigest == null) {
            messageDigest = Hasher.getMessageDigest("SHA");
        }
        if (messageDigest == null) {
            throw new RuntimeException("Unable to retrieve a valid message digest!");
        }
        return messageDigest;
    }

    private static MessageDigest getMessageDigest(String digestName) {
        try {
            return MessageDigest.getInstance(digestName);
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}

