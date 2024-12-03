/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.cli.db;

public class SecretStoreOutput {
    private final String secretValue;
    private final String secretStoreClass;

    public SecretStoreOutput(String secretValue, String secretStoreClass) {
        this.secretValue = secretValue;
        this.secretStoreClass = secretStoreClass;
    }

    public String getSecretValue() {
        return this.secretValue;
    }

    public String getSecretStoreClass() {
        return this.secretStoreClass;
    }
}

