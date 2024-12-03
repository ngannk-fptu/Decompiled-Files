/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Hex
 *  org.springframework.stereotype.Service
 */
package com.atlassian.crowd.embedded.admin.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

@Service
public class GravatarService {
    public String calculateEmailHash(String email) {
        if (email == null) {
            return "";
        }
        try {
            return this.md5Hex(email.trim().toLowerCase());
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String md5Hex(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("md5");
        String trimmedAndLowercase = string.toLowerCase(Locale.ROOT).trim();
        byte[] bytes = trimmedAndLowercase.getBytes(StandardCharsets.UTF_8);
        return new String(Hex.encodeHex((byte[])digest.digest(bytes)));
    }
}

