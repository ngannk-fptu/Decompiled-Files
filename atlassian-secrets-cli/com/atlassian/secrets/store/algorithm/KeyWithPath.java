/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.algorithm;

import javax.crypto.spec.SecretKeySpec;

class KeyWithPath {
    private final SecretKeySpec secretKeySpec;
    private final String path;

    public KeyWithPath(SecretKeySpec secretKeySpec, String path) {
        this.secretKeySpec = secretKeySpec;
        this.path = path;
    }

    public SecretKeySpec getSecretKeySpec() {
        return this.secretKeySpec;
    }

    public String getPath() {
        return this.path;
    }
}

