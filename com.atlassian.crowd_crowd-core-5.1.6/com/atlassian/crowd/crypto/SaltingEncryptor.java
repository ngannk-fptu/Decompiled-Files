/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Encryptor
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.embedded.api.Encryptor;
import java.util.UUID;

public class SaltingEncryptor
implements Encryptor {
    static final String SALT_SEPARATOR = "|SALT-";
    private static final int UUID_LENGTH = 36;
    private static final int SALT_LENGTH = 36 + "|SALT-".length();
    private final Encryptor delegate;

    public SaltingEncryptor(Encryptor delegate) {
        this.delegate = delegate;
    }

    public String encrypt(String password) {
        return this.delegate.encrypt(this.addSalt(password));
    }

    public String decrypt(String encryptedPassword) {
        return this.removeSaltIfNeeded(this.delegate.decrypt(encryptedPassword));
    }

    private String addSalt(String password) {
        if (password == null) {
            return null;
        }
        return password + SALT_SEPARATOR + UUID.randomUUID();
    }

    private String removeSaltIfNeeded(String text) {
        if (text == null) {
            return null;
        }
        int expectedSaltStart = text.length() - SALT_LENGTH;
        if (expectedSaltStart >= 0 && text.startsWith(SALT_SEPARATOR, expectedSaltStart)) {
            return text.substring(0, expectedSaltStart);
        }
        return text;
    }

    public boolean changeEncryptionKey() {
        return this.delegate.changeEncryptionKey();
    }
}

