/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.secrets.api.SecretStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.secrets.store.base64;

import com.atlassian.secrets.api.SecretStore;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base64SecretStore
implements SecretStore {
    private static final Logger log = LoggerFactory.getLogger(Base64SecretStore.class);

    public Base64SecretStore() {
        log.debug("Initiate Base64Cipher");
    }

    public String store(String plainTextData) {
        log.debug("Encrypting data...");
        try {
            String processed = this.encodeToString(plainTextData.getBytes());
            log.debug("Encryption done.");
            return processed;
        }
        catch (Exception runtimeException) {
            log.error("Runtime Exception thrown when encrypting: {}", (Object)plainTextData, (Object)runtimeException);
            throw runtimeException;
        }
    }

    public String get(String encryptedData) {
        log.debug("Decrypting data...");
        try {
            String processed = new String(this.decodeFromString(encryptedData));
            log.debug("Decryption done.");
            return processed;
        }
        catch (Exception runtimeException) {
            log.error("Runtime Exception thrown when decrypting: {}", (Object)encryptedData, (Object)runtimeException);
            throw runtimeException;
        }
    }

    private String encodeToString(byte[] src) {
        return Base64.getEncoder().encodeToString(src);
    }

    private byte[] decodeFromString(String src) {
        return Base64.getDecoder().decode(src);
    }
}

