/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Encryptor
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.embedded.api.Encryptor;
import java.util.Base64;

public class Base64Encryptor
implements Encryptor {
    public String encrypt(String password) {
        return new String(Base64.getEncoder().encode(password.getBytes()));
    }

    public String decrypt(String encryptedPassword) {
        try {
            return new String(Base64.getDecoder().decode(encryptedPassword.getBytes()));
        }
        catch (IllegalArgumentException e) {
            return encryptedPassword;
        }
    }
}

