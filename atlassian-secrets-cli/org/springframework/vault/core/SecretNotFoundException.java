/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import org.springframework.vault.VaultException;

public class SecretNotFoundException
extends VaultException {
    private final String path;

    public SecretNotFoundException(String msg, String path) {
        super(msg);
        this.path = path;
    }

    public SecretNotFoundException(String msg, Throwable cause, String path) {
        super(msg, cause);
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}

