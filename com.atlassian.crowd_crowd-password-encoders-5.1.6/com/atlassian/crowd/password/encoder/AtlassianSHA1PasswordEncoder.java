/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class AtlassianSHA1PasswordEncoder
implements InternalPasswordEncoder {
    public static final String ATLASSIAN_SHA1_KEY = "atlassian-sha1";

    @Override
    public String encodePassword(String password, Object salt) {
        byte[] hash;
        byte[] bytes = password.getBytes();
        try {
            hash = MessageDigest.getInstance("SHA-512").digest(bytes);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return new String(Base64.encodeBase64((byte[])hash));
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        boolean valid = false;
        if (encPass != null && rawPass != null) {
            valid = encPass.equals(this.encodePassword(rawPass, salt));
        }
        return valid;
    }

    @Override
    public String getKey() {
        return ATLASSIAN_SHA1_KEY;
    }
}

