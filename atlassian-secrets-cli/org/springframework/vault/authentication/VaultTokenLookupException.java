/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.vault.VaultException;

public class VaultTokenLookupException
extends VaultException {
    public VaultTokenLookupException(String msg) {
        super(msg);
    }

    public VaultTokenLookupException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

