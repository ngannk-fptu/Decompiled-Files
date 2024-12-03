/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.db.config.password.Cipher
 *  com.atlassian.secrets.api.SecretStore
 */
package com.atlassian.secrets;

import com.atlassian.db.config.password.Cipher;
import com.atlassian.secrets.api.SecretStore;

public class DelegateCipherSecretStore
implements SecretStore {
    private final Cipher delegate;

    public DelegateCipherSecretStore() {
        throw new UnsupportedOperationException("This class should not be used explicitly");
    }

    public DelegateCipherSecretStore(Cipher cipher) {
        this.delegate = cipher;
    }

    public String store(String plainTextData) {
        return this.delegate.encrypt(plainTextData);
    }

    public String get(String encryptedData) {
        return this.delegate.decrypt(encryptedData);
    }
}

