/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Hex
 */
package com.atlassian.plugin.webresource.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

public class HashBuilder {
    private static final String UTF8 = "UTF-8";
    private static final String MD5 = "MD5";
    private final MessageDigest md5;

    public HashBuilder() {
        try {
            this.md5 = MessageDigest.getInstance(MD5);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 hashing algorithm is not available.", e);
        }
    }

    public static String buildHash(String ... values) {
        HashBuilder hashBuilder = new HashBuilder();
        for (String value : values) {
            hashBuilder.add(value);
        }
        return hashBuilder.build();
    }

    public static String buildHash(Iterable<String> values) {
        HashBuilder hashBuilder = new HashBuilder();
        for (String value : values) {
            hashBuilder.add(value);
        }
        return hashBuilder.build();
    }

    public void add(String value) {
        try {
            this.md5.update(value.getBytes(UTF8));
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError((Object)"UTF-8 encoding is not available.");
        }
    }

    public String build() {
        return new String(Hex.encodeHex((byte[])this.md5.digest()));
    }
}

