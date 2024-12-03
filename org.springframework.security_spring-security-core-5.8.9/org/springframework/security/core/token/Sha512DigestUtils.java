/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.crypto.codec.Hex
 */
package org.springframework.security.core.token;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.security.crypto.codec.Hex;

public abstract class Sha512DigestUtils {
    private static MessageDigest getSha512Digest() {
        try {
            return MessageDigest.getInstance("SHA-512");
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static byte[] sha(byte[] data) {
        return Sha512DigestUtils.getSha512Digest().digest(data);
    }

    public static byte[] sha(String data) {
        return Sha512DigestUtils.sha(data.getBytes());
    }

    public static String shaHex(byte[] data) {
        return new String(Hex.encode((byte[])Sha512DigestUtils.sha(data)));
    }

    public static String shaHex(String data) {
        return new String(Hex.encode((byte[])Sha512DigestUtils.sha(data)));
    }
}

