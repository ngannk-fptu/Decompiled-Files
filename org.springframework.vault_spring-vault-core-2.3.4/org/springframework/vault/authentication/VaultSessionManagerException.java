/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.vault.VaultException;

public abstract class VaultSessionManagerException
extends VaultException {
    public VaultSessionManagerException(String msg) {
        super(msg);
    }

    public VaultSessionManagerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

