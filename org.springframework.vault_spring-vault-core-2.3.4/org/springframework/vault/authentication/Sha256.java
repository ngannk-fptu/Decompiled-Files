/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.util.Assert;

class Sha256 {
    private static final Charset US_ASCII = StandardCharsets.US_ASCII;

    Sha256() {
    }

    public static String toSha256(String content) {
        Assert.hasText((String)content, (String)"Content must not be empty");
        MessageDigest messageDigest = Sha256.getMessageDigest("SHA-256");
        byte[] digest = messageDigest.digest(content.getBytes(US_ASCII));
        return Sha256.toHexString(digest);
    }

    private static MessageDigest getMessageDigest(String algorithm) throws IllegalArgumentException {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm [" + algorithm + "]");
        }
    }

    static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%X", b));
        }
        return sb.toString();
    }
}

