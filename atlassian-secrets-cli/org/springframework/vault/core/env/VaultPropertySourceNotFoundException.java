/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.env;

import org.springframework.vault.VaultException;

public class VaultPropertySourceNotFoundException
extends VaultException {
    public VaultPropertySourceNotFoundException(String msg) {
        super(msg);
    }

    public VaultPropertySourceNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

