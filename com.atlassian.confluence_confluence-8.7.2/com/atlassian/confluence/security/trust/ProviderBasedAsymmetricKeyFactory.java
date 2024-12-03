/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.EncryptionProvider
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.confluence.security.trust.AsymmetricKeyFactory;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public final class ProviderBasedAsymmetricKeyFactory
implements AsymmetricKeyFactory {
    private EncryptionProvider encryptionProvider;

    @Override
    public KeyPair getNewKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
        return this.encryptionProvider.generateNewKeyPair();
    }

    public void setEncryptionProvider(EncryptionProvider encryptionProvider) {
        this.encryptionProvider = encryptionProvider;
    }
}

