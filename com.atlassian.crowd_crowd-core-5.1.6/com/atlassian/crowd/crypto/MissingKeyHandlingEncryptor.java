/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Encryptor
 *  com.atlassian.crowd.exception.crypto.MissingKeyException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.embedded.api.Encryptor;
import com.atlassian.crowd.exception.crypto.MissingKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MissingKeyHandlingEncryptor
implements Encryptor {
    private static final Logger log = LoggerFactory.getLogger(MissingKeyHandlingEncryptor.class);
    private final Encryptor delegate;

    public MissingKeyHandlingEncryptor(Encryptor delegate) {
        this.delegate = delegate;
    }

    public String encrypt(String password) {
        try {
            return this.delegate.encrypt(password);
        }
        catch (MissingKeyException e) {
            log.warn("Encryption key {} is missing. Generating new key.", (Object)e.getMessage());
            this.delegate.changeEncryptionKey();
            return this.delegate.encrypt(password);
        }
    }

    public String decrypt(String encryptedPassword) {
        return this.delegate.decrypt(encryptedPassword);
    }

    public boolean changeEncryptionKey() {
        return this.delegate.changeEncryptionKey();
    }
}

