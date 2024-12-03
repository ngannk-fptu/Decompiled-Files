/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.model;

import java.security.KeyStore;

public class KeyStoreSettings {
    private final KeyStore keyStore;
    private final String spAlias;
    private final String spKeyPass;

    public KeyStoreSettings(KeyStore keyStore, String spAlias, String spKeyPass) {
        this.keyStore = keyStore;
        this.spAlias = spAlias;
        this.spKeyPass = spKeyPass;
    }

    public final KeyStore getKeyStore() {
        return this.keyStore;
    }

    public final String getSpAlias() {
        return this.spAlias;
    }

    public final String getSpKeyPass() {
        return this.spKeyPass;
    }
}

