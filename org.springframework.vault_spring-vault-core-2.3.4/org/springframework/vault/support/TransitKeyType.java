/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

public enum TransitKeyType {
    ENCRYPTION_KEY("encryption-key"),
    SIGNING_KEY("signing-key"),
    HMAC_KEY("hmac-key");

    final String value;

    private TransitKeyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

